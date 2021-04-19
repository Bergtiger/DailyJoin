package de.bergtiger.dailyjoin;

import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import de.bergtiger.dailyjoin.bdo.DailyPlayer;
import de.bergtiger.dailyjoin.data.MySQL;
import de.bergtiger.dailyjoin.exception.NoSQLConnectionException;
import de.bergtiger.dailyjoin.exception.UpdatePlayerException;

public class DailyFileToSQL {

	public final String 
			FILE_DIRECTORY		= "plugins/DailyJoin/players", 
			FILE_NAME			= "player.yml",
			// key path
			PLAYER_PATH			= "player",
			PLAYER_PATH_FORMAT	= "player.%s.",
			// data
			NAME				= "name",
			DAYS_CONSECUTIVE	= "days.consecutive",
			DAYS_TOTAL			= "days.total",
			DAYS_OLD_CONSECUTIVE= "day", 
			DAYS_OLD_TOTAL		= "totaldays",
			FIRSTJOIN			= "firstjoin",
			LASTJOIN			= "lastjoin";
	
	/**
	 * load players from file.
	 * @return
	 */
	private List<DailyPlayer> getlist() {
		File file = new File(FILE_DIRECTORY, FILE_NAME);
		if (file.exists()) {
			FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
			List<String> list = cfg.getStringList("uuids");
			List<DailyPlayer> players = new ArrayList<>();
			String path = "";
			for (int i = 0; i < list.size(); i++) {
				path = "player." + list.get(i) + ".";
				DailyPlayer p = new DailyPlayer(cfg.getString(path + "name"), list.get(i), cfg.getInt(path + "day"),
						cfg.getInt(path + "totaldays"), new Timestamp(cfg.getLong(path + "firstjoin")),
						new Timestamp(cfg.getLong(path + "lastjoin")));
				players.add(p);
			}
			// load players with uuid
			cfg.getConfigurationSection("player").getKeys(false).forEach(uuid -> {
				DailyPlayer p = new DailyPlayer();
				String dp = String.format(PLAYER_PATH_FORMAT, uuid);
				// set uuid
				p.setUuid(uuid);
				// set name
				if (cfg.contains(dp + NAME))
					p.setName(cfg.getString(dp + NAME));
				// set days total
				if (cfg.contains(dp + DAYS_TOTAL))
					p.setTotaldays(cfg.getInt(dp + DAYS_TOTAL));
				// set days consecutive
				if (cfg.contains(dp + DAYS_CONSECUTIVE))
					p.setDay(cfg.getInt(dp + DAYS_CONSECUTIVE));
				// set first join
				if (cfg.contains(dp + FIRSTJOIN))
					p.setFirstjoin(new Timestamp(cfg.getLong(dp + FIRSTJOIN)));
				// set last join
				if (cfg.contains(dp + LASTJOIN))
					p.setLastjoin(new Timestamp(cfg.getLong(dp + LASTJOIN)));
				// TODO remove old code
				// set days total
				if (cfg.contains(dp + DAYS_OLD_TOTAL))
					p.setTotaldays(cfg.getInt(dp + DAYS_OLD_TOTAL));
				// set days consecutive
				if (cfg.contains(dp + DAYS_OLD_CONSECUTIVE))
					p.setDay(cfg.getInt(dp + DAYS_OLD_CONSECUTIVE));
				// add players
				players.add(p);
			});
			return players;
		}
		return null;
	}

	private void savelist(List<DailyPlayer> players) {
		File file = new File(FILE_DIRECTORY, FILE_NAME);
		// delte existing file
		if (file.exists()) {
			if(file.delete()) {
				dailyjoin.getDailyLogger().log(Level.INFO, "deleted old player file.");
			} else {
				dailyjoin.getDailyLogger().log(Level.WARNING, "Could not delete old player file.");
			}
		}
		// build new players
		if (players != null && !players.isEmpty()) {
			FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
			String path;
			// add player
			for(DailyPlayer p : players) {
				path = String.format(PLAYER_PATH_FORMAT, p.getUuid());
				cfg.addDefault(path + NAME, p.getName());
				cfg.addDefault(path + DAYS_TOTAL, p.getTotaldays());
				cfg.addDefault(path + DAYS_CONSECUTIVE, p.getDay());
				cfg.addDefault(path + FIRSTJOIN, p.getFirstjoin());
				cfg.addDefault(path + LASTJOIN, p.getLastjoin());
			}
			// save file
			try {
				cfg.options().header("DailyJoin offline player list");
				cfg.options().copyHeader(true);
				cfg.options().copyDefaults(true);
				cfg.save(file);
			} catch (IOException e) {
				dailyjoin.getDailyLogger().log(Level.SEVERE, "savelist: ", e);
			}
		}
	}

	/**
	 * check if ljoin is yesterday to fjoin.
	 * @param fjoin
	 * @param ljoin
	 * @return
	 */
	private boolean yesterday(Timestamp fjoin, Timestamp ljoin) {
		Calendar firstjoin = Calendar.getInstance();
		Calendar lastjoin = Calendar.getInstance();
		firstjoin.setTimeInMillis(fjoin.getTime());
		lastjoin.setTimeInMillis(ljoin.getTime());
		if ((firstjoin.get(Calendar.YEAR) == lastjoin.get(Calendar.YEAR))
				&& (firstjoin.get(Calendar.DAY_OF_YEAR) == (lastjoin.get(Calendar.DAY_OF_YEAR) + 1))) {
			return true;
		} else if ((firstjoin.get(Calendar.YEAR) == (lastjoin.get(Calendar.YEAR) + 1))
				&& (((lastjoin.get(Calendar.MONTH) == 11) && (lastjoin.get(Calendar.DAY_OF_MONTH) == 31))
						&& (firstjoin.get(Calendar.DAY_OF_YEAR) == 1))) {
			return true;
		}
		return false;
	}

	/**
	 * check if fjoin and ljoin are on the same day.
	 * @param fjoin
	 * @param ljoin
	 * @return
	 */
	private boolean today(Timestamp fjoin, Timestamp ljoin) {
		Calendar firstjoin = Calendar.getInstance();
		Calendar lastjoin = Calendar.getInstance();
		firstjoin.setTimeInMillis(fjoin.getTime());
		lastjoin.setTimeInMillis(ljoin.getTime());
		if ((firstjoin.get(Calendar.YEAR) == lastjoin.get(Calendar.YEAR))
				&& (firstjoin.get(Calendar.DAY_OF_YEAR) == lastjoin.get(Calendar.DAY_OF_YEAR))) {
			return true;
		}
		return false;
	}

	/**
	 * update Player in Database
	 * @param p
	 * @return
	 * @throws NoSQLConnectionException 
	 * @throws UpdatePlayerException 
	 */
	private Integer updatePlayer(DailyPlayer p) throws NoSQLConnectionException, UpdatePlayerException {
		if(p != null) {
			if(MySQL.hasConnection()) {
				ResultSet rs = null;
				PreparedStatement st = null;
				try {
					st = MySQL.conn().prepareStatement("INSERT INTO dailyjoin (uuid, name, day, totaldays, firstjoin, lastjoin) VALUES (?,?,?,?,?,?) ON DUPLICATE KEY UPDATE "
							+ "name = VALUES(name), "
							+ "day = VALUES(day), "
							+ "totaldays = VALUES(totaldays), "
							+ "lastjoin = VALUES(lastjoin)", Statement.RETURN_GENERATED_KEYS);
					st.setString(1, p.getUuid());
					st.setString(2, p.getName());
					st.setInt(3, p.getDay());
					st.setInt(4, p.getTotaldays());
					st.setTimestamp(5, p.getFirstjoin());
					st.setTimestamp(6, p.getLastjoin());
					st.executeUpdate();
					rs = st.getGeneratedKeys();
					if(rs.next()) {
						return rs.getInt(1);
					}
				} catch (SQLException e) {
					dailyjoin.getDailyLogger().log(Level.SEVERE, "updatePlayer: " + p, e);
					throw new UpdatePlayerException(p);
				} finally {
					MySQL.closeRessources(rs, st);
				}
			} else {
				throw new NoSQLConnectionException();
			}
		}
		return null;
	}
	
	/**
	 * get Player
	 * @param uuid
	 * @return
	 * @throws NoSQLConnectionException 
	 */
	private DailyPlayer getPlayer(String uuid) throws NoSQLConnectionException {
		if (MySQL.hasConnection()) {
			ResultSet rs = null;
			PreparedStatement st = null;
			try {
				if(uuid.length() > 16)
					st = MySQL.conn().prepareStatement("SELECT * FROM dailyjoin WHERE uuid LIKE ?", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
				else
					st = MySQL.conn().prepareStatement("SELECT * FROM dailyjoin WHERE name LIKE ?", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
				// set uuid
				st.setString(1, uuid);
				// get Query
				rs = st.executeQuery();
				// only one player allowed
				if(rs.next()) {
					DailyPlayer p = new DailyPlayer();
					p.setName(rs.getString("name"));
					p.setUuid(rs.getString("uuid"));
					p.setDay(rs.getInt("day"));
					p.setTotaldays(rs.getInt("totaldays"));
					p.setFirstjoin(rs.getTimestamp("firstjoin"));
					p.setLastjoin(rs.getTimestamp("lastjoin"));
				}
			} catch (SQLException e) {
				dailyjoin.getDailyLogger().log(Level.SEVERE, "getPlayer: " + uuid, e);
			} finally {
				MySQL.closeRessources(rs, st);
			}
		} else {
			throw new NoSQLConnectionException();
		}
		return null;
	}
	
	/**
	 * 
	 */
	public void FileToSQL() {
		File datei = new File(FILE_DIRECTORY, "player.yml");
		if (datei.exists()) {
			List<DailyPlayer> players = getlist();
			if (players != null && !players.isEmpty()) {
				for (int i = 0; i < players.size(); i++) {
					try {
						DailyPlayer pFile = players.get(i);
						DailyPlayer pSQL = getPlayer(pFile.getUuid());
						// check if existing Player
						if (pSQL != null) {
							// existing Player -> merge
							if (today(pFile.getFirstjoin(), pSQL.getLastjoin())) {
								// continues logins day overlapping
								// sum totaldays - 1
								pFile.setTotaldays(pFile.getTotaldays() + pSQL.getTotaldays() - 1);
								// check if days correct ???
								pFile.setDay(pFile.getDay() + pSQL.getDay() - 1);
								// lastlogin = file.lastlogin
							} else
							if (yesterday(pFile.getFirstjoin(), pSQL.getLastjoin())) {
								// continues logins
								// sum totaldays
								pFile.setTotaldays(pFile.getTotaldays() + pSQL.getTotaldays());
								// check if days correct ???
								pFile.setDay(pFile.getDay() + pSQL.getDay());
								// lastlogin = file.lastlogin
							} else {
								// not continues logins
								// sum totaldays
								pFile.setTotaldays(pFile.getTotaldays() + pSQL.getTotaldays());
								// day = file.day
								// lastlogin = file.lastlogin
							}
						}
						// update or insert Player
						updatePlayer(pFile);
						// remove from working list
						players.remove(i);
						i--;
					} catch (NoSQLConnectionException e) {
						// NoSQLException
						// stop merge and save file
						savelist(players);
						MySQL.noConnection();
						return;
					} catch (UpdatePlayerException e) {
						dailyjoin.getDailyLogger().log(Level.SEVERE, "FileToSQL", e);
					}
				}
			} else {
				// EmptyList
				dailyjoin.getDailyLogger().log(Level.INFO, "Empty Offline List");
			}
		} else {
			// No File
			dailyjoin.getDailyLogger().log(Level.INFO, "No Offline List");
		}
	}
}

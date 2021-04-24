package de.bergtiger.dailyjoin.dao.impl.file;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import de.bergtiger.dailyjoin.dailyjoin;
import de.bergtiger.dailyjoin.exception.LoadPlayerException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import de.bergtiger.dailyjoin.bdo.DailyPlayer;
import de.bergtiger.dailyjoin.exception.SavePlayerException;

public class DailyFile {

	public static final String 
			FILE_DIRECTORY = "plugins/DailyJoin/players", FILE_NAME = "player.yml",
			// key path
			PLAYER_PATH = "player", PLAYER_PATH_SHORT = "player.%s", PLAYER_PATH_FORMAT = "player.%s.",
			// data
			NAME = "name", DAYS_CONSECUTIVE = "days.consecutive", DAYS_TOTAL = "days.total",
			DAYS_OLD_CONSECUTIVE = "day", DAYS_OLD_TOTAL = "totaldays", FIRSTJOIN = "firstjoin", LASTJOIN = "lastjoin";

	/**
	 * Save new Player
	 * 
	 * @param p Player to save
	 * @throws SavePlayerException
	 */
	public void save(Player p) throws SavePlayerException {
		save(new DailyPlayer(p.getName(), p.getUniqueId().toString()));
	}

	/**
	 * Save DailyPlayer
	 * 
	 * @param dp DailyPlayer to save
	 * @throws SavePlayerException
	 */
	public void save(DailyPlayer dp) throws SavePlayerException {
		// load file
		File file = new File(FILE_DIRECTORY, FILE_NAME);
		FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
		// save player
		Timestamp t = new Timestamp(Calendar.getInstance().getTime().getTime());
		String path = String.format(PLAYER_PATH_FORMAT, dp.getUuid());
		// name
		if (cfg.contains(path + NAME))
			cfg.set(path + NAME, dp.getName());
		else
			cfg.addDefault(path + NAME, dp.getName());
		// day
		if (cfg.contains(path + DAYS_CONSECUTIVE))
			cfg.set(path + DAYS_CONSECUTIVE, dp.getDaysConsecutive());
		else
			cfg.addDefault(path + DAYS_CONSECUTIVE, dp.getDaysConsecutive());
		// totaldays
		if (cfg.contains(path + DAYS_TOTAL))
			cfg.set(path + DAYS_TOTAL, dp.getDaysTotal());
		else
			cfg.addDefault(path + DAYS_TOTAL, dp.getDaysTotal());
		// firstjoin
		if (!cfg.contains(path + FIRSTJOIN))
			cfg.set(path + FIRSTJOIN, t.getTime());
		// lastjoin
		if (cfg.contains(path + LASTJOIN))
			cfg.set(path + LASTJOIN, t.getTime());
		else
			cfg.addDefault(path + LASTJOIN, t.getTime());
		//
		cfg.options().copyDefaults(true);
		try {
			// save file
			cfg.save(file);
			dailyjoin.getDailyLogger().log(Level.INFO, "Save File");
		} catch (IOException e) {
			dailyjoin.getDailyLogger().log(Level.INFO, "Error on save file");
			throw new SavePlayerException(false, dp);
		}
	}

	/**
	 * load Player
	 * 
	 * @param p Player to load
	 * @return DailyPlayer from p
	 */
	public DailyPlayer load(Player p) {
		return p != null ? load(p.getUniqueId().toString()) : null;
	}

	/**
	 * load Player
	 * 
	 * @param uuid UUid from player
	 * @return DailyPlayer from p
	 */
	public DailyPlayer load(String uuid) {
		if (uuid != null) {
			// load file
			File file = new File(FILE_DIRECTORY, FILE_NAME);
			if (file.exists()) {
				FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
				// file has player
				if (cfg.contains(String.format(PLAYER_PATH_SHORT, uuid))) {
					String path = String.format(PLAYER_PATH_FORMAT, uuid);
					DailyPlayer dp = new DailyPlayer();
					// set UUid
					dp.setUuid(uuid);
					// set name
					if (cfg.contains(path + NAME))
						dp.setName(cfg.getString(path + NAME));
					// set days total
					if (cfg.contains(path + DAYS_TOTAL))
						dp.setDaysTotal(cfg.getInt(path + DAYS_TOTAL));
					// set days consecutive
					if (cfg.contains(path + DAYS_CONSECUTIVE))
						dp.setDaysConsecutive(cfg.getInt(path + DAYS_CONSECUTIVE));
					// set first join
					if (cfg.contains(path + FIRSTJOIN))
						dp.setFirstjoin(new Timestamp(cfg.getLong(path + FIRSTJOIN)));
					// set last join
					if (cfg.contains(path + LASTJOIN))
						dp.setLastjoin(new Timestamp(cfg.getLong(path + LASTJOIN)));
					// TODO remove old code
					// set days total
					if (cfg.contains(path + DAYS_OLD_TOTAL))
						dp.setDaysTotal(cfg.getInt(path + DAYS_OLD_TOTAL));
					// set days consecutive
					if (cfg.contains(path + DAYS_OLD_CONSECUTIVE))
						dp.setDaysConsecutive(cfg.getInt(path + DAYS_OLD_CONSECUTIVE));
					return dp;
				}
			}
		}
		return null;
	}

	/**
	 * load Player from file.
	 * 
	 * @return List of all DailyPlayer in file
	 */
	public static List<DailyPlayer> loadAll() {
		File file = new File(DailyFile.FILE_DIRECTORY, DailyFile.FILE_NAME);
		if (file.exists()) {
			FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
			List<DailyPlayer> players = new ArrayList<>();
			// TODO remove old
			/*
			 * List<String> list = cfg.getStringList("uuids"); String paths = ""; for (int i
			 * = 0; i < list.size(); i++) { paths = "player." + list.get(i) + ".";
			 * DailyPlayer p = new DailyPlayer(cfg.getString(paths + "name"), list.get(i),
			 * cfg.getInt(paths + "day"), cfg.getInt(paths + "totaldays"), new
			 * Timestamp(cfg.getLong(paths + "firstjoin")), new Timestamp(cfg.getLong(paths
			 * + "lastjoin"))); players.add(p); }
			 */
			// load players with uuid
			if (cfg.contains(PLAYER_PATH)) {
				cfg.getConfigurationSection(PLAYER_PATH).getKeys(false).forEach(uuid -> {
					DailyPlayer p = new DailyPlayer();
					String path = String.format(PLAYER_PATH_FORMAT, uuid);
					// set uuid
					p.setUuid(uuid);
					// set name
					if (cfg.contains(path + NAME))
						p.setName(cfg.getString(path + NAME));
					// set days total
					if (cfg.contains(path + DAYS_TOTAL))
						p.setDaysTotal(cfg.getInt(path + DAYS_TOTAL));
					// set days consecutive
					if (cfg.contains(path + DAYS_CONSECUTIVE))
						p.setDaysConsecutive(cfg.getInt(path + DAYS_CONSECUTIVE));
					// set first join
					if (cfg.contains(path + FIRSTJOIN))
						p.setFirstjoin(new Timestamp(cfg.getLong(path + FIRSTJOIN)));
					// set last join
					if (cfg.contains(path + LASTJOIN))
						p.setLastjoin(new Timestamp(cfg.getLong(path + LASTJOIN)));
					// TODO remove old code
					// set days total
					if (cfg.contains(path + DAYS_OLD_TOTAL))
						p.setDaysTotal(cfg.getInt(path + DAYS_OLD_TOTAL));
					// set days consecutive
					if (cfg.contains(path + DAYS_OLD_CONSECUTIVE))
						p.setDaysConsecutive(cfg.getInt(path + DAYS_OLD_CONSECUTIVE));
					// add players
					players.add(p);
				});
				return players;
			}
		}
		return null;
	}

	/**
	 * Save all Player
	 * 
	 * @param players List of Player to save
	 */
	public static void saveAll(List<DailyPlayer> players) {
		File file = new File(FILE_DIRECTORY, FILE_NAME);
		// delte existing file
		if (file.exists()) {
			if (file.delete()) {
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
			for (DailyPlayer dp : players) {
				path = String.format(PLAYER_PATH_FORMAT, dp.getUuid());
				cfg.addDefault(path + NAME, dp.getName());
				cfg.addDefault(path + DAYS_TOTAL, dp.getDaysTotal());
				cfg.addDefault(path + DAYS_CONSECUTIVE, dp.getDaysConsecutive());
				cfg.addDefault(path + FIRSTJOIN, dp.getFirstjoin());
				cfg.addDefault(path + LASTJOIN, dp.getLastjoin());
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
	 * get uuid from a name
	 * @param name
	 * @return
	 */
	public static String getUUid(String name) {
		File file = new File(FILE_DIRECTORY, FILE_NAME);
		// check if file exists
		if(file.exists()) {
			// load cfg
			FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
			// cfg has players
			if(cfg.contains(PLAYER_PATH)) {
				// load uuid keys
				Iterator<String> i = cfg.getConfigurationSection(PLAYER_PATH).getKeys(false).iterator();
				String key, path;
				while(i.hasNext()) {
					key = i.next();
					path = String.format(PLAYER_PATH_FORMAT, key) + NAME;
					// if name path exists and name matches return uuid key
					if(cfg.contains(path) && cfg.getString(path).equalsIgnoreCase(name)) {
						return key;
					}
				}
			}
		}
		return null;
	}
}

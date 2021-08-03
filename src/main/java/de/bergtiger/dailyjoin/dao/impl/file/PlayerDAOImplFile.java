package de.bergtiger.dailyjoin.dao.impl.file;

import de.bergtiger.dailyjoin.bdo.DailyPlayer;
import de.bergtiger.dailyjoin.bdo.TigerList;
import de.bergtiger.dailyjoin.DailyJoin;
import de.bergtiger.dailyjoin.dao.DailyDataBase;
import de.bergtiger.dailyjoin.dao.PlayerDAO;
import de.bergtiger.dailyjoin.exception.UpdatePlayerException;
import de.bergtiger.dailyjoin.utils.TigerLogger;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class PlayerDAOImplFile implements PlayerDAO {
	/** file directory path*/
	public static final String
			FILE_DIRECTORY = "plugins/DailyJoin/players",
			/** player file name*/
			FILE_NAME = "player.yml",
			// key path
			/** player key path*/
			PLAYER_PATH = "player",
			/** player key path short for checking player with uuid*/
			PLAYER_PATH_SHORT = "player.%s",
			/** player key path long to get or check player value*/
			PLAYER_PATH_FORMAT = "player.%s.",
			// data
			/** player value name*/
			NAME = "name",
			/** player value days total*/
			DAYS_TOTAL = "days.total",
			/** player value days consecutive*/
			DAYS_CONSECUTIVE = "days.consecutive",
			/** old player value days total*/
			DAYS_OLD_TOTAL = "totaldays",
			/** old player value days consecutive*/
			DAYS_OLD_CONSECUTIVE = "day",
			/** player value first join date*/
			FIRSTJOIN = "firstjoin",
			/** player value last join date*/
			LASTJOIN = "lastjoin";

	/**
	 * used DateTimeFormat in file (ISO_DATE_TIME)
	 */
	private static final DateTimeFormatter DTF = DateTimeFormatter.ISO_DATE_TIME;

	@Override
	public Integer updatePlayer(DailyPlayer dp) throws UpdatePlayerException {
		if (dp != null) {
			// load file
			File file = new File(FILE_DIRECTORY, FILE_NAME);
			FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
			// save player
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
				cfg.set(path + FIRSTJOIN, DTF.format(dp.getFirstjoin() != null ? dp.getFirstjoin().toLocalDateTime() : LocalDateTime.now()));
			// lastjoin
			if (cfg.contains(path + LASTJOIN))
				cfg.set(path + LASTJOIN, DTF.format(dp.getLastjoin() != null ? dp.getLastjoin().toLocalDateTime() : LocalDateTime.now()));
			else
				cfg.addDefault(path + LASTJOIN, DTF.format(dp.getLastjoin() != null ? dp.getLastjoin().toLocalDateTime() : LocalDateTime.now()));
			//
			cfg.options().copyDefaults(true);
			try {
				// save file
				cfg.save(file);
				TigerLogger.log(Level.INFO, "Save File");
			} catch (IOException e) {
				TigerLogger.log(Level.INFO, "Error on save file");
				throw new UpdatePlayerException(false, dp);
			}
		}
		return null;
	}

	@Override
	public void updatePlayers(List<DailyPlayer> players) throws UpdatePlayerException {
		if (players != null && !players.isEmpty()) {
			// load file
			File file = new File(FILE_DIRECTORY, FILE_NAME);
			FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
			// save player
			for (DailyPlayer dp : players) {
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
					cfg.set(path + FIRSTJOIN, DTF.format(dp.getFirstjoin() != null ? dp.getFirstjoin().toLocalDateTime() : LocalDateTime.now()));
				// lastjoin
				if (cfg.contains(path + LASTJOIN))
					cfg.set(path + LASTJOIN, DTF.format(dp.getLastjoin() != null ? dp.getLastjoin().toLocalDateTime() : LocalDateTime.now()));
				else
					cfg.addDefault(path + LASTJOIN, DTF.format(dp.getLastjoin() != null ? dp.getLastjoin().toLocalDateTime() : LocalDateTime.now()));
			}
			cfg.options().copyDefaults(true);
			try {
				// save file
				cfg.save(file);
				TigerLogger.log(Level.INFO, "Save File");
			} catch (IOException e) {
				TigerLogger.log(Level.INFO, "Error on save file");
				throw new UpdatePlayerException(false, null);
			}
		}
	}

	@Override
	public DailyPlayer getPlayer(String uuid) {
		if (uuid != null) {
			// UUid is a name -> get UUid;
			if (uuid.length() <= 16)
				uuid = getUUid(uuid);
			// load file
			File file = new File(FILE_DIRECTORY, FILE_NAME);
			if (file.exists()) {
				FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
				// file has player uuid
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
						dp.setFirstjoin(Timestamp.valueOf(LocalDateTime.parse(cfg.getString(path + FIRSTJOIN), DTF)));
					// set last join
					if (cfg.contains(path + LASTJOIN))
						dp.setLastjoin(Timestamp.valueOf(LocalDateTime.parse(cfg.getString(path + LASTJOIN), DTF)));
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

	@Override
	public List<DailyPlayer> getPlayers() {
		File file = new File(FILE_DIRECTORY, FILE_NAME);
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
						p.setFirstjoin(Timestamp.valueOf(LocalDateTime.parse(cfg.getString(path + FIRSTJOIN), DTF)));
					// set last join
					if (cfg.contains(path + LASTJOIN))
						p.setLastjoin(Timestamp.valueOf(LocalDateTime.parse(cfg.getString(path + LASTJOIN), DTF)));
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

	@Override
	public List<DailyPlayer> getPlayers(String name) {
		File file = new File(FILE_DIRECTORY, FILE_NAME);
		if (file.exists()) {
			FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
			List<DailyPlayer> players = new ArrayList<>();
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
					// check if name equals
					if (p.getName().equalsIgnoreCase(name)) {
						// set days total
						if (cfg.contains(path + DAYS_TOTAL))
							p.setDaysTotal(cfg.getInt(path + DAYS_TOTAL));
						// set days consecutive
						if (cfg.contains(path + DAYS_CONSECUTIVE))
							p.setDaysConsecutive(cfg.getInt(path + DAYS_CONSECUTIVE));
						// set first join
						if (cfg.contains(path + FIRSTJOIN))
							p.setFirstjoin(
									Timestamp.valueOf(LocalDateTime.parse(cfg.getString(path + FIRSTJOIN), DTF)));
						// set last join
						if (cfg.contains(path + LASTJOIN))
							p.setLastjoin(Timestamp.valueOf(LocalDateTime.parse(cfg.getString(path + LASTJOIN), DTF)));
						// TODO remove old code
						// set days total
						if (cfg.contains(path + DAYS_OLD_TOTAL))
							p.setDaysTotal(cfg.getInt(path + DAYS_OLD_TOTAL));
						// set days consecutive
						if (cfg.contains(path + DAYS_OLD_CONSECUTIVE))
							p.setDaysConsecutive(cfg.getInt(path + DAYS_OLD_CONSECUTIVE));
						// add players
						players.add(p);
					}
				});
				return players;
			}
		}
		return null;
	}

	@Override
	public HashMap<String, DailyPlayer> getPlayersAsMap() {
		File file = new File(FILE_DIRECTORY, FILE_NAME);
		if (file.exists()) {
			FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
			HashMap<String, DailyPlayer> players = new HashMap<>();
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
						p.setFirstjoin(Timestamp.valueOf(LocalDateTime.parse(cfg.getString(path + FIRSTJOIN), DTF)));
					// set last join
					if (cfg.contains(path + LASTJOIN))
						p.setLastjoin(Timestamp.valueOf(LocalDateTime.parse(cfg.getString(path + LASTJOIN), DTF)));
					// TODO remove old code
					// set days total
					if (cfg.contains(path + DAYS_OLD_TOTAL))
						p.setDaysTotal(cfg.getInt(path + DAYS_OLD_TOTAL));
					// set days consecutive
					if (cfg.contains(path + DAYS_OLD_CONSECUTIVE))
						p.setDaysConsecutive(cfg.getInt(path + DAYS_OLD_CONSECUTIVE));
					// add players
					players.put(uuid, p);
				});
				return players;
			}
		}
		return null;
	}

	@Override
	public TigerList<DailyPlayer> getOrderedPlayers(String column, String richtung) {
		File file = new File(FILE_DIRECTORY, FILE_NAME);
		if (file.exists()) {
			FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
			if (cfg.contains(PLAYER_PATH)) {
				return new TigerList<>(cfg.getConfigurationSection(PLAYER_PATH).getKeys(false).stream().map(k -> {
					String path = String.format(PLAYER_PATH_FORMAT, k);
					DailyPlayer dp = new DailyPlayer();
					// set UUid
					dp.setUuid(k);
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
						dp.setFirstjoin(Timestamp.valueOf(LocalDateTime.parse(cfg.getString(path + FIRSTJOIN), DTF)));
					// set last join
					if (cfg.contains(path + LASTJOIN))
						dp.setLastjoin(Timestamp.valueOf(LocalDateTime.parse(cfg.getString(path + LASTJOIN), DTF)));
					// TODO remove old code
					// set days total
					if (cfg.contains(path + DAYS_OLD_TOTAL))
						dp.setDaysTotal(cfg.getInt(path + DAYS_OLD_TOTAL));
					// set days consecutive
					if (cfg.contains(path + DAYS_OLD_CONSECUTIVE))
						dp.setDaysConsecutive(cfg.getInt(path + DAYS_OLD_CONSECUTIVE));
					return dp;
				}).sorted(getComparator(column)).collect(Collectors.toList()));
			}
		}
		return null;
	}

	/**
	 * 
	 * @param column
	 * @return
	 */
	private Comparator<DailyPlayer> getComparator(String column) {
		if (column != null && !column.isEmpty()) {
			switch (column.toLowerCase()) {
			case DailyDataBase.UUID:
				return Comparator.comparing(DailyPlayer::getUuid);
			case DailyDataBase.NAME:
				return Comparator.comparing(DailyPlayer::getName);
			case DailyDataBase.LASTJOIN:
				return Comparator.comparing(DailyPlayer::getLastjoin);
			case DailyDataBase.FIRSTJOIN:
				return Comparator.comparing(DailyPlayer::getFirstjoin);
			case DailyDataBase.DAYS_TOTAL:
				return Comparator.comparing(DailyPlayer::getDaysTotal);
			case DailyDataBase.DAYS_CONSECUTIVE:
				return Comparator.comparing(DailyPlayer::getDaysConsecutive);
			}
		}
		return Comparator.comparing(DailyPlayer::getName);
	}

	@Override
	public List<String> getNames(String args) {
		File file = new File(FILE_DIRECTORY, FILE_NAME);
		if (file.exists()) {
			FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
			if (cfg.contains(PLAYER_PATH)) {
				// '^player\.\S+\.name$'
				return cfg.getKeys(true).stream()
						.filter(k -> k.matches(String.format("^%s\\.\\S+\\.%s$", PLAYER_PATH, NAME)))
						.map(k -> cfg.getString(k)).filter(n -> n.startsWith(args)).collect(Collectors.toList());
			}
		}
		return null;
	}

	@Override
	public String getUUid(String name) {
		File file = new File(FILE_DIRECTORY, FILE_NAME);
		if (file.exists()) {
			FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
			if (cfg.contains(PLAYER_PATH)) {
				try {
				// '^player\.\S+\.name$'
					return cfg.getKeys(true).stream()
						.filter(k -> k.matches(String.format("^%s\\.\\S+\\.%s$", PLAYER_PATH, NAME)))
						.filter(k -> Objects.requireNonNull(cfg.getString(k)).equalsIgnoreCase(name))
						.map(n -> n.substring(PLAYER_PATH.length() + 1, n.length() - (NAME.length() + 1))).findFirst()
						.get();
				} catch (Exception e) {
					TigerLogger.log(Level.INFO, String.format("No such name in player file (%s)", name));
				}
			}
		}
		return null;
	}
}

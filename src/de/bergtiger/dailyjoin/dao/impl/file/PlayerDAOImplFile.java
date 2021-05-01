package de.bergtiger.dailyjoin.dao.impl.file;

import de.bergtiger.dailyjoin.bdo.DailyPlayer;
import de.bergtiger.dailyjoin.bdo.TigerList;
import de.bergtiger.dailyjoin.dailyjoin;
import de.bergtiger.dailyjoin.dao.PlayerDAO;
import de.bergtiger.dailyjoin.exception.UpdatePlayerException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import static de.bergtiger.dailyjoin.dao.impl.file.DailyFile.*;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class PlayerDAOImplFile implements PlayerDAO {
	
	@Override
	public Integer updatePlayer(DailyPlayer dp) throws UpdatePlayerException {
		if (dp != null) {
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
				throw new UpdatePlayerException(false, dp);
			}
		}
		return null;
	}

	@Override
	public DailyPlayer getPlayer(String uuid) {
		if (uuid != null) {
			// UUid is a name -> get UUid;
			if(uuid.length() <= 16)
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

	@Override
	public TigerList<DailyPlayer> getOrderedPlayers(String column, String richtung) {
		File file = new File(FILE_DIRECTORY, FILE_NAME);
		if (file.exists()) {
			FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
			if (cfg.contains(PLAYER_PATH)) {
				return new TigerList<>(cfg.getConfigurationSection(PLAYER_PATH).getKeys(false).stream().map(k -> {
					String uuid = k.substring(PLAYER_PATH.length() + 1);
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
				}).sorted(Comparator.comparing(DailyPlayer::getDaysTotal)).collect(Collectors.toList()));
			}
		}
		return null;
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
						.map(k -> cfg.getString(k))
						.filter(n -> n.startsWith(args)).collect(Collectors.toList());
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
				// '^player\.\S+\.name$'
				return cfg.getKeys(true).stream()
						.filter(k -> k.matches(String.format("^%s\\.\\S+\\.%s$", PLAYER_PATH, NAME)))
						.filter(k -> Objects.requireNonNull(cfg.getString(k)).equalsIgnoreCase(name))
						.map(n -> n.substring(PLAYER_PATH.length() + 1, n.length() - (NAME.length() + 1)))
						.findFirst().get();
			}
		}
		return null;
	}
}

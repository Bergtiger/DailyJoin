package de.bergtiger.dailyjoin.utils.config;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;

import de.bergtiger.dailyjoin.dailyjoin;
import de.bergtiger.dailyjoin.utils.lang.Lang;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class DailyConfig {
	
	public static final String 
		DATA_SQL = "sql",
		DATA_FILE = "file",
	
		DB		= "database",
		HOST	= DB + ".host",
		PORT	= DB + ".port",
		USER	= DB + ".user",
		PASSWORD= DB + ".password",
		DATABASE= DB + ".database",
		
		CONFIG					= "config",
		DATA_FORMAT				= CONFIG + ".data.format",
		DATA_FORMAT_SQL			= CONFIG + ".data.format.sql",
		DATA_FORMAT_OLD			= CONFIG + ".SQL",
		DELAY					= CONFIG + ".delay",
		PAGE_SIZE				= CONFIG + ".page.size",
		DAILY					= CONFIG + ".daily",
		BIRTHDAY				= CONFIG + ".birthday",
		FILE_DAYS_TOTAL			= CONFIG + ".file.days.total",
		FILE_DAYS_CONSECUTIVE	= CONFIG + ".file.days.consecutive",
		FILE_DAYS_OLD_TOTAL			= CONFIG + ".FileTotalDays",
		FILE_DAYS_OLD_CONSECUTIVE	= CONFIG + ".FileDay",
		REWARD_ON_SQL_CONNECTION_LOST = CONFIG + "GetRewardOnSQLConnectionLost";
	
	private static DailyConfig instance;
	
	public static DailyConfig inst() {
		if(instance == null)
			instance = new DailyConfig();
		return instance;
	}
	
	private DailyConfig() {}
	
	public static void load(){
		DailyConfig dc = inst();
		dc.handleConfiguration();
		dc.handleLanguage();
		//TODO
//		dc.setFileDay(dailyjoin.inst().getConfig().getString("config.FileDay"));
//		dc.setFileTotalDays(dailyjoin.inst().getConfig().getString("config.FileTotalDays"));
	}
	
	/**
	 * 
	 */
	private void handleConfiguration() {
		// empty cache
		values.clear();
		// config
		FileConfiguration cfg = dailyjoin.inst().getConfig();
		// delay
		if(cfg.contains(DELAY)) {
			// check matching integer (\\d*)
			if(!cfg.getString(DELAY).matches("\\d*")) {
				dailyjoin.getDailyLogger().log(Level.WARNING, "delay no integer");
				cfg.set(DELAY, 30);
			}
		} else {
			cfg.addDefault(DELAY, 30);
		}
		// page_size
		if(cfg.contains(PAGE_SIZE)) {
			// check matching integer (\\d*)
			if(!cfg.getString(PAGE_SIZE).matches("\\d*")) {
				dailyjoin.getDailyLogger().log(Level.WARNING, "page_size no integer");
				cfg.set(PAGE_SIZE, 15);
			}
		} else {
			cfg.addDefault(PAGE_SIZE, 15);
		}
		// file_days_total(matches: *.yml - "^.+\\.yml$")
		if(cfg.contains(FILE_DAYS_TOTAL)) {
			// check matching yaml (^.+\\.yml$)
			if(!cfg.getString(FILE_DAYS_TOTAL).matches(".+\\.yml")) {
				dailyjoin.getDailyLogger().log(Level.WARNING, "File_Days_total no yaml file");
				cfg.set(FILE_DAYS_TOTAL, "TotalDays.yml");
			}
		} else {
			cfg.addDefault(FILE_DAYS_TOTAL, "TotalDays.yml");
		}
		// file_days_consecutive(matches: *.yml - "^.+\\.yml$")
		if(cfg.contains(FILE_DAYS_CONSECUTIVE)) {
			// check matching yaml (^.+\\.yml$)
			if(!cfg.getString(FILE_DAYS_CONSECUTIVE).matches(".+\\.yml")) {
				dailyjoin.getDailyLogger().log(Level.WARNING, "File_Days_consecutive no yaml file");
				cfg.set(FILE_DAYS_CONSECUTIVE, "Day.yml");
			}
		} else {
			cfg.addDefault(FILE_DAYS_CONSECUTIVE, "Day.yml");
		}
		// check if total and consecutive files are equal -> warning
		if(cfg.getString(FILE_DAYS_TOTAL).equals(cfg.getString(FILE_DAYS_CONSECUTIVE))) {
			dailyjoin.getDailyLogger().log(Level.WARNING, "days total and days consecutive share same file");
		}

		// data format (SQL/File)
		if(cfg.contains(DATA_FORMAT)) {
			// check matching sql|file
			if(!cfg.getString(DATA_FORMAT).matches(String.format("(?i)(%s|%s)", DATA_SQL, DATA_FILE))) {
				// no allowed data format
				dailyjoin.getDailyLogger().log(Level.SEVERE, "data format has to be file or sql");
				cfg.set(DATA_FORMAT, DATA_FILE);
			} else {
				// load
				// set DATA_FORMAT_SQL (boolean)
				values.put(DATA_FORMAT_SQL, Boolean.toString(cfg.getString(DATA_FORMAT).equalsIgnoreCase(DATA_SQL)));
				// check if sql configuration is needed
				if(values.get(DATA_FORMAT_SQL).equalsIgnoreCase("true")) {
					// if sql check sql configuration
					// reward on sql connection lost
					if(cfg.contains(REWARD_ON_SQL_CONNECTION_LOST)) {
						// check matching true|false
						if(!cfg.getString(REWARD_ON_SQL_CONNECTION_LOST).matches("(?i)(true|false)")) {
							// not allowed value
							dailyjoin.getDailyLogger().log(Level.WARNING, "reward on sql connection lost has to be true or false");
							cfg.set(REWARD_ON_SQL_CONNECTION_LOST, true);
						}
					} else {
						cfg.addDefault(REWARD_ON_SQL_CONNECTION_LOST, true);
					}
					// database
					if(!cfg.contains(DATABASE)) {
						cfg.addDefault(DATABASE, "database");
					}
					// host
					if(!cfg.contains(HOST)) {
						cfg.addDefault(HOST, "localhost");
					}
					// port
					if(!cfg.contains(PORT)) {
						cfg.addDefault(PORT, 3306);
					}
					// user
					if(!cfg.contains(USER)) {
						cfg.addDefault(USER, "user");
					}
					// password
					if(!cfg.contains(PASSWORD)) {
						cfg.addDefault(PASSWORD, "password");
					}
				}
			}
		} else {
			cfg.addDefault(DATA_FORMAT, DATA_FILE);
		}
		// save
		cfg.options().header("DailyJoin");
		cfg.options().copyHeader(true);
		cfg.options().copyDefaults(true);
		dailyjoin.inst().saveConfig();
	}
	
	//name = plugin.getConfig().getString("config.FileDay")
	private void setFileDay(String name){
		File file = new File("plugins/DailyJoin/", name);
		if(!file.exists()){
			try {
				FileWriter w = new FileWriter(file);
				w.write("#DailyJoin - Day Config\n");
				w.write("#Exampel:\n");
				w.write("#'10':\n");
				w.write("#  - give -player- minecraft:diamond_shovel 1\n");
				w.write("#'20':\n");
				w.write("#  - gamemode 1 -player-\n");
				
				w.flush();
				w.close();
			} catch (IOException e) {
				System.out.println("Error Create File: " + name);
			}
		}
	}
	
	//name = cfg.getString("config.FileTotalDays")
	private void setFileTotalDays(String name){
		File file = new File("plugins/DailyJoin/", name);
		if(!file.exists()){
			try{
				FileWriter w = new FileWriter(file);
				w.write("#DailyJoin - TotalDays Config\n");
				w.write("#Exampel:\n");
				w.write("#'2':\n");
				w.write("#- give -player- wooden_shovel 1\n");
				w.write("#- give -player- minecraft:wooden_axe 1\n");
				w.write("#'4':\n");
				w.write("#- give -player- wooden_pikeaxe 1\n");
				
				w.flush();
				w.close();
			} catch (IOException e){
				System.out.println("Error Create File: " + name);
			}
		}
	}

	/**
	 * load and save language file.
	 */
	private void handleLanguage() {
		try {
			// language file
			File file = new File("plugins/DailyJoin", "lang.yml");
			YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
			// path for each enum
			String path;
			// go threw each enum
			for(Lang l : Lang.values()) {
				path = l.name().replaceAll("_", ".");
				// if enum exists load, else save
				if(cfg.contains(path))
					l.set(cfg.getString(path));
				else
					cfg.addDefault(path, l.get());
			}
			// options
			cfg.options().header("Language file for DailyJoin");
			cfg.options().copyHeader(true);
			cfg.options().copyDefaults(true);
			// save file
			cfg.save(file);
		} catch (IOException e) {
			dailyjoin.getDailyLogger().log(Level.SEVERE, "´handleLanguage: could not save language file", e);
		}
	}
	
	private HashMap<String, String> values = new HashMap<>();
	
	/**
	 * check if configuration has a value.
	 * @param key check
	 * @return
	 */
	public boolean hasValue(String key) {
		if(values.containsKey(key)) {
			return true;
		}
		if(dailyjoin.inst().getConfig().contains(key)) {
			return true;
		}
		return false;
	}
	
	/**
	 * get string value from configuration.
	 * @param key
	 * @return 
	 */
	public String getValue(String key) {
		if(!values.containsKey(key)) {
			values.put(key, dailyjoin.inst().getConfig().getString(key));
		}
		return values.get(key);
	}
	
	/**
	 * get boolean value from configuration.
	 * @param key
	 * @return 
	 */
	public boolean getBoolean(String key) {
		return Boolean.valueOf(getValue(key));
	}
	
	/**
	 * get integer value from configuration.
	 * @param key
	 * @return 
	 */
	public Integer getInteger(String key) {
		try {
			return Integer.valueOf(getValue(key));
		} catch (NumberFormatException e) {
			dailyjoin.getDailyLogger().log(Level.SEVERE, String.format("getInteger(%s) got an exception please check your configuration.", key), e);
		}
		return null;
	}
}

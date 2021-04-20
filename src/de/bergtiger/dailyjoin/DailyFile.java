package de.bergtiger.dailyjoin;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import de.bergtiger.dailyjoin.dao.TigerConnection;
import de.bergtiger.dailyjoin.data.MyUtils;

public class DailyFile implements MyUtils{
	
	public static final String NAME = "name", DAY = "day", TOTALDAYS = "totaldays", FIRSTJOIN = "firstjoin", LASTJOIN = "lastjoin";
	public static final String FILE_DIRECTORY = "plugins/DailyJoin/players", FILE_NAME = "player.yml";
	private boolean reward;
	private boolean sql;
	
	public DailyFile(){
		this.setData();
	}
	
	/**
	 * set configuration data. (rewardOnSQLConnectionLost and saveAsSQL)
	 */
	private void setData(){
		sql = dailyjoin.inst().getConfig().getString("config.SQL").equalsIgnoreCase("true");
		reward = dailyjoin.inst().getConfig().getString("config.GetRewardOnSQLConnectionLost").equalsIgnoreCase("true");
	}
	
	/**
	 * File save
	 * @param p
	 */
	@Deprecated
	public void dailyjoin_file(Player p){
		File datei = new File(FILE_DIRECTORY, FILE_NAME);
		FileConfiguration cfg = YamlConfiguration.loadConfiguration(datei);
		if(datei.exists()){
			//datei existiert
			Set<String> keys = null;
			try {
				keys = cfg.getConfigurationSection("player").getKeys(false);
			} catch (Exception e) {
			}
			if((keys == null) || ((keys != null) && (keys.size() == 0))){
				//keys sind leer
				//new player
				save(datei, cfg, p);
			} else {
				//suche in keys
				String player = p.getUniqueId().toString();
				if(keys.contains(player)){
					//old player
					player = "player." + player + "."; //umwandeln von nur uuid auf cfg
					if(!today(new Timestamp(cfg.getLong(player + "lastjoin")))){
						int day = cfg.getInt(player + "day");
						int totaldays = cfg.getInt(player + "totaldays") + 1;
						if(yesterday(new Timestamp(cfg.getLong(player + "lastjoin")))){
							day++;
						} else {
							day = 1;
						}
						save(datei, cfg, p, day, totaldays);
					}
				} else {
					//new player
					save(datei, cfg, p);
				}
			}
		} else {
			//neue datei erstellen
			save(datei, cfg, p);
		}
	}
	
	/**
	 * Save new Player
	 * @param file saveFile
	 * @param cfg loaded FileConfiguration
	 * @param p Player to save
	 */
	public void save(File file, FileConfiguration cfg, Player p) {
		save(file, cfg, p, 1, 1);
	}
	
	/**
	 * Save Player
	 * @param file saveFile
	 * @param cfg loaded FileConfiguration
	 * @param p Player to save
	 * @param day player
	 * @param totaldays player totaldays
	 */
	public void save(File file, FileConfiguration cfg, Player p, int day, int totaldays) {
		Timestamp t = new Timestamp(Calendar.getInstance().getTime().getTime());
		String player = "player." + p.getUniqueId().toString() + ".";
		// name
		if(cfg.contains(player + NAME))
			cfg.set(player + NAME, p.getName());
		else
			cfg.addDefault(player + NAME, p.getName());
		// day
		if(cfg.contains(player + DAY))
			cfg.set(player + DAY, day);
		else
			cfg.addDefault(player + DAY, day);
		// totaldays
		if(cfg.contains(player + TOTALDAYS))
			cfg.set(player + TOTALDAYS, totaldays);
		else
			cfg.addDefault(player + TOTALDAYS, totaldays);
		// firstjoin
		if(!cfg.contains(player + FIRSTJOIN))
			cfg.set(player + FIRSTJOIN, t.getTime());
		// lastjoin
		if(cfg.contains(player + LASTJOIN))
			cfg.set(player + LASTJOIN, t.getTime());
		else
			cfg.addDefault(player + LASTJOIN, t.getTime());
		//
		cfg.options().copyDefaults(true);
		try{
			cfg.save(file);
			if((!sql) || ((!TigerConnection.hasConnection()) && reward)){
				dailyjoin.inst().getDailyReward().giveReward(p, day, totaldays, t);
			}
			dailyjoin.getDailyLogger().log(Level.INFO, "Save File");
		} catch (IOException e){
			dailyjoin.getDailyLogger().log(Level.INFO, "Error on save file");
		}
	}
	
	/**
	 * reload configuration
	 */
	public void reload(){
		setData();
	}
}

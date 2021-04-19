package de.bergtiger.dailyjoin;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Set;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import de.bergtiger.dailyjoin.data.MyUtils;

public class DailyFile implements MyUtils{
	
	private dailyjoin plugin;
	private String file_player = "plugins/DailyJoin/players";
	private boolean reward;
	private boolean sql;
	
	public DailyFile(dailyjoin plugin){
		this.plugin = plugin;
		this.SetData();
	}
	
	private void SetData(){
		if(this.plugin.getConfig().getString("config.SQL").equalsIgnoreCase("true")){
			this.sql = true;
		} else {
			this.sql = false;
		}
		if(this.plugin.getConfig().getString("config.GetRewardOnSQLConnectionLost").equalsIgnoreCase("true")){
			this.reward = true;
		} else {
			this.reward = false;
		}
	}
	
	public void dailyjoin_file(Player p){
		File datei = new File(this.file_player, "player.yml");
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
				this.save_new(datei, cfg, p);
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
						this.save_old(datei, cfg, p, day, totaldays);
					}
				} else {
					//new player
					this.save_new(datei, cfg, p);
				}
			}
		} else {
			//neue datei erstellen
			this.save_new(datei, cfg, p);
		}
	}
	
	public void save_new(File file, FileConfiguration cfg, Player p){
		Timestamp t = new Timestamp(Calendar.getInstance().getTime().getTime());
		String player = "player." + p.getUniqueId().toString() + ".";
		cfg.addDefault(player + "name", p.getName());
		cfg.addDefault(player + "day", 1);
		cfg.addDefault(player + "totaldays", 1);
		cfg.addDefault(player + "firstjoin", t.getTime());
		cfg.addDefault(player + "lastjoin", t.getTime());
		
		cfg.options().copyDefaults(true);
		try{
			cfg.save(file);
			if((!this.sql) || ((!this.plugin.getMySQL().hasConnection()) && this.reward)){
				this.plugin.getDailyReward().setReward(p, 1, 1, t);
			}
			System.out.println("Save File");
		} catch (IOException e){
			System.out.println("Error on save file");
		}
	}
	
	public void save_old(File file, FileConfiguration cfg, Player p, int day, int totaldays){
		Timestamp t = new Timestamp(Calendar.getInstance().getTime().getTime());
		String player = "player." + p.getUniqueId().toString() + ".";
		cfg.set(player + "name", p.getName());
		cfg.set(player + "day", day);
		cfg.set(player + "totaldays", totaldays);
		cfg.set(player + "lastjoin", t.getTime());
		
		cfg.options().copyDefaults(true);
		try{
			cfg.save(file);
			if((!this.sql) || ((!this.plugin.getMySQL().hasConnection()) && this.reward)){
				this.plugin.getDailyReward().setReward(p, day, totaldays, t);
			}
			System.out.println("Save File");
		} catch (IOException e){
			System.out.println("Error on save file");
		}
	}
	
	public void reload(){
		this.plugin.reloadConfig();
	}
}

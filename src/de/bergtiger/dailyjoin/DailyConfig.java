package de.bergtiger.dailyjoin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;

import de.bergtiger.dailyjoin.utils.lang.Lang;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class DailyConfig {
	
	public DailyConfig(dailyjoin plugin){
		this.createConfig(plugin);
		this.checkConfig(plugin);
		this.setFileDay(plugin.getConfig().getString("config.FileDay"));
		this.setFileTotalDays(plugin.getConfig().getString("config.FileTotalDays"));
	}
	
	public void createConfig(dailyjoin plugin){
		FileConfiguration cfg = plugin.getConfig();
		
		String db = "database.";
		cfg.addDefault(db + "host", "localhost");
		cfg.addDefault(db + "port", 3306);
		cfg.addDefault(db + "user", "user");
		cfg.addDefault(db + "password", "password");
		cfg.addDefault(db + "database", "database");
		
		String lang ="lang.";
		cfg.addDefault(lang + "Daily", "&7Du hast dich bereits den &e-day-&7. Tag in Folge eingeloggt.");
		cfg.addDefault(lang + "SpezialDay", "&e-player- &7ist bereits &e-day- &7Tage in Folge auf dem Server.");
		cfg.addDefault(lang + "SpezialTotalDays", "&e-player- &7ist bereits insgesamt &e-day- &7Tage auf dem Server.");
		cfg.addDefault(lang + "Birthday", "&e-player- &7hat heute seinen Server-Geburtstag.");
		
		cfg.addDefault(lang + "DailyInfo", "&b/daily  -  &7zeigt alle g�ltigen Befehle");
		cfg.addDefault(lang + "DailyInfoTop", "&b/daily top [day, totaldays] [value] -  &7zeigt top Liste");
		cfg.addDefault(lang + "DailyInfoSet", "&b/daily set [player, uuid] [day, totaldays] [value]  -  &7Setzt dem Spieler day/totaldays value");
		cfg.addDefault(lang + "DailyInfoAdd", "&b/daily add [player, uuid] [day, totaldays] [value]  -  &7Addiert dem Spieler day/totaldays value");
		cfg.addDefault(lang + "DailyInfoInfo", "&b/daily info  -  &7zeigt Plugin version");
		cfg.addDefault(lang + "DailyInfoReload", "&b/daily reload  -  &7l�dt config neu");
		cfg.addDefault(lang + "DailyInfoPlayer", "&b/daily player [player, uuid]  -  &7zeigt die Daten des Spielers");
		cfg.addDefault(lang + "DailyInfoConfig", "&b/daily config [ma/system/reward/oldfiles/delay] [true/false/in case of delay number]  -  &7�ndert die config");
		
		cfg.addDefault(lang + "EqualNames", "&cError: Equal names: -player-");
		cfg.addDefault(lang + "EqualNamesList", "&7-uuid-");
		cfg.addDefault(lang + "NoPermission", "&cNo Permission");
		cfg.addDefault(lang + "NoConnection", "&7Error: SQL-Connection");
		cfg.addDefault(lang + "NoPlayer", "&7No such player could be found");
		cfg.addDefault(lang + "NoUUID", "&7Not a correct UUID or Player is not online.");
		cfg.addDefault(lang + "NoFile", "&7Could not find Player file.");
		cfg.addDefault(lang + "WrongArgument", "&7Wrong Argument. Please Check your Command.");
		cfg.addDefault(lang + "OnlySQL", "&7This Command works only with SQL.");
		
		cfg.addDefault(lang + "PlayerUmrandungOben", "&a----<[&6-player-&a]>----");
		cfg.addDefault(lang + "PlayerUmrandungUnten", "&a--------------------");
		cfg.addDefault(lang + "PlayerFirstJoin", "&eFirstJoin: &7Day Month Year");
		cfg.addDefault(lang + "PlayerLastJoin", "&eLastJoin: &7Day Month Year");
		cfg.addDefault(lang + "PlayerDay", "&eDay: &7-day-");
		cfg.addDefault(lang + "PlayerTotalDays", "&eTotalDays: &7-day-");
		
		cfg.addDefault(lang + "PluginUmrandungOben", "&a----<[&6DailyJoin&a]>----");
		cfg.addDefault(lang + "PluginUmrandungUnten", "&a---------------------");
		cfg.addDefault(lang + "PluginVersion", "&eVersion: &7-version-");
		cfg.addDefault(lang + "PluginMonatsAnzeige", "&eMonatsAnzeige: &7-status-");
		cfg.addDefault(lang + "PluginSystem", "&eSystem: &7-status-");
		cfg.addDefault(lang + "PluginRewardReconnection", "&eReward on Reconnection: &7-status-");
		cfg.addDefault(lang + "PluginDelay", "&eRewardDelay: &7-delay-");
		cfg.addDefault(lang + "PluginTopPlayer", "&eTopPlayer: &7-amount-");
		
		cfg.addDefault(lang + "DailySet", "&7/daily set [player/uuid] [day, totaldays] [value]");
		cfg.addDefault(lang + "DailyAdd", "&7/daily add [player/uuid] [day, totaldays] [value]");
		cfg.addDefault(lang + "DailySetData", "&aBei dem Spieler mit der UUID: &e-player- &awurde &e-data- &aauf &e-value- &agesetzt.");
		cfg.addDefault(lang + "DailyAddData", "&aBei dem Spieler mit der UUID: &e-player- &awurde &e-data- &aum &e-value- &aerh�ht.");
		cfg.addDefault(lang + "DailyReload", "&7DailyJoin reloaded.");
		cfg.addDefault(lang + "DailyConfig", "&7/daily config [ma/system/reward/oldfiles/delay] [true/false/in case of delay number]");
		cfg.addDefault(lang + "DailySetConfig", "&7DailyJoin reloaded.");
		
		cfg.addDefault(lang + "TopPlayerDay", "&a----<[&6Top Day&a]>----");
		cfg.addDefault(lang + "TopPlayerTotalDays", "&a----<[&6Top Totaldays&a]>----");
		cfg.addDefault(lang + "TopPlayerList", "&e-player-: &7-days-");
		
		String config = "config.";
		cfg.addDefault(config + "MonatsAnzeige", "true");
		cfg.addDefault(config + "SQL", "true");
		cfg.addDefault(config + "GetRewardOnSQLConnectionLost", "true");
		cfg.addDefault(config + "delay", 30);
		cfg.addDefault(config + "TopPlayer", 10);
		
		cfg.addDefault(config + "FileDay", "Day.yml");
		cfg.addDefault(config + "FileTotalDays", "TotalDays.yml");
		
		cfg.addDefault(config + "birthday", new String[] {
				"give -player- minecraft:cake 1\n",
				"test 2"
		});
		
		cfg.addDefault(config + "daily", new String[] {
				"test 3",
				"test 4"
		});
	//Save	
		cfg.options().copyDefaults(true);
		cfg.options().header("DailyJoin");
		plugin.saveConfig();
	}
	
	public void checkConfig(dailyjoin plugin){
		FileConfiguration cfg = plugin.getConfig();
		int delay = cfg.getInt("config.delay");
		if(delay < 0){
			System.out.println("Error in Config: delay");
			cfg.set("config.delay", 0);
			System.out.println("Error in Config: Delay auf 0 gesetzt");
		}
		//MonatsAnzeige
		if(!((cfg.getString("config.MonatsAnzeige").equals("true"))||(cfg.getString("config.MonatsAnzeige").equals("false")))){
			System.out.println("Error in Config: MonatsAnzeige");
			cfg.set("config.MonatsAnzeige", "false");
			plugin.saveConfig();
			System.out.println("Error in Config: MonatsAnzeige auf false gesetzt");
		}
		//SQL
		if(!((cfg.getString("config.SQL").equals("true"))||(cfg.getString("config.SQL").equals("false")))){
			System.out.println("Error in Config: SQL");
			cfg.set("config.SQL", "false");
			plugin.saveConfig();
			System.out.println("Error in Config: SQL auf false gesetzt");
		}
		//GetRewardOnSQLConnectionLost
		if(!((cfg.getString("config.GetRewardOnSQLConnectionLost").equals("true"))||(cfg.getString("config.GetRewardOnSQLConnectionLost").equals("false")))){
			System.out.println("Error in Config: GetRewardOnSQLConnectionLost");
			cfg.set("config.GetRewardOnSQLConnectionLost", "false");
			plugin.saveConfig();
			System.out.println("Error in Config: GetRewardOnSQLConnectionLost auf false gesetzt");
		}
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
}

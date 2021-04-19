package de.bergtiger.dailyjoin;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import de.bergtiger.dailyjoin.data.MySQL;
import de.bergtiger.dailyjoin.data.MyUtils;
import de.bergtiger.dailyjoin.lang.Lang;

public class DailyCommand implements CommandExecutor, MyUtils{
	
	public static final String CMD = "dailyjoin", TOP = "top", SET = "set", ADD = "add", INFO = "info", RELOAD = "reload", PLAYER = "player";
	
	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		//info - set - reload - config - player
		if(args.length > 0){
			switch (args[0]) {
				case TOP	: daily_top(cs, args);break;
				case SET	: daily_set_player(cs, args);break;
				case ADD	: daily_add_player(cs, args);break;
				case INFO	: daily_info(cs);break;
				case RELOAD	: daily_reload(cs);break;
				case PLAYER	: daily_player(cs, args);break;
		//		case "config": daily_config(cs, args);break;
				default: {
					cs.sendMessage(Lang.WrongArgument.colored());
					return true;
				}
			}
		} else {
			daily_command(cs);
		}
		return true;
	}
	
	private void daily_command(CommandSender cs){
		if(cs.hasPermission(p_admin) || cs.hasPermission(p_cmd)){
			if(cs instanceof Player) {
				Player p = (Player)cs;
				p.spigot().sendMessage(Lang.buildTC(Lang.DailyInfo.get()));
				p.spigot().sendMessage(Lang.buildTC(Lang.DailyInfoTop.get(), null, null, CMD + " " + TOP));
				p.spigot().sendMessage(Lang.buildTC(Lang.DailyInfoSet.get(), null, null, CMD + " " + SET));
				p.spigot().sendMessage(Lang.buildTC(Lang.DailyInfoAdd.get(), null, null, CMD + " " + ADD));
				p.spigot().sendMessage(Lang.buildTC(Lang.DailyInfoInfo.get(), null, null, CMD + " " + INFO));
				p.spigot().sendMessage(Lang.buildTC(Lang.DailyInfoReload.get(), CMD + " " + RELOAD, null, null));
				p.spigot().sendMessage(Lang.buildTC(Lang.DailyInfoPlayer.get(), null, null, CMD + " " + PLAYER));
			} else {
				cs.sendMessage(Lang.DailyInfo.colored());
				cs.sendMessage(Lang.DailyInfoTop.colored());
				cs.sendMessage(Lang.DailyInfoSet.colored());
				cs.sendMessage(Lang.DailyInfoAdd.colored());
				cs.sendMessage(Lang.DailyInfoInfo.colored());
				cs.sendMessage(Lang.DailyInfoReload.colored());
				cs.sendMessage(Lang.DailyInfoPlayer.colored());
//				cs.sendMessage(Lang.DailyInfoConfig.colored());
			}
		} else {
			cs.sendMessage(Lang.NoPermission.colored());
		}
	}
	
//	private void daily_config(CommandSender cs, String[] args){
//		if(cs.hasPermission("dailyjoin.config")||cs.hasPermission("dailyjoin.admin")){
//			if(args.length == 3){
//				String data = "";
//				switch (args[1].toLowerCase()) {
//					case "monatsanzeige": case "ma": data = "MonatsAnzeige";break;
//					case "sql": case "file": case "system": data = "SQL";break;
//					case "getrewardonaqlconnectionlost": case "getreward": case "reward": data = "GetRewardOnSQLConnectionLost";break;
//					case "getoldfiles": case "oldfiles": data = "GetOldFiles";break;
//					case "delay": data = "delay";break;
//
//					default: cs.sendMessage(ChatColor.translateAlternateColorCodes('&',this.cfg.getString("lang.DailyConfig")));return;
//				}
//				if(data.equalsIgnoreCase("delay")){
//					this.cfg.set("config." + data, Integer.parseInt(args[2]));
//					this.plugin.saveConfig();
//					this.plugin.reload();
//					cs.sendMessage(ChatColor.translateAlternateColorCodes('&', this.cfg.getString("lang.DailyReload")));
//				} else {
//					if(data.equalsIgnoreCase("SQL") && args[2].equalsIgnoreCase("sql")){
//						args[2] = "true";
//					} else if(data.equalsIgnoreCase("SQL") && args[2].equalsIgnoreCase("file")){
//						args[2] = "false";
//					}
//					if(args[2].equalsIgnoreCase("true")||args[2].equalsIgnoreCase("false")){
//						this.cfg.set("config." + data, args[2]);
//						this.plugin.saveConfig();
//						this.plugin.reload();
//						cs.sendMessage(ChatColor.translateAlternateColorCodes('&', this.cfg.getString("lang.DailyReload")));
//					} else {
//						cs.sendMessage(ChatColor.translateAlternateColorCodes('&', this.cfg.getString("lang.DailyConfig")));
//					}
//				}
//			} else {
//				cs.sendMessage(ChatColor.translateAlternateColorCodes('&', this.cfg.getString("lang.DailyConfig")));
//			}
//		}
//	}
	
	private void daily_add_player(CommandSender cs, String[] args){
		if(cs.hasPermission(p_admin) || cs.hasPermission(p_set)){
			if((args.length == 4) && (args[2].equalsIgnoreCase("day") || args[2].equalsIgnoreCase("totaldays"))){
				try {
					addPlayerData(cs, change_to_uuid(cs, args[1]), args[2], Integer.parseInt(args[3]));
				} catch (NumberFormatException e) {
					cs.sendMessage(Lang.NoNumber.colored().replace(Lang.VALUE, args[3]));
				}
			} else {
				cs.sendMessage(Lang.DailyAdd.colored());
			}
		} else {
			cs.sendMessage(Lang.NoPermission.colored());
		}
	}
	
	private void addPlayerData(CommandSender cs, String p_uuid, String data, int value){
		if(p_uuid != null){
			boolean status = false;
			if(cfg.getString("config.SQL").equalsIgnoreCase("true") && this.plugin.getMySQL().hasConnection()){
				status = addPlayerData_SQL(cs, p_uuid, data, value);
			} else {
				status = addPlayerData_file(cs, p_uuid, data, value);
			}
			if(status){
				cs.sendMessage(ChatColor.translateAlternateColorCodes('&', cfg.getString("lang.DailyAddData").replace("-player-", p_uuid).replace("-data-", data).replace("-value-", Integer.toString(value))));
			}
		} else {
			cs.sendMessage(ChatColor.translateAlternateColorCodes('&', cfg.getString("lang.NoUUID")));
		}
	}
	
	private boolean addPlayerData_file(CommandSender cs, String p_uuid, String data, int value){
		File datei = new File(file_player, "player.yml");
		if(datei.exists()){
			FileConfiguration cfg_p = YamlConfiguration.loadConfiguration(datei);
			Set<String> keys = null;
			try {
				keys = cfg.getConfigurationSection("player").getKeys(false);
			} catch (Exception e) {
			}
			if((keys == null) || ((keys != null) && (keys.size() == 0))){
				//keys sind leer
				cs.sendMessage(ChatColor.translateAlternateColorCodes('&', this.cfg.getString("lang.NoPlayer")));
			} else {
				//suche in keys
				if(p_uuid.length() <= 16){
					keys.forEach(k -> {
						if(cfg_p.contains(k + "." + p_uuid)){
							cfg_p.set("player." + k + "." + data, (value + cfg_p.getInt("player." + k + "." + data)));
							try{
								cfg_p.save(datei);
								System.out.println("Save File");
								cs.sendMessage(ChatColor.translateAlternateColorCodes('&', cfg.getString("lang.DailyAddData").replace("-player-", p_uuid).replace("-data-", data).replace("-value-", Integer.toString(value))));
								return;
							} catch (IOException e){
								System.out.println("Error on save file");
							}
						}
					});
				} else {
					if(keys.contains(p_uuid)){
						cfg_p.set("player." + p_uuid + "." + data, (value + cfg_p.getInt("player." + p_uuid + "." + data)));
						try{
							cfg_p.save(datei);
							System.out.println("Save File");
							return true;
						} catch (IOException e){
							System.out.println("Error on save file");
						}						
					} else {
						cs.sendMessage(ChatColor.translateAlternateColorCodes('&', this.cfg.getString("lang.NoPlayer")));
					}
				}
			}
		} else {
			cs.sendMessage(ChatColor.translateAlternateColorCodes('&', this.cfg.getString("lang.NoFile")));
		}
		return false;
	}
	
	private boolean addPlayerData_SQL(CommandSender cs, String p_uuid, String data, int value){
		MySQL sql = this.plugin.getMySQL();
		if(sql.hasConnection()){
			Connection conn = sql.getConnection();
			ResultSet rs = null;
			PreparedStatement st = null;
			try {
				st = conn.prepareStatement("SELECT * FROM dailyjoin WHERE uuid = ? ");
				st.setString(1, p_uuid);
				rs = st.executeQuery();
				rs.last();
				if(rs.getRow() != 0) {
					rs.first();
					return setPlayerData_SQL(cs, p_uuid, data, (value + rs.getInt(data)));
				} else {
					cs.sendMessage(ChatColor.translateAlternateColorCodes('&', cfg.getString("lang.NoPlayer")));
				}
			} catch (SQLException error) {
				cs.sendMessage(ChatColor.translateAlternateColorCodes('&', cfg.getString("lang.NoConnection")));
			} finally {
				sql.closeRessources(rs, st);
			}
		}
		return false;
	}
	
	private void daily_set_player(CommandSender cs, String[] args){
		//dailyjoin set [player] day [value]
		if(cs.hasPermission("dailyjoin.set")||cs.hasPermission("dailyjoin.admin")){
			if((args.length == 4)&&(args[2].equalsIgnoreCase("day")||args[2].equalsIgnoreCase("totaldays"))){
				try {
					setPlayerData(cs, change_to_uuid(cs, args[1]), args[2], Integer.parseInt(args[3]));
				} catch (NumberFormatException e) {
					// TODO Wrong number
				}
			} else {
				cs.sendMessage(ChatColor.translateAlternateColorCodes('&', cfg.getString("lang.DailySet")));
			}
		} else {
			cs.sendMessage(ChatColor.translateAlternateColorCodes('&', cfg.getString("lang.NoPermission")));
		}
	}
	
	private void setPlayerData(CommandSender cs, String p_uuid, String data, int value){
		if(p_uuid != null){
			boolean status = false;
			if(cfg.getString("config.SQL").equalsIgnoreCase("true")&&this.plugin.getMySQL().hasConnection()){
				status = setPlayerData_SQL(cs, p_uuid, data, value);
			} else {
				status = setPlayerData_file(cs, p_uuid, data, value);
			}
			if(status){
				cs.sendMessage(ChatColor.translateAlternateColorCodes('&', cfg.getString("lang.DailySetData").replace("-player-", p_uuid).replace("-data-", data).replace("-value-", Integer.toString(value))));
			}
		} else {
			cs.sendMessage(ChatColor.translateAlternateColorCodes('&', cfg.getString("lang.NoUUID")));
		}
	}
	
	private boolean setPlayerData_file(CommandSender cs, String p_uuid, String data, int value){
		File datei = new File(file_player, "player.yml");
		if(datei.exists()){
			FileConfiguration cfg_p = YamlConfiguration.loadConfiguration(datei);
			Set<String> keys = null;
			try {
				keys = cfg.getConfigurationSection("player").getKeys(false);
			} catch (Exception e) {
			}
			if((keys == null) || ((keys != null) && (keys.size() == 0))){
				//keys sind leer
				cs.sendMessage(ChatColor.translateAlternateColorCodes('&', this.cfg.getString("lang.NoPlayer")));
			} else {
				//suche in keys
				if(p_uuid.length() <= 16){
					keys.forEach(k -> {
						if(cfg_p.contains(k + "." + p_uuid)){
							cfg_p.set("player." + k + "." + data, value);
							try{
								cfg_p.save(datei);
								System.out.println("Save File");
								cs.sendMessage(ChatColor.translateAlternateColorCodes('&', cfg.getString("lang.DailyAddData").replace("-player-", p_uuid).replace("-data-", data).replace("-value-", Integer.toString(value))));
								return;
							} catch (IOException e){
								System.out.println("Error on save file");
							}
						}
					});
				} else {
					if(keys.contains(p_uuid)){
						cfg_p.set("player." + p_uuid + "." + data, value);
						try{
							cfg_p.save(datei);
							System.out.println("Save File");
							return true;
						} catch (IOException e){
							System.out.println("Error on save file");
						}						
					} else {
						cs.sendMessage(ChatColor.translateAlternateColorCodes('&', this.cfg.getString("lang.NoPlayer")));
					}
				}
			}
		} else {
			cs.sendMessage(ChatColor.translateAlternateColorCodes('&', cfg.getString("lang.NoFile")));
		}
		return false;
	}
	
	private boolean setPlayerData_SQL(CommandSender cs, String p_uuid, String data, int value){
		MySQL sql = this.plugin.getMySQL();
		sql.queryUpdate("UPDATE dailyjoin SET " + data + " = '" + value + "' WHERE uuid='" + p_uuid + "'");
		return true;
	}
	
	private void daily_player(CommandSender cs, String[] args){
		if((args.length == 1) && (cs.hasPermission(p_admin) || cs.hasPermission(p_user) || cs.hasPermission(p_user))){
			getPlayerInfo(cs, change_to_uuid(cs, cs.getName()));
		} else if((args.length == 2) && ((args[1].equalsIgnoreCase(cs.getName()) && cs.hasPermission(p_user)) || cs.hasPermission(p_admin) || cs.hasPermission(p_player))){		
			getPlayerInfo(cs, change_to_uuid(cs, args[1]));
		} else {
			cs.sendMessage(ChatColor.translateAlternateColorCodes('&', this.cfg.getString("lang.NoPermission")));
		}
	}
	
	private String change_to_uuid(CommandSender cs, String p_name){
		if(p_name.length() > 16){			
			return p_name;
		} else {
			for(Player p : Bukkit.getOnlinePlayers()){
				if(p.getName().equalsIgnoreCase(p_name)){
					return p.getUniqueId().toString();
				}
			}
			
		}
		return new DailyUUid(this.plugin, this.cfg, file_player).getUUID(cs, p_name);
	}
	
	private void getPlayerInfo(CommandSender cs, String p_uuid){
		if(cfg.getString("config.SQL").equalsIgnoreCase("true") && this.plugin.getMySQL().hasConnection()){
			getPlayerInfo_SQL(cs, p_uuid);
		} else {
			getPlayerInfo_file(cs, p_uuid);
		}
	}
	
	private void getPlayerInfo_file(CommandSender cs, String p_uuid){
		File datei = new File(file_player, "player.yml");
		if(datei.exists()){
			FileConfiguration cfg_p = YamlConfiguration.loadConfiguration(datei);
			Set<String> keys = null;
			try {
				keys = cfg.getConfigurationSection("player").getKeys(false);
			} catch (Exception e) {
			}
			if((keys == null) || ((keys != null) && (keys.size() == 0))){
				//keys sind leer
				cs.sendMessage(ChatColor.translateAlternateColorCodes('&', this.cfg.getString("lang.NoPlayer")));
			} else {
				//suche in keys
				if(keys.contains(p_uuid)){
					String path = "player." + p_uuid + ".";
					
					cs.sendMessage(ChatColor.translateAlternateColorCodes('&', cfg.getString("lang.PlayerUmrandungOben").replace("-player-", cfg_p.getString(path + "name"))));				
					Calendar calendar = Calendar.getInstance();				
					calendar.setTime(new Timestamp(cfg_p.getLong(path + "firstjoin")));
					cs.sendMessage(ChatColor.translateAlternateColorCodes('&', TimeToString(calendar, cfg.getString("lang.PlayerFirstJoin"))));
					calendar.setTime(new Timestamp(cfg_p.getLong(path + "lastjoin")));
					cs.sendMessage(ChatColor.translateAlternateColorCodes('&', TimeToString(calendar, cfg.getString("lang.PlayerLastJoin"))));
					cs.sendMessage(ChatColor.translateAlternateColorCodes('&', cfg.getString("lang.PlayerDay").replace("-day-", Integer.toString(cfg_p.getInt(path + "day")))));
					cs.sendMessage(ChatColor.translateAlternateColorCodes('&', cfg.getString("lang.PlayerTotalDays").replace("-day-", Integer.toString(cfg_p.getInt(path + "totaldays")))));
					cs.sendMessage(ChatColor.translateAlternateColorCodes('&', cfg.getString("lang.PlayerUmrandungUnten")));
				} else {
					cs.sendMessage(ChatColor.translateAlternateColorCodes('&', cfg.getString("lang.NoPlayer")));
				}
			}
			
//			List<String> list = cfg_p.getStringList("uuids");
//			if(list.contains(p_uuid)){
//				String path = "player." + p_uuid + ".";
//	
//				cs.sendMessage(ChatColor.translateAlternateColorCodes('&', cfg.getString("lang.PlayerUmrandungOben").replace("-player-", cfg_p.getString(path + "name"))));				
//				Calendar calendar = Calendar.getInstance();				
//				calendar.setTime(new Timestamp(cfg_p.getLong(path + "firstjoin")));
//				cs.sendMessage(ChatColor.translateAlternateColorCodes('&', TimeToString(calendar, cfg.getString("lang.PlayerFirstJoin"))));
//				calendar.setTime(new Timestamp(cfg_p.getLong(path + "lastjoin")));
//				cs.sendMessage(ChatColor.translateAlternateColorCodes('&', TimeToString(calendar, cfg.getString("lang.PlayerLastJoin"))));
//				cs.sendMessage(ChatColor.translateAlternateColorCodes('&', cfg.getString("lang.PlayerDay").replace("-day-", Integer.toString(cfg_p.getInt(path + "day")))));
//				cs.sendMessage(ChatColor.translateAlternateColorCodes('&', cfg.getString("lang.PlayerTotalDays").replace("-day-", Integer.toString(cfg_p.getInt(path + "totaldays")))));
//				cs.sendMessage(ChatColor.translateAlternateColorCodes('&', cfg.getString("lang.PlayerUmrandungUnten")));
//			} else {
//				cs.sendMessage(ChatColor.translateAlternateColorCodes('&', cfg.getString("lang.NoPlayer")));
//			}
		} else {
			cs.sendMessage(ChatColor.translateAlternateColorCodes('&', cfg.getString("lang.NoFile")));
		}
	}
	
	private void getPlayerInfo_SQL(CommandSender cs, String p_uuid){
		if(p_uuid != null){
			MySQL sql = this.plugin.getMySQL();
			if(sql.hasConnection()){
				Connection conn = sql.getConnection();
				ResultSet rs = null;
				PreparedStatement st = null;
				try {
					st = conn.prepareStatement("SELECT * FROM dailyjoin WHERE uuid = ? ");
					st.setString(1, p_uuid);
					rs = st.executeQuery();
					rs.last();
					if(rs.getRow() != 0) {
						cs.sendMessage(ChatColor.translateAlternateColorCodes('&', cfg.getString("lang.PlayerUmrandungOben").replace("-player-", rs.getString("name"))));				
						Calendar calendar = Calendar.getInstance();				
						calendar.setTime(rs.getTimestamp("firstjoin"));
						cs.sendMessage(ChatColor.translateAlternateColorCodes('&', TimeToString(calendar, cfg.getString("lang.PlayerFirstJoin"))));
						calendar.setTime(rs.getTimestamp("lastjoin"));
						cs.sendMessage(ChatColor.translateAlternateColorCodes('&', TimeToString(calendar, cfg.getString("lang.PlayerLastJoin"))));
						cs.sendMessage(ChatColor.translateAlternateColorCodes('&', cfg.getString("lang.PlayerDay").replace("-day-", Integer.toString(rs.getInt("day")))));
						cs.sendMessage(ChatColor.translateAlternateColorCodes('&', cfg.getString("lang.PlayerTotalDays").replace("-day-", Integer.toString(rs.getInt("totaldays")))));
						cs.sendMessage(ChatColor.translateAlternateColorCodes('&', cfg.getString("lang.PlayerUmrandungUnten")));
					} else {
						cs.sendMessage(ChatColor.translateAlternateColorCodes('&', cfg.getString("lang.NoPlayer")));
					}
				} catch (SQLException error) {
					cs.sendMessage(ChatColor.translateAlternateColorCodes('&', cfg.getString("lang.NoConnection")));
				} finally {
					sql.closeRessources(rs, st);
				}
			} else {
				cs.sendMessage(ChatColor.translateAlternateColorCodes('&', cfg.getString("lang.NoUUID")));
			}
		}
	}
	
	private String TimeToString(Calendar calendar, String text){
		String[] month = {"Jan","Feb","Mar","Apr","Mai","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
		String buf;
		text = text.replace("Year", Integer.toString(calendar.get(Calendar.YEAR)));
		if(cfg.get("config.MonatsAnzeige").equals("true")){
			buf = month[calendar.get(Calendar.MONTH)];
		} else {
			buf = Integer.toString(calendar.get(Calendar.MONTH) + 1);
		}
		if(buf.length() < 2){
			buf = "" + buf; 
		}
		text = text.replace("Month", buf);
		buf = Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));
		if(buf.length() < 2){
			buf = "0" + buf; 
		}
		text = text.replace("Day", buf);
		buf = Integer.toString(calendar.get(Calendar.HOUR_OF_DAY));
		if(buf.length() < 2){
			buf = " " + buf; 
		}
		text = text.replace("Hour", buf);
		buf = Integer.toString(calendar.get(Calendar.MINUTE));
		if(buf.length() < 2){
			buf = "0" + buf; 
		}
		text = text.replace("Minutes", buf);
		return text;
	}
	
	private void daily_info(CommandSender cs){
		if(cs.hasPermission("dailyjoin.info")||cs.hasPermission("dailyjoin.admin")){
			//plugin info
			cs.sendMessage(ChatColor.translateAlternateColorCodes('&', this.cfg.getString("lang.PluginUmrandungOben")));
			cs.sendMessage(ChatColor.translateAlternateColorCodes('&', this.cfg.getString("lang.PluginVersion").replace("-version-", plugin.getDescription().getVersion())));
			
			//config
			cs.sendMessage(ChatColor.translateAlternateColorCodes('&', this.cfg.getString("lang.PluginMonatsAnzeige").replace("-status-", cfg.getString("config.MonatsAnzeige"))));
			if(this.cfg.getString("config.SQL").equalsIgnoreCase("true")){
				cs.sendMessage(ChatColor.translateAlternateColorCodes('&', this.cfg.getString("lang.PluginSystem").replace("-status-", "SQL")));
				cs.sendMessage(ChatColor.translateAlternateColorCodes('&', this.cfg.getString("lang.PluginRewardReconnection").replace("-status-", cfg.getString("config.GetRewardOnSQLConnectionLost"))));
			} else {
				cs.sendMessage(ChatColor.translateAlternateColorCodes('&', this.cfg.getString("lang.PluginSystem").replace("-status-", "File")));
			}
		//	cs.sendMessage(ChatColor.translateAlternateColorCodes('&', this.cfg.getString("lang.PluginGetOldFiles").replace("-status-", cfg.getString("config.GetOldFiles"))));
			cs.sendMessage(ChatColor.translateAlternateColorCodes('&', this.cfg.getString("lang.PluginDelay").replace("-delay-", Integer.toString(cfg.getInt("config.delay")))));
			cs.sendMessage(ChatColor.translateAlternateColorCodes('&', this.cfg.getString("lang.PluginTopPlayer").replace("-amount-", Integer.toString(cfg.getInt("config.TopPlayer")))));
			
			
			cs.sendMessage(ChatColor.translateAlternateColorCodes('&', this.cfg.getString("lang.PluginUmrandungUnten")));
		} else {
			cs.sendMessage(ChatColor.translateAlternateColorCodes('&', this.cfg.getString("lang.NoPermission")));
		}
	}
	
	private void daily_reload(CommandSender cs){
		if(cs.hasPermission(p_admin) || cs.hasPermission(p_reload)){
			this.plugin.reload();
			cs.sendMessage(ChatColor.translateAlternateColorCodes('&', this.cfg.getString("lang.DailyReload")));
		} else {
			cs.sendMessage(ChatColor.translateAlternateColorCodes('&', this.cfg.getString("lang.NoPermission")));
		}
	}
	
	private void daily_top(CommandSender cs, String[]args){
		if(cs.hasPermission("dailyjoin.top")||cs.hasPermission("dailyjoin.admin")){
			if((args.length < 2)||(args.length > 3)){
				cs.sendMessage(ChatColor.translateAlternateColorCodes('&', cfg.getString("lang.WrongArgument")));
			} else {
				int count = cfg.getInt("config.TopPlayer");
				if(args.length == 3){
					try {
						count = Integer.parseInt(args[2]);
					} catch (NumberFormatException e) {
						//musst be a number
					}
				}
				if(cfg.getString("config.SQL").equalsIgnoreCase("true") && this.plugin.getMySQL().hasConnection()){
					switch (args[1]) {
						case "day": top_player_sql_day(cs, count);break;
						case "totaldays": top_player_sql_totaldays(cs, count);break;
						
						default: cs.sendMessage(ChatColor.translateAlternateColorCodes('&', cfg.getString("lang.WrongArgument")));
					}
				} else {
					cs.sendMessage(ChatColor.translateAlternateColorCodes('&', this.cfg.getString("lang.OnlySQL")));
					//file
					this.top_player_file(cs, count, args[1]);
				}
			}
		} else {
			cs.sendMessage(ChatColor.translateAlternateColorCodes('&', this.cfg.getString("lang.NoPermission")));
		}
	}
	
	private void top_player_file(CommandSender cs, int count, String value){
		System.out.println("test 1");
		File datei = new File(file_player, "player.yml");
		if(datei.exists()){
			FileConfiguration cfg_p = YamlConfiguration.loadConfiguration(datei);
			List<String> list = cfg_p.getStringList("uuids");
			for(int i = 0; i < list.size(); i++){
				
			}
		} else {
			cs.sendMessage(ChatColor.translateAlternateColorCodes('&', cfg.getString("lang.NoFile")));
		}
	}
	
	private void top_player_sql_day(CommandSender cs, int count){
		MySQL sql = this.plugin.getMySQL();
		Connection conn = sql.getConnection();
		ResultSet rs = null;
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("SELECT name, day FROM dailyjoin ORDER BY day DESC LIMIT ?");
			st.setInt(1, count);
			rs = st.executeQuery();
			rs.last();
			if(rs.getRow() != 0) {
				rs.first();
				cs.sendMessage(ChatColor.translateAlternateColorCodes('&', cfg.getString("lang.TopPlayerDay")));
				while(!rs.isAfterLast()){
					cs.sendMessage(ChatColor.translateAlternateColorCodes('&', cfg.getString("lang.TopPlayerList").replace("-days-", rs.getString("day")).replace("-player-", rs.getString("name"))));
					rs.next();
				}
			} else {
				cs.sendMessage(ChatColor.translateAlternateColorCodes('&', cfg.getString("lang.NoPlayer")));
			}
		} catch (SQLException error) {
			cs.sendMessage(ChatColor.translateAlternateColorCodes('&', cfg.getString("lang.NoConnection")));
		} finally {
			sql.closeRessources(rs, st);
		}
	}
	
	private void top_player_sql_totaldays(CommandSender cs, int count){
		MySQL sql = this.plugin.getMySQL();
		Connection conn = sql.getConnection();
		ResultSet rs = null;
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("SELECT name, totaldays FROM dailyjoin ORDER BY totaldays DESC LIMIT ?");
			st.setInt(1, count);
			rs = st.executeQuery();
			rs.last();
			if(rs.getRow() != 0) {
				rs.first();
				cs.sendMessage(ChatColor.translateAlternateColorCodes('&', cfg.getString("lang.TopPlayerTotalDays")));
				while(!rs.isAfterLast()){
					cs.sendMessage(ChatColor.translateAlternateColorCodes('&', cfg.getString("lang.TopPlayerList").replace("-days-", rs.getString("totaldays")).replace("-player-", rs.getString("name"))));
					rs.next();
				}
			} else {
				cs.sendMessage(ChatColor.translateAlternateColorCodes('&', cfg.getString("lang.NoPlayer")));
			}
		} catch (SQLException error) {
			cs.sendMessage(ChatColor.translateAlternateColorCodes('&', cfg.getString("lang.NoConnection")));
		} finally {
			sql.closeRessources(rs, st);
		}
	}
	
	public void reload(){
		this.plugin.reloadConfig();
		this.cfg = this.plugin.getConfig();
	}
}

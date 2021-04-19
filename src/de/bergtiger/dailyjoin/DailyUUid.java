package de.bergtiger.dailyjoin;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import de.bergtiger.dailyjoin.data.MySQL;

public class DailyUUid {
	
	private dailyjoin plugin;
	private FileConfiguration cfg;
	private String file_player;
	
	public DailyUUid(dailyjoin plugin, FileConfiguration cfg, String file_player){
		this.plugin = plugin;
		this.cfg = cfg;
		this.file_player = file_player;
	}
	
	public String getUUID(CommandSender cs, String name){
		String uuid = null;
		if(this.cfg.getString("config.SQL").equalsIgnoreCase("true")&&this.plugin.getMySQL().hasConnection()){
			if(Bukkit.getPluginManager().isPluginEnabled("UserList")){
				uuid = this.uuid_userlist(name);
			}
			if(uuid == null){
				uuid = this.uuid_dailyjoin(cs, name);
			}
		} else {
			uuid = this.uuid_file(cs, name);
		}
		return uuid;
	}
	
	private String uuid_file(CommandSender cs, String name){
		File datei = new File(this.file_player, "player.yml");
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
				if(name.length() <= 16){
					List<String> buf = new ArrayList<String>();
					keys.forEach(k -> {
						if(cfg_p.contains(k + "." + name)){
							buf.add(k);
						}
					});
					if(buf.size() == 1){
						return buf.get(0);
					} else if(buf.size() > 1){
						cs.sendMessage(ChatColor.translateAlternateColorCodes('&', cfg.getString("lang.EqualNames").replace("-player-", name)));
						for(int i = 0; i < buf.size(); i++){
							cs.sendMessage(ChatColor.translateAlternateColorCodes('&', cfg.getString("lang.EqualNamesList").replace("-uuid-", buf.get(i))));
						}
					}
				} else {
					return name;
				}
			}
		}
		return null;
	}
	
	private String uuid_dailyjoin(CommandSender cs, String name){
		MySQL sql = this.plugin.getMySQL();
		Connection conn = sql.getConnection();
		ResultSet rs = null;
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("SELECT uuid FROM dailyjoin WHERE name = ?");
			st.setString(1, name);
			rs = st.executeQuery();
			rs.last();
			if(rs.getRow() != 0) {
				if(rs.getRow() > 1){
					rs.first();
					cs.sendMessage(ChatColor.translateAlternateColorCodes('&', cfg.getString("lang.EqualNames").replace("-player-", name)));
					while(!rs.isAfterLast()){
						cs.sendMessage(ChatColor.translateAlternateColorCodes('&', cfg.getString("lang.EqualNamesList").replace("-uuid-", rs.getString("uuid"))));
						rs.next();
					}
				} else {
					rs.first();
					return rs.getString("uuid");
				}
			}
		} catch (SQLException error) {
			cs.sendMessage(ChatColor.translateAlternateColorCodes('&', cfg.getString("lang.NoConnection")));
		} finally {
			sql.closeRessources(rs, st);
		}
		return null;
	}
	
	private String uuid_userlist(String name){
		MySQL sql = this.plugin.getMySQL();
		Connection conn = sql.getConnection();
		ResultSet rs = null;
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("SELECT uuid FROM userliste WHERE name = ?");
			st.setString(1, name);
			rs = st.executeQuery();
			rs.last();
			if(rs.getRow() != 0) {
				rs.first();
				return rs.getString("uuid");
			}
		} catch (SQLException error) {
			System.out.println(ChatColor.translateAlternateColorCodes('&', cfg.getString("lang.NoConnection")));
		} finally {
			sql.closeRessources(rs, st);
		}
		return null;
	}
}

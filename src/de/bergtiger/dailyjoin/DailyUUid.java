package de.bergtiger.dailyjoin;

import java.io.File;
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

import de.bergtiger.dailyjoin.dao.TigerConnection;
import de.bergtiger.dailyjoin.lang.Lang;

public class DailyUUid {

	private FileConfiguration cfg;
	
	public DailyUUid(){
		this.cfg = dailyjoin.inst().getConfig();
	}
	
	public String getUUID(CommandSender cs, String name){
		String uuid = null;
		if(cfg.getString("config.SQL").equalsIgnoreCase("true") && TigerConnection.hasConnection()){
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
		File datei = new File(DailyFile.FILE_DIRECTORY, DailyFile.FILE_NAME);
		if(datei.exists()){
			FileConfiguration cfg = YamlConfiguration.loadConfiguration(datei);
			Set<String> keys = cfg.getConfigurationSection("player").getKeys(false);
			if(keys != null && !keys.isEmpty()) {
				//suche in keys
				if(name.length() <= 16){
					List<String> buf = new ArrayList<String>();
					keys.forEach(k -> {
						if(cfg.contains(k + "." + name)){
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
				
			} else {
				cs.spigot().sendMessage(Lang.buildTC(Lang.NoPlayer.get()));
			}
		}
		return null;
	}
	
	private String uuid_dailyjoin(CommandSender cs, String name){
		if(TigerConnection.hasConnection()) {
			ResultSet rs = null;
			PreparedStatement st = null;
			try {
				st = TigerConnection.conn().prepareStatement("SELECT uuid FROM dailyjoin WHERE name = ?");
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
				TigerConnection.closeRessources(rs, st);
			}
		}
		return null;
	}
	
	private String uuid_userlist(String name){
		if(TigerConnection.hasConnection()) {
			ResultSet rs = null;
			PreparedStatement st = null;
			try {
				st = TigerConnection.conn().prepareStatement("SELECT uuid FROM userliste WHERE name = ?");
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
				TigerConnection.closeRessources(rs, st);
			}
		}
		return null;
	}
}

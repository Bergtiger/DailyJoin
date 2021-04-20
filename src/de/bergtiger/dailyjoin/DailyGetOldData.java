package de.bergtiger.dailyjoin;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import de.bergtiger.dailyjoin.dao.TigerConnection;

@Deprecated
public class DailyGetOldData {
	
//	private dailyjoin plugin;
//	private FileConfiguration cfg;
//	private HashMap<String, Integer> players = new HashMap<String, Integer>();
	
//	public DailyGetOldData(dailyjoin plugin){
//		this.plugin = plugin;
//		this.cfg = this.plugin.getConfig();
//	}
	
//	public void getoldplayer(Player p){
//		
//		int buf = Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin,new Runnable(){
//			@Override
//			public void run() {
//				oldplayer(p);
//				int buf = players.get(p.getName());
//				Bukkit.getScheduler().cancelTask(buf);
//				players.remove(p.getName());
//				}
//			}, 5*20L);
//		this.players.put(p.getName(), buf);
//	}
	
//	private void oldplayer(Player p){
//		String file_uuid = p.getUniqueId().toString();
//		String file_name = p.getName();
//		
//		File datei_uuid = new File(cfg.getString("config.OldFileUUID"), file_uuid + ".yml");
//		File datei_name = new File(cfg.getString("config.OldFileNAME"), file_name + ".yml");
//		
//		int TotalDays = 0;
//		int TotalDays_uuid = 0;
//		int TotalDays_name = 0;
//		int ConsecutiveDays = 0;
//		int ConsecutiveDays_uuid = 0;
//		int ConsecutiveDays_name = 0;
//		if(datei_uuid.exists()){
//			FileConfiguration cfg_uuid = YamlConfiguration.loadConfiguration(datei_uuid);
//			TotalDays_uuid = cfg_uuid.getInt("Rewards.TotalDays");
//			ConsecutiveDays_uuid = cfg_uuid.getInt("Reqards.ConsecutiveDays");
//		}
//		if(datei_name.exists()){
//			FileConfiguration cfg_name = YamlConfiguration.loadConfiguration(datei_name);
//			TotalDays_name = cfg_name.getInt(p.getName() + ".TotalDays");
//			ConsecutiveDays_name = cfg_name.getInt(p.getName() + ".ConsecutiveDays");
//			if(!(TotalDays_name > 0)){
//				TotalDays_name = cfg_name.getInt("Rewards.TotalDays");
//			}
//			if(!(ConsecutiveDays_name > 0)){
//				ConsecutiveDays_name = cfg_name.getInt("Rewards.ConsecutiveDays");
//			}
//		}
//		if(ConsecutiveDays_uuid > ConsecutiveDays_name){
//			ConsecutiveDays = ConsecutiveDays_uuid;
//		} else {
//			ConsecutiveDays = ConsecutiveDays_name;
//		}
//		if(TotalDays_uuid > TotalDays_name){
//			TotalDays = TotalDays_uuid;
//		} else {
//			TotalDays = TotalDays_name;
//		}
//		//send an sql
//		if((TotalDays > 0)&&(ConsecutiveDays > 0)){
//			TigerConnection sql = this.plugin.getMySQL();
//			Connection conn = sql.getConnection();
//			ResultSet rs = null;
//			PreparedStatement st = null;
//			try {
//				st = conn.prepareStatement("SELECT userliste.firstjoin, dailyjoin.day, dailyjoin.totaldays FROM userliste JOIN dailyjoin ON userliste.uuid = dailyjoin.uuid WHERE userliste.uuid = ? ");
//				st.setString(1, p.getUniqueId().toString());
//				rs = st.executeQuery();
//				rs.last();
//				if(rs.getRow() != 0) {
//					TotalDays += rs.getInt("dailyjoin.totaldays");
//					ConsecutiveDays += rs.getInt("dailyjoin.day");
//					sql.queryUpdate("UPDATE dailyjoin SET day = '" + ConsecutiveDays + "', totaldays = '" + TotalDays + "', firstjoin = '" + rs.getTimestamp("userliste.firstjoin") + "' WHERE uuid = '" + p.getUniqueId().toString() + "'");
//					System.out.println("[DailyJoin] Update " + p.getName());
//					if(datei_uuid.exists()){
//						datei_uuid.delete();
//						System.out.println("[DailyJoin] Delete Datei " + file_uuid + ".yml");
//					}
//					if(datei_name.exists()){
//						datei_name.delete();
//						System.out.println("[DailyJoin] Delete Datei " + file_name + ".yml");
//					}
//				} else {
//					System.out.println("[DailyJoin] Error on Update " + p.getName());
//				}
//			} catch (SQLException error) {
//				System.out.println("[DailyJoin] Error on SQL-Connection");
//			} finally {
//				sql.closeRessources(rs, st);
//			}
//		} else {
//			System.out.println("[DailyJoin] No Old Files for " + p.getName());
//		}
//	}
	
//	public void reload(){
//		this.plugin.reloadConfig();
//		this.cfg = this.plugin.getConfig();
//	}
}

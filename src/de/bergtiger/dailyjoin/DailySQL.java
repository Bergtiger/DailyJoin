package de.bergtiger.dailyjoin;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import de.bergtiger.dailyjoin.data.MySQL;
import de.bergtiger.dailyjoin.data.MyUtils;

public class DailySQL implements MyUtils{
	
	private dailyjoin plugin;
	private String file_player = "plugins/DailyJoin/players";
	
	public DailySQL(dailyjoin plugin){
		this.plugin = plugin;
	}

	private boolean old_player(Player p, int day, int totaldays, Timestamp firstjoin, Timestamp lastjoin){
		MySQL sql = this.plugin.getMySQL();
		if(sql.hasConnection()){
			Connection conn = sql.getConnection();
			PreparedStatement st = null;
			try {
				st = conn.prepareStatement("UPDATE dailyjoin SET day = ?, totaldays = ?, lastjoin = ?, name = ? WHERE uuid = ?");
				st.setInt(1, day);
				st.setInt(2, totaldays);
				st.setTimestamp(3, lastjoin);
				st.setString(4, p.getName());
				st.setString(5, p.getUniqueId().toString());
				st.executeUpdate();
				this.plugin.getDailyReward().setReward(p, day, totaldays, firstjoin);
				return true;
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				sql.closeRessources(null, st);
			}
		}
		return false;
	}
	
	private boolean new_player(Player p){
		MySQL sql = this.plugin.getMySQL();
		if(sql.hasConnection()){
			Connection conn = sql.getConnection();
			PreparedStatement st = null;
			Timestamp t = new Timestamp(Calendar.getInstance().getTime().getTime());
			try {
				st = conn.prepareStatement("INSERT INTO dailyjoin (name, uuid, day, totaldays, firstjoin, lastjoin) VALUES (?, ?, '1', '1', ?, ?)");
				st.setString(1, p.getName());
				st.setString(2, p.getUniqueId().toString());
				st.setTimestamp(3, t);
				st.setTimestamp(4, t);
				st.executeUpdate();
				this.plugin.getDailyReward().setReward(p, 1, 1, t);
				return true;
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				sql.closeRessources(null, st);
			}
		}
		return false;
	}

//	private void connLost(Player p){
//		System.out.println("[DailyJoin] Error: lost SQL-Connection");
//		this.plugin.getDailyFile().dailyjoin_file(p);
//		this.plugin.getMySQL().reconnect();
//	}
	
	public void dailyjoin_sql(Player p){
		if(p.isOnline()){
			MySQL sql = this.plugin.getMySQL();
			if(sql.hasConnection()){
				Connection conn = sql.getConnection();
				ResultSet rs = null;
				PreparedStatement st = null;
				try {
					st = conn.prepareStatement("SELECT * FROM dailyjoin WHERE uuid = ? ");
					st.setString(1, p.getUniqueId().toString());
					rs = st.executeQuery();
					rs.last();
					if(rs.getRow() != 0) {
						//old Player
						if(!today(rs.getTimestamp("lastjoin"))){
							int day = rs.getInt("day");
							int totaldays = rs.getInt("totaldays") + 1;
							if(yesterday(rs.getTimestamp("lastjoin"))){
								day++;
							} else {
								day = 1;
							}
							Timestamp t = new Timestamp(Calendar.getInstance().getTime().getTime());
							if(!old_player(p, day, totaldays, rs.getTimestamp("firstjoin"), t)){
								//oldPlayer connection lost
								
								File datei = new File(this.file_player, "player.yml");
								FileConfiguration cfg = YamlConfiguration.loadConfiguration(datei);
								this.plugin.getDailyFile().save_old(datei, cfg, p, day, totaldays);
							}
						}
					} else {
						//new Player
						if(!new_player(p)){
							//new Player connection lost
							
							File datei = new File(this.file_player, "player.yml");
							FileConfiguration cfg = YamlConfiguration.loadConfiguration(datei);
							this.plugin.getDailyFile().save_new(datei, cfg, p);
						}
					}
				} catch (SQLException e) {
					e.printStackTrace();
					//suche fehlgeschlagen
					
					this.plugin.getDailyFile().dailyjoin_file(p);
					
				} finally {
					sql.closeRessources(rs, st);
				}
			} else {
				//Suche fehlgeschlagen
				
				this.plugin.getDailyFile().dailyjoin_file(p);
			}
		}
	}
}

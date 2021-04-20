package de.bergtiger.dailyjoin;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import de.bergtiger.dailyjoin.dao.TigerConnection;
import de.bergtiger.dailyjoin.data.MyUtils;

public class DailySQL implements MyUtils{
	
	@Deprecated
	private boolean old_player(Player p, int day, int totaldays, Timestamp firstjoin, Timestamp lastjoin){
		if(TigerConnection.hasConnection()){
			PreparedStatement st = null;
			try {
				st = TigerConnection.conn().prepareStatement("UPDATE dailyjoin SET day = ?, totaldays = ?, lastjoin = ?, name = ? WHERE uuid = ?");
				st.setInt(1, day);
				st.setInt(2, totaldays);
				st.setTimestamp(3, lastjoin);
				st.setString(4, p.getName());
				st.setString(5, p.getUniqueId().toString());
				st.executeUpdate();
				dailyjoin.inst().getDailyReward().giveReward(p, day, totaldays, firstjoin);
				return true;
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				TigerConnection.closeRessources(null, st);
			}
		}
		return false;
	}
	
	@Deprecated
	private boolean new_player(Player p){
		if(TigerConnection.hasConnection()){
			PreparedStatement st = null;
			Timestamp t = new Timestamp(Calendar.getInstance().getTime().getTime());
			try {
				st = TigerConnection.conn().prepareStatement("INSERT INTO dailyjoin (name, uuid, day, totaldays, firstjoin, lastjoin) VALUES (?, ?, '1', '1', ?, ?)");
				st.setString(1, p.getName());
				st.setString(2, p.getUniqueId().toString());
				st.setTimestamp(3, t);
				st.setTimestamp(4, t);
				st.executeUpdate();
				dailyjoin.inst().getDailyReward().giveReward(p, 1, 1, t);
				return true;
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				TigerConnection.closeRessources(null, st);
			}
		}
		return false;
	}
	
	/**
	 * check if player exists.
	 * if exists update else insert
	 * if gone wrong add to file
	 * @param p
	 */
	@Deprecated
	public void dailyjoin_sql(Player p){
		if(p.isOnline()){
			if(TigerConnection.hasConnection()){
				ResultSet rs = null;
				PreparedStatement st = null;
				try {
					st = TigerConnection.conn().prepareStatement("SELECT * FROM dailyjoin WHERE uuid = ? ");
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
								
								File datei = new File(FILE_DIRECTORY, FILE_NAME);
								FileConfiguration cfg = YamlConfiguration.loadConfiguration(datei);
								dailyjoin.inst().getDailyFile().save(datei, cfg, p, day, totaldays);
							}
						}
					} else {
						//new Player
						if(!new_player(p)){
							//new Player connection lost
							File datei = new File(FILE_DIRECTORY, FILE_NAME);
							FileConfiguration cfg = YamlConfiguration.loadConfiguration(datei);
							dailyjoin.inst().getDailyFile().save(datei, cfg, p);
						}
					}
				} catch (SQLException e) {
					e.printStackTrace();
					//suche fehlgeschlagen
					
					dailyjoin.inst().getDailyFile().dailyjoin_file(p);
					
				} finally {
					TigerConnection.closeRessources(rs, st);
				}
			} else {
				//Suche fehlgeschlagen
				
				dailyjoin.inst().getDailyFile().dailyjoin_file(p);
			}
		}
	}
}

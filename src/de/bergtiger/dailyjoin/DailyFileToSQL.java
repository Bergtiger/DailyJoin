package de.bergtiger.dailyjoin;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import de.bergtiger.dailyjoin.data.MySQL;

public class DailyFileToSQL {

	private dailyjoin plugin;
	private List<DailyPlayer> player_list;
	private String file_player = "plugins/DailyJoin/players";
	
	public DailyFileToSQL(dailyjoin plugin){
		this.plugin = plugin;
	}
	
	private List<DailyPlayer> getlist(){
		File datei = new File(this.file_player, "player.yml");
		if(datei.exists()){
			FileConfiguration cfg_p = YamlConfiguration.loadConfiguration(datei);
			List<String> list = cfg_p.getStringList("uuids");
			List<DailyPlayer> player = new ArrayList<DailyPlayer>();
			String path = "";
			for(int i = 0; i < list.size(); i++){
				path = "player." + list.get(i) + ".";
				DailyPlayer p = new DailyPlayer(cfg_p.getString(path + "name"), list.get(i), cfg_p.getInt(path + "day"), cfg_p.getInt(path + "totaldays"), new Timestamp(cfg_p.getLong(path + "firstjoin")), new Timestamp(cfg_p.getLong(path + "lastjoin")));
				player.add(p);
			}
			return player;
		} else {
			return null;
		}
	}
	
	private void savelist(){
		File datei = new File(this.file_player, "player.yml");
		datei.delete();
		if(!this.player_list.isEmpty()){
			FileConfiguration cfg_p = YamlConfiguration.loadConfiguration(datei);
			List<String> list = new ArrayList<String>();
			String path = "";
			for(int i = 0; i < this.player_list.size(); i++){
			list.add(this.player_list.get(i).uuid());
			path = "player." + this.player_list.get(i).uuid() + ".";
			cfg_p.set(path + "name", this.player_list.get(i).name());
			cfg_p.set(path + "day", this.player_list.get(i).day());
			cfg_p.set(path + "totaldays", this.player_list.get(i).totaldays());
			cfg_p.set(path + "firstjoin", this.player_list.get(i).firstjoin().getTime());
			cfg_p.set(path + "lastjoin", this.player_list.get(i).lastjoin().getTime());
			}
			cfg_p.set("uuids", list);
			try{
				cfg_p.save(datei);
				System.out.println("Save File");
			} catch (IOException e){
				System.out.println("Error on save file");
			}
		}
	}
	
	public void SQLconnError(){
		System.out.println("[DailyJoin] Error: Sql-Connection");
		savelist();
		this.plugin.getMySQL().reconnect();
	}
	
	private boolean updatePlayer(String uuid, int day, int totaldays, Timestamp t){
		boolean status = true;
		MySQL sql = this.plugin.getMySQL();
		Connection conn = sql.getConnection();
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("UPDATE dailyjoin SET day = ?, totaldays = ?, lastjoin=? WHERE uuid = ?");
			st.setInt(1, day);
			st.setInt(2, totaldays);
			st.setTimestamp(3, t);
			st.setString(4, uuid);
			st.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			status = false;
		} finally {
			sql.closeRessources(null, st);
		}
		return status;
	}
	
	private boolean insertPlayer(DailyPlayer p){
		boolean status = true;
		MySQL sql = this.plugin.getMySQL();
		Connection conn = sql.getConnection();
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("INSERT INTO dailyjoin (name, uuid, day, totaldays, firstjoin, lastjoin) VALUES (?, ?, ?, ?, ?, ?)");
			st.setString(1, p.name());
			st.setString(2, p.uuid());
			st.setInt(3, p.day());
			st.setInt(4, p.totaldays());
			st.setTimestamp(5, p.firstjoin());
			st.setTimestamp(6, p.lastjoin());
			st.executeUpdate();
		} catch (SQLException error) {
			error.printStackTrace();
			status = false;
		} finally {
			sql.closeRessources(null, st);
		}
		return status;
	}
	
	private boolean yesterday(Timestamp fjoin, Timestamp ljoin){
		Calendar firstjoin = Calendar.getInstance();
		Calendar lastjoin = Calendar.getInstance();
		firstjoin.setTimeInMillis(fjoin.getTime());
		lastjoin.setTimeInMillis(ljoin.getTime());
		if((firstjoin.get(Calendar.YEAR) == lastjoin.get(Calendar.YEAR)) && (firstjoin.get(Calendar.DAY_OF_YEAR) == (lastjoin.get(Calendar.DAY_OF_YEAR)+1))){
			return true;
		} else if((firstjoin.get(Calendar.YEAR) == (lastjoin.get(Calendar.YEAR)+1)) && (((lastjoin.get(Calendar.MONTH) == 11)&&(lastjoin.get(Calendar.DAY_OF_MONTH) == 31))&&(firstjoin.get(Calendar.DAY_OF_YEAR) == 1))){
			return true;
		}
		return false;
	}
	
	private boolean today(Timestamp fjoin, Timestamp ljoin){
		Calendar firstjoin = Calendar.getInstance();
		Calendar lastjoin = Calendar.getInstance();
		firstjoin.setTimeInMillis(fjoin.getTime());
		lastjoin.setTimeInMillis(ljoin.getTime());
		if((firstjoin.get(Calendar.YEAR) == lastjoin.get(Calendar.YEAR)) && (firstjoin.get(Calendar.DAY_OF_YEAR) == lastjoin.get(Calendar.DAY_OF_YEAR))){
			return true;
		}
		return false;
	}
	
	private boolean getPlayer(DailyPlayer p){
		boolean status = false;
		MySQL sql = this.plugin.getMySQL();
		Connection conn = sql.getConnection();
		ResultSet rs = null;
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("SELECT * FROM dailyjoin WHERE uuid = ? ");
			st.setString(1, p.uuid());
			rs = st.executeQuery();
			rs.last();
			if(rs.getRow() != 0) {
				//oldPlayer
				int day;
				int totaldays;
				Timestamp lastjoin;
				if(yesterday(p.firstjoin(),rs.getTimestamp("lastjoin"))){
					day = p.day() + rs.getInt("day");
					totaldays = p.totaldays() + rs.getInt("totaldays");
					lastjoin = p.lastjoin();
				} else if(today(p.firstjoin(),rs.getTimestamp("lastjoin"))){
					day = p.day() + rs.getInt("day") - 1;
					totaldays = p.totaldays() + rs.getInt("totaldays") - 1;
					lastjoin = p.lastjoin();
				} else {
					day = 1;
					totaldays = p.totaldays() + rs.getInt("totaldays");
					lastjoin = p.lastjoin();
				}
				if(updatePlayer(p.uuid(), day, totaldays, lastjoin)){status = true;}
			} else {
				if(insertPlayer(p)){status = true;}
			}
		} catch (SQLException error) {
			error.printStackTrace();
		} finally {
			sql.closeRessources(rs, st);
		}
		return status;
	}
	
	public void FileToSQL(){
		File datei = new File(this.file_player, "player.yml");
		if(datei.exists()){
			this.player_list = getlist();
			if(!this.player_list.isEmpty()){
				for(int i = 0; i < this.player_list.size(); i++){
					if(getPlayer(this.player_list.get(i))){
					//hat funktioniert
						this.player_list.remove(i);
						i--;
					} else {
						SQLconnError();
						return;
					}
				}
				savelist();
			} else {
				//EmptyList
				System.out.println("[DailyJoin] Error: Empty Offline List");
			}
		} else {
			System.out.println("[DailyJoin] No offline Player");
		}
	}
}

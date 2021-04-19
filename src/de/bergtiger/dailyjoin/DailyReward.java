package de.bergtiger.dailyjoin;

import java.io.File;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import de.bergtiger.dailyjoin.data.MyUtils;

public class DailyReward implements MyUtils{
	
	private dailyjoin plugin;
	private FileConfiguration cfg;
	private List<String> daily;
	private List<String> birthday;
	private HashMap<Integer, List<String>> day			= new HashMap<Integer, List<String>>();;
	private HashMap<Integer, List<String>> totaldays	= new HashMap<Integer, List<String>>();
	
	
	public DailyReward(dailyjoin plugin){
		this.plugin = plugin;
		this.cfg = this.plugin.getConfig();
		this.setData();
	}
	
	private void setData(){
		this.daily = cfg.getStringList("config.daily");
		this.birthday = cfg.getStringList("config.birthday");
		this.getDataDay();
		this.getDataTotalDays();
	}
	
	private void getDataDay(){
		File file = new File("plugins/DailyJoin/", this.cfg.getString("config.FileDay"));
		if(file.exists()){
			this.day.clear();
			FileConfiguration cfg_day = YamlConfiguration.loadConfiguration(file);
			Set<String> keys = null;
			try {
				keys = cfg_day.getKeys(false);
			} catch (Exception e) {
			}
			if((keys == null) || ((keys != null) && (keys.size() == 0))){
				System.err.println("DailyJoin: getDataDay: No Keys");
				return;
			} else {
			keys.forEach(k -> {
					this.day.put(Integer.parseInt(k), cfg_day.getStringList(k));
				});
			}
		}
	}
	
	private void getDataTotalDays(){
		File file = new File("plugins/DailyJoin/", cfg.getString("config.FileTotalDays"));
		if(file.exists()){
			this.totaldays.clear();
			FileConfiguration cfg_totaldays = YamlConfiguration.loadConfiguration(file);
			Set<String> keys = null;
			try {
				keys = cfg_totaldays.getKeys(false);
			} catch (Exception e) {
			}
			if((keys == null) || ((keys != null) && (keys.size() == 0))){
				System.err.println("DailyJoin: getDataTotalDays: No Keys");
				return;
			} else {
			keys.forEach(k -> {
					this.totaldays.put(Integer.parseInt(k), cfg_totaldays.getStringList(k));
				});
			}
		}
	}
	
	public void setReward(Player p, int day, int totaldays, Timestamp t){
		this.setDaily(p, day);
		this.setDay(p, day);
		this.setTotalDays(p, totaldays);
		this.setBirthday(p, totaldays, t);
	}
	
	private void setDaily(Player p, int day){
		if((this.daily != null) && (!this.daily.isEmpty())){
			this.setCommand(p, this.daily);
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', cfg.getString("lang.Daily").replace("-day-", Integer.toString(day))));
		}
	}
	
	private void setDay(Player p, int day){
		if((this.day != null) && this.day.containsKey(day)){
			this.setCommand(p, this.day.get(day));
			Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', cfg.getString("lang.SpezialDay").replace("-day-", Integer.toString(day)).replace("-player-", p.getName())));
		}
	}
	
	private void setTotalDays(Player p, int totaldays){
		if((this.totaldays != null) && this.totaldays.containsKey(totaldays)){
			this.setCommand(p, this.totaldays.get(totaldays));
			Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', cfg.getString("lang.SpezialTotalDays").replace("-day-", Integer.toString(totaldays)).replace("-player-", p.getName())));
		}
	}
	
	private void setBirthday(Player p, int totaldays, Timestamp t){
		if((this.birthday != null) && (!this.birthday.isEmpty())){
			Calendar today = Calendar.getInstance();
			Calendar time = today;
			time.setTimeInMillis(t.getTime());
			if((time.get(Calendar.YEAR)!=today.get(Calendar.YEAR))&&((time.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH))&&(time.get(Calendar.MONTH)==today.get(Calendar.MONTH)))){
				setCommand(p, this.birthday);
				Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', cfg.getString("lang.Birthday").replace("-day-", Integer.toString(totaldays)).replace("-player-", p.getName())));
			}
		}
	}
	
	private void setCommand(Player p, List<String> list){
		for(int i = 0; i < list.size(); i++){
			Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), list.get(i).replace("-player-", p.getName()));
		}
	}
	
	public void reload(){
		this.plugin.reloadConfig();
		this.cfg = this.plugin.getConfig();
		this.setData();
	}
}

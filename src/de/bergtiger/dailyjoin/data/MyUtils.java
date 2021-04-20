package de.bergtiger.dailyjoin.data;

import java.sql.Timestamp;
import java.util.Calendar;

public interface MyUtils {
	
	public final String
		p_admin = "dailyjoin.admin",
		p_user = "dailyjoin.user",
		p_set = "dailyjoin.set",
		p_cmd = "dailyjoin.command",
		p_player = "dailyjoin.player",
		p_reload = "dailyjoin.reload",
		
		FILE_DIRECTORY = "plugins/DailyJoin/players",
		FILE_NAME = "player.yml";
	
	
	public default boolean yesterday(Timestamp t){
		Calendar today = Calendar.getInstance();
		Calendar lastjoin = Calendar.getInstance();
		lastjoin.setTimeInMillis(t.getTime());
		if((lastjoin.get(Calendar.YEAR) == today.get(Calendar.YEAR)) && ((lastjoin.get(Calendar.DAY_OF_YEAR) + 1) == today.get(Calendar.DAY_OF_YEAR))){
			return true;
		} else if(((lastjoin.get(Calendar.YEAR) + 1) == today.get(Calendar.YEAR))&& (((lastjoin.get(Calendar.MONTH) == 11)&&(lastjoin.get(Calendar.DAY_OF_MONTH) == 31))&&(today.get(Calendar.DAY_OF_YEAR) == 1))){
			return true;
		}
		return false;
	}
	
	public default boolean today(Timestamp t){
		Calendar today = Calendar.getInstance();
		Calendar lastjoin = Calendar.getInstance();
		lastjoin.setTimeInMillis(t.getTime());
		if( (lastjoin.get(Calendar.YEAR) == today.get(Calendar.YEAR)) && (lastjoin.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR))){
			return true;
		}
		return false;
	}
}

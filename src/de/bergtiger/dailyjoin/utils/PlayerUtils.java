package de.bergtiger.dailyjoin.utils;

import de.bergtiger.dailyjoin.DailyJoin;
import de.bergtiger.dailyjoin.bdo.DailyPlayer;
import de.bergtiger.dailyjoin.utils.lang.Lang;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;

public class PlayerUtils {

	/**
	 * build string from player data for hover effect in chat.
	 * 
	 * @param dp {@link DailyPlayer}
	 * @return string representing player
	 */
	public static String buildHover(DailyPlayer dp) {
		if (dp != null) {
			return new StringBuilder().append(Lang.HOVER_PLAYER_NAME.get().replace(Lang.VALUE, dp.getName()))
					.append("\n")
					.append(Lang.HOVER_PLAYER_DAYS_TOTAL.get().replace(Lang.VALUE, Integer.toString(dp.getDaysTotal())))
					.append("\n")
					.append(Lang.HOVER_PLAYER_DAYS_CONSECUTIVE.get().replace(Lang.VALUE,
							Integer.toString(dp.getDaysConsecutive())))
					.append("\n")
					.append(Lang.HOVER_PLAYER_LASTJOIN.get().replace(Lang.VALUE, TimeUtils.formatted(dp.getLastjoin())))
					.append("\n").append(Lang.HOVER_PLAYER_FIRSTJOIN.get().replace(Lang.VALUE,
							TimeUtils.formatted(dp.getFirstjoin())))
					.toString();
		}
		return null;
	}

	/**
	 * request website.
	 * @param new_url http url to connect with
	 * @return String representing the website response
	 */
	public String[] httpRequest(String new_url){
		String[] buffer = null;
		if(new_url != null){
			try {
				URL url = new URL(new_url);
				URLConnection urlConnection = url.openConnection();
				HttpURLConnection connection;
				if(urlConnection instanceof HttpURLConnection) {
					connection = (HttpURLConnection) urlConnection;
				} else {
					DailyJoin.getDailyLogger().log(Level.SEVERE, String.format("no httpRequest: '%s'", new_url));
					return null;
				}
				BufferedReader in = new BufferedReader(
						new InputStreamReader(connection.getInputStream()));
				StringBuilder urlString = new StringBuilder();
				String current;
				while((current = in.readLine()) != null) {
					urlString.append(current);
				}
				buffer = urlString.toString().split(",");
			} catch(IOException e) {
				DailyJoin.getDailyLogger().log(Level.SEVERE, String.format("httpRequest: '%s'", new_url), e);
			}
		}
//		DailyJoin.getDailyLogger().log(Level.INFO, String.format("httpRequest: '%s'", new_url));
		return buffer;
	}

	/**
	 * get latest user name from Mojang.
	 * @param uuid player identification
	 * @return latest name or null
	 */
	public static String getLatestName(String uuid){
		if(uuid != null){
			String[] buffer = new PlayerUtils().httpRequest(String.format("https://api.mojang.com/user/profiles/%s/names", uuid.replaceAll("-", "")));
			if(buffer != null){
				if(buffer.length == 1){
					return buffer[0].substring(10, (buffer[0].length() - 3));
				} else {
					return buffer[(buffer.length - 2)].substring(9, (buffer[(buffer.length - 2)].length() - 1));
				}
			}
		}
		return null;
	}
}

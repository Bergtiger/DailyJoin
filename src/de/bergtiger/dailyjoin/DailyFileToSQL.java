package de.bergtiger.dailyjoin;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import de.bergtiger.dailyjoin.bdo.DailyPlayer;
import de.bergtiger.dailyjoin.dao.TigerConnection;
import de.bergtiger.dailyjoin.exception.NoSQLConnectionException;
import de.bergtiger.dailyjoin.exception.UpdatePlayerException;

public class DailyFileToSQL {

	/**
	 * check if ljoin is yesterday to fjoin.
	 * @param fjoin firstjoin
	 * @param ljoin lastjoin
	 * @return true when fjoin and ljoin are on the same day
	 */
	private boolean yesterday(Timestamp fjoin, Timestamp ljoin) {
		Calendar firstjoin = Calendar.getInstance();
		Calendar lastjoin = Calendar.getInstance();
		firstjoin.setTimeInMillis(fjoin.getTime());
		lastjoin.setTimeInMillis(ljoin.getTime());
		if ((firstjoin.get(Calendar.YEAR) == lastjoin.get(Calendar.YEAR))
				&& (firstjoin.get(Calendar.DAY_OF_YEAR) == (lastjoin.get(Calendar.DAY_OF_YEAR) + 1))) {
			return true;
		} else if ((firstjoin.get(Calendar.YEAR) == (lastjoin.get(Calendar.YEAR) + 1))
				&& (((lastjoin.get(Calendar.MONTH) == 11) && (lastjoin.get(Calendar.DAY_OF_MONTH) == 31))
						&& (firstjoin.get(Calendar.DAY_OF_YEAR) == 1))) {
			return true;
		}
		return false;
	}

	/**
	 * check if fjoin and ljoin are on the same day.
	 * @param fjoin firstjoin
	 * @param ljoin lastjoin
	 * @return true when fjoin and ljoin are on the same day
	 */
	private boolean today(Timestamp fjoin, Timestamp ljoin) {
		Calendar firstjoin = Calendar.getInstance();
		Calendar lastjoin = Calendar.getInstance();
		firstjoin.setTimeInMillis(fjoin.getTime());
		lastjoin.setTimeInMillis(ljoin.getTime());
		return (firstjoin.get(Calendar.YEAR) == lastjoin.get(Calendar.YEAR))
				&& (firstjoin.get(Calendar.DAY_OF_YEAR) == lastjoin.get(Calendar.DAY_OF_YEAR));
	}
	
	/**
	 * 
	 */
	public void FileToSQL() {
		File file = new File(DailyFile.FILE_DIRECTORY, DailyFile.FILE_NAME);
		if (file.exists()) {
			List<DailyPlayer> players = DailyFile.loadAll();
			if (players != null && !players.isEmpty()) {
				for (int i = 0; i < players.size(); i++) {
					try {
						DailyPlayer pFile = players.get(i);
						DailyPlayer pSQL = TigerConnection.inst().getPlayerDAO().getPlayer(pFile.getUuid());
						// check if existing Player
						if (pSQL != null) {
							// existing Player -> merge
							if (today(pFile.getFirstjoin(), pSQL.getLastjoin())) {
								// continues logins day overlapping
								// sum days total - 1
								pFile.setDaysTotal(pFile.getDaysTotal() + pSQL.getDaysTotal() - 1);
								// check if days correct ???
								pFile.setDaysConsecutive(pFile.getDaysConsecutive() + pSQL.getDaysConsecutive() - 1);
								// lastlogin = file.lastlogin
							} else
							if (yesterday(pFile.getFirstjoin(), pSQL.getLastjoin())) {
								// continues logins
								// sum totaldays
								pFile.setDaysTotal(pFile.getDaysTotal() + pSQL.getDaysTotal());
								// check if days correct ???
								pFile.setDaysConsecutive(pFile.getDaysConsecutive() + pSQL.getDaysConsecutive());
								// lastlogin = file.lastlogin
							} else {
								// not continues logins
								// sum totaldays
								pFile.setDaysTotal(pFile.getDaysTotal() + pSQL.getDaysTotal());
								// day = file.day
								// lastlogin = file.lastlogin
							}
						}
						// update or insert Player
						TigerConnection.inst().getPlayerDAO().updatePlayer(pFile);
						// remove from working list
						players.remove(i);
						i--;
					} catch (NoSQLConnectionException e) {
						// NoSQLException
						// stop merge and save file
						DailyFile.saveAll(players);
						TigerConnection.noConnection();
						return;
					} catch (UpdatePlayerException e) {
						dailyjoin.getDailyLogger().log(Level.SEVERE, "FileToSQL", e);
					}
				}
			} else {
				// EmptyList
				dailyjoin.getDailyLogger().log(Level.INFO, "Empty Offline List");
			}
			// finished merging file in sql
			// delete file when players is empty
			if(players == null || players.isEmpty()) {
				file.delete();
			}
		} else {
			// No File
			dailyjoin.getDailyLogger().log(Level.INFO, "No Offline List");
		}
	}
}

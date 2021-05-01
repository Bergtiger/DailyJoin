package de.bergtiger.dailyjoin;

import java.io.File;
import java.util.List;
import java.util.logging.Level;

import de.bergtiger.dailyjoin.bdo.DailyPlayer;
import de.bergtiger.dailyjoin.dao.TigerConnection;
import de.bergtiger.dailyjoin.dao.impl.file.DailyFile;
import de.bergtiger.dailyjoin.dao.impl.sql.PlayerDAOImplSQL;
import de.bergtiger.dailyjoin.exception.NoSQLConnectionException;
import de.bergtiger.dailyjoin.exception.UpdatePlayerException;
import de.bergtiger.dailyjoin.utils.TimeUtils;

public class DailyFileToSQL {
	
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
						// explicit SQL
						DailyPlayer pSQL = new PlayerDAOImplSQL().getPlayer(pFile.getUuid());
						// check if existing Player
						if (pSQL != null) {
							// existing Player -> merge
							if(TimeUtils.isSameDay(pFile.getFirstjoin(), pSQL.getLastjoin())) {
								// continues logins day overlapping
								// sum days total - 1
								pFile.setDaysTotal(pFile.getDaysTotal() + pSQL.getDaysTotal() - 1);
								// check if days correct ???
								pFile.setDaysConsecutive(pFile.getDaysConsecutive() + pSQL.getDaysConsecutive() - 1);
								// lastlogin = file.lastlogin
							} else if(TimeUtils.isDaysBetween(pSQL.getLastjoin(), pFile.getFirstjoin(), 1)) {
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
						// explicit SQL
						new PlayerDAOImplSQL().updatePlayer(pFile);
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

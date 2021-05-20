package de.bergtiger.dailyjoin.dao.migration;

import java.io.File;
import java.util.List;
import java.util.logging.Level;

import de.bergtiger.dailyjoin.DailyJoin;
import de.bergtiger.dailyjoin.bdo.DailyPlayer;
import de.bergtiger.dailyjoin.dao.TigerConnection;
import de.bergtiger.dailyjoin.dao.impl.PlayerDAOImpl;
import de.bergtiger.dailyjoin.exception.NoSQLConnectionException;
import de.bergtiger.dailyjoin.exception.UpdatePlayerException;
import de.bergtiger.dailyjoin.utils.TimeUtils;
import de.bergtiger.dailyjoin.utils.lang.Lang;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import static de.bergtiger.dailyjoin.dao.impl.file.PlayerDAOImplFile.*;
import static de.bergtiger.dailyjoin.utils.permission.TigerPermission.*;

public class DailyFileToSQL {

	private static DailyFileToSQL instance;

	public static DailyFileToSQL inst() {
		if(instance == null)
			instance = new DailyFileToSQL();
		return instance;
	}

	private DailyFileToSQL() {}

	/**
	 * runs daily migrate file_to_sql command in its own thread.
	 * @param cs {@link CommandSender}
	 */
	public static void run(CommandSender cs) {
		Bukkit.getScheduler().runTaskAsynchronously(DailyJoin.inst(), () -> DailyFileToSQL.inst().FileToSQL(cs));
	}

	/**
	 * encloses FileToSQL
	 * @param cs {@link CommandSender}
	 */
	private void FileToSQL(CommandSender cs) {
		if (hasPermission(cs, ADMIN, MIGRATION)) {
				cs.spigot().sendMessage(Lang.build(Lang.MIGRATION_START_FILETOSQL.get()));
				FileToSQL();
				cs.spigot().sendMessage(Lang.build(Lang.MIGRATION_SUCCESS.get()));
		} else {
			cs.spigot().sendMessage(Lang.build(Lang.NOPERMISSION.get()));
		}
	}

	/**
	 * loads from file and migrates into sql database.
	 */
	public void FileToSQL() {
		File file = new File(FILE_DIRECTORY, FILE_NAME);
		if (file.exists()) {
			try {
				List<DailyPlayer> players = PlayerDAOImpl.inst().getPlayers(false);
				if (players != null && !players.isEmpty()) {
					for (int i = 0; i < players.size(); i++) {
						try {
							DailyPlayer pFile = players.get(i);
							// explicit SQL
							DailyPlayer pSQL = PlayerDAOImpl.inst().getPlayer(pFile.getUuid(), true);
							// check if existing Player
							if (pSQL != null) {
								// existing Player -> merge
								if (TimeUtils.isSameDay(pFile.getFirstjoin(), pSQL.getLastjoin())) {
									// continues logins day overlapping
									// sum days total - 1
									pFile.setDaysTotal(pFile.getDaysTotal() + pSQL.getDaysTotal() - 1);
									// check if days correct ???
									pFile.setDaysConsecutive(
											pFile.getDaysConsecutive() + pSQL.getDaysConsecutive() - 1);
									// lastlogin = file.lastlogin
								} else if (TimeUtils.isDaysBetween(pSQL.getLastjoin(), pFile.getFirstjoin(), 1)) {
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
							PlayerDAOImpl.inst().updatePlayer(pFile, true);
							// remove from working list
							players.remove(i);
							i--;
						} catch (NoSQLConnectionException e) {
							// NoSQLException
							// stop merge and save file
							try {
								PlayerDAOImpl.inst().updatePlayers(players, false);
							} catch (UpdatePlayerException e1) {
								DailyJoin.getDailyLogger().log(Level.SEVERE, "FileToSQL, no Connection and could not save back to file", e);
							}
							TigerConnection.noConnection();
							return;
						} catch (UpdatePlayerException e) {
							DailyJoin.getDailyLogger().log(Level.SEVERE, "FileToSQL", e);
						}
					}
				} else {
					// EmptyList
					DailyJoin.getDailyLogger().log(Level.INFO, "Empty Offline List");
				}
				// finished merging file in sql
				// delete file when players is empty
				if (players == null || players.isEmpty()) {
					if(file.delete()) {
						// file deleted
						DailyJoin.getDailyLogger().log(Level.INFO, "file deleted");
					} else {
						// could not delete file
						DailyJoin.getDailyLogger().log(Level.INFO, "could not delete file");
					}
				}
			} catch (NoSQLConnectionException e1) {
				// should never be
				DailyJoin.getDailyLogger().log(Level.INFO, "that should never happen :°(", e1);
			}
		} else {
			// No File
			DailyJoin.getDailyLogger().log(Level.INFO, "No Offline List");
		}
	}
}

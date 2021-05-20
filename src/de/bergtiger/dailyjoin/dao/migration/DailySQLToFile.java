package de.bergtiger.dailyjoin.dao.migration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

import de.bergtiger.dailyjoin.DailyJoin;
import de.bergtiger.dailyjoin.bdo.DailyPlayer;
import de.bergtiger.dailyjoin.utils.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import de.bergtiger.dailyjoin.dao.impl.PlayerDAOImpl;
import de.bergtiger.dailyjoin.exception.NoSQLConnectionException;
import de.bergtiger.dailyjoin.exception.UpdatePlayerException;
import de.bergtiger.dailyjoin.utils.lang.Lang;

import static de.bergtiger.dailyjoin.utils.permission.TigerPermission.*;

public class DailySQLToFile {

	private DailySQLToFile() {
	}

	/**
	 * runs daily migrate sql_to_file command in its own thread.
	 * @param cs {@link CommandSender}
	 */
	public static void run(CommandSender cs) {
		Bukkit.getScheduler().runTaskAsynchronously(DailyJoin.inst(), () -> new DailySQLToFile().SQLToFile(cs));
	}

	/**
	 * encloses SQLToFile
	 * @param cs {@link CommandSender}
	 */
	private void SQLToFile(CommandSender cs) {
		if (hasPermission(cs, ADMIN, MIGRATION)) {
			try {
				cs.spigot().sendMessage(Lang.build(Lang.MIGRATION_START_SQLTOFILE.get()));
				SQLToFile();
				cs.spigot().sendMessage(Lang.build(Lang.MIGRATION_SUCCESS.get()));
			} catch (NoSQLConnectionException e) {
				cs.spigot().sendMessage(Lang.build(Lang.NOCONNECTION.get()));
				DailyJoin.getDailyLogger().log(Level.SEVERE, "SQLToFile: ", e);
			}
		} else {
			cs.spigot().sendMessage(Lang.build(Lang.NOPERMISSION.get()));
		}
	}

	/**
	 * loads from sql and migrates into file
	 * @throws NoSQLConnectionException when no sql connection available
	 */
	private void SQLToFile() throws NoSQLConnectionException {
		// load both as HashMap with uuid as key
 		// for each SQL migrate(p1, p2)
		HashMap<String, DailyPlayer>
				playersSQL = PlayerDAOImpl.inst().getPlayersAsMap(true),
				playersFile = PlayerDAOImpl.inst().getPlayersAsMap(false);
		// check if there are players in sql
		if (playersSQL != null && !playersSQL.isEmpty()) {
			// check if there are players in file
			if(playersFile != null && !playersFile.isEmpty()) {
				// exists player has to merge
				playersSQL.forEach((uuid, pSQL) -> mergeFromSQL(pSQL, playersFile.remove(uuid)));
			}
			// toList
			// add all sql players (they are merged with possible file players)
			List<DailyPlayer> players = new ArrayList<>(playersSQL.values());
			// add possible left over file players that are not in sql players
			if(playersFile != null && !playersFile.isEmpty())
				players.addAll(playersFile.values());
			// save players
			try {
				PlayerDAOImpl.inst().updatePlayers(players, false);
			} catch (NoSQLConnectionException e) {
				DailyJoin.getDailyLogger().log(Level.SEVERE, "SQLToFile: that should be imposible.", e);
			} catch (UpdatePlayerException e) {
				DailyJoin.getDailyLogger().log(Level.SEVERE, "SQLToFile: could not save file", e);
			}
		}
	}

	/**
	 * merge SQL and file player together.
	 * using same algorithm from FileToSQL
	 * @param pSQL {@link DailyPlayer} from SQL
	 * @param pFile {@link DailyPlayer} from file
	 */
	private void mergeFromSQL(DailyPlayer pSQL, DailyPlayer pFile) {
		if(pSQL != null && pFile != null) {
			// existing Player -> merge
			if (TimeUtils.isSameDay(pFile.getFirstjoin(), pSQL.getLastjoin())) {
				// continues logins day overlapping
				// sum days total - 1
				pSQL.setDaysTotal(pFile.getDaysTotal() + pSQL.getDaysTotal() - 1);
				// check if days correct ???
				pSQL.setDaysConsecutive(
						pFile.getDaysConsecutive() + pSQL.getDaysConsecutive() - 1);
				// lastlogin = file.lastlogin
			} else if (TimeUtils.isDaysBetween(pSQL.getLastjoin(), pFile.getFirstjoin(), 1)) {
				// continues logins
				// sum totaldays
				pSQL.setDaysTotal(pFile.getDaysTotal() + pSQL.getDaysTotal());
				// check if days correct ???
				pSQL.setDaysConsecutive(pFile.getDaysConsecutive() + pSQL.getDaysConsecutive());
				// lastlogin = file.lastlogin
			} else {
				// not continues logins
				// sum totaldays
				pSQL.setDaysTotal(pFile.getDaysTotal() + pSQL.getDaysTotal());
				// day = file.day
				// lastlogin = file.lastlogin
			}
		}
	}
}

package de.bergtiger.dailyjoin.dao.migration;

import de.bergtiger.dailyjoin.DailyJoin;
import de.bergtiger.dailyjoin.bdo.DailyPlayer;
import de.bergtiger.dailyjoin.bdo.TigerList;
import de.bergtiger.dailyjoin.dao.TigerConnection;
import de.bergtiger.dailyjoin.dao.impl.PlayerDAOImpl;
import de.bergtiger.dailyjoin.exception.NoSQLConnectionException;
import de.bergtiger.dailyjoin.exception.UpdatePlayerException;
import de.bergtiger.dailyjoin.utils.PlayerUtils;
import de.bergtiger.dailyjoin.utils.lang.Lang;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import static de.bergtiger.dailyjoin.utils.permission.TigerPermission.*;

public class DailyNameUpdate {

	private static DailyNameUpdate instance;

	public static DailyNameUpdate inst() {
		if (instance == null)
			instance = new DailyNameUpdate();
		return instance;
	}

	private DailyNameUpdate() {
	}

	/**
	 * runs name update in its own thread.
	 *
	 * @param cs {@link CommandSender}
	 * @param args command arguments
	 */
	public static void run(CommandSender cs, String[] args) {
		Bukkit.getScheduler().runTaskAsynchronously(DailyJoin.inst(), () -> DailyNameUpdate.inst().updateNames(cs, args));
	}

	/**
	 * Mojang API allows 600 request per 10 Minutes. To be save using a Max of 250
	 * request per 15 Minute interval. This allows 1000 requests per hour.
	 */
	private void updateNames(CommandSender cs, String[] args) {
		if (hasPermission(cs, ADMIN, UPDATE_NAMES)) {
			// stop command
			if(args != null && args.length >= 2) {
				if(args[1].matches("(?i)(stop|end|abort)")) {
					if(thread != null) {
						endThread();
						// name update stopped
						cs.spigot().sendMessage(Lang.build(Lang.UPDATE_NAME_STOPPED.get()));
					} else {
						cs.spigot().sendMessage(Lang.build(Lang.UPDATE_NAME_NOTRUNNING.get()));
					}
				} else {
					// wrong argument
					cs.spigot().sendMessage(Lang.build(Lang.WRONG_ARGUMENT.get()));
				}
				return;
			}
			// start command
			if (thread == null) {
				try {
					TigerList<DailyPlayer> players = new TigerList<>(PlayerDAOImpl.inst().getPlayers());
					if (!players.isEmpty()) {
						// set page size to 250
						players.setPageSize(250);
						this.players = players;
						// start updating
						updatePlayers(cs);
					} else {
						// no players to update
						cs.spigot().sendMessage(Lang.build(Lang.UPDATE_NAME_NOPLAYERS.get()));
					}
				} catch (NoSQLConnectionException e) {
					e.printStackTrace();
				}
			} else {
				// thread is running, please wait until it is finished
				cs.spigot().sendMessage(
						Lang.build(Lang.UPDATE_NAME_RUNNING.get().replace(Lang.VALUE, getProcessFormatted())));
			}
		} else {
			cs.spigot().sendMessage(Lang.build(Lang.NOPERMISSION.get()));
		}

	}

	/**
	 * checks for each player if cached name is equal to Mojangs name.
	 * if needed cached players are updated.
	 * @param cs {@link CommandSender}
	 */
	private void updatePlayers(CommandSender cs) {	
		// initialize
		currentPlayer = 0;
		updatedPlayers = 0;
		thread = new Thread(() -> {
			try {
				// start update names
				cs.spigot().sendMessage(Lang.build(Lang.UPDATE_NAME_START.get()));
				// update list containing players with new name
				List<DailyPlayer> updates = new ArrayList<>();
				// for (currentPage < lastPage)
				for(int i = 0; i < players.getPageMax(); i++) {
					// next Batch
					for (DailyPlayer dp : players.pageSublist(i)) {
						// getCurrentName
						String currentName = PlayerUtils.getLatestName(dp.getUuid());
						// checkCurrentName with saved Name
						if (!currentName.equalsIgnoreCase(dp.getName())) {
							// needs update
							DailyJoin.getDailyLogger().log(Level.INFO, String.format("updated %s to %s", dp.getName(), currentName));
							// set name
							dp.setName(currentName);
							// add to update list
							updates.add(dp);
						}
						currentPlayer++;
					}
					// show process
					DailyJoin.getDailyLogger().log(Level.INFO, String.format("update Names (%s%%)", getProcessFormatted()));
					// check if hasNext
					if(i < players.getPageMax() - 1)
						// sleep interval
						Thread.sleep(15 * 60 * 1000);
				}
				// update players
				PlayerDAOImpl.inst().updatePlayers(updates);
				updatedPlayers += updates.size();
				// finished update
				DailyJoin.getDailyLogger().log(Level.INFO, "finished update Names.");
				// finished updating names
				cs.spigot().sendMessage(Lang.build(
						Lang.UPDATE_NAME_FINISHED.get().replace(Lang.VALUE, Integer.toString(updatedPlayers))));
			} catch (InterruptedException e) {
				DailyJoin.getDailyLogger().log(Level.WARNING, "interrupted updating Names.");
				cs.spigot().sendMessage(Lang.build(Lang.UPDATE_NAME_EXCEPTION.get().replace(Lang.VALUE, "Interrupted")));
			} catch (NoSQLConnectionException e) {
				DailyJoin.getDailyLogger().log(Level.SEVERE, "stopped updating Names, SQL Connection lost.", e);
				cs.spigot().sendMessage(Lang.build(Lang.UPDATE_NAME_EXCEPTION.get().replace(Lang.VALUE, "No SQL Connection")));
				TigerConnection.noConnection();
			} catch (UpdatePlayerException e) {
				DailyJoin.getDailyLogger().log(Level.SEVERE, "could not update Names.", e);
				cs.spigot().sendMessage(Lang.build(Lang.UPDATE_NAME_EXCEPTION.get().replace(Lang.VALUE, "Update Player")));
			} finally {
				thread = null;
			}
		});
		thread.start();
	}

	private Thread thread;

	private int currentPlayer = 0, updatedPlayers = 0;

	private TigerList<DailyPlayer> players;

	private static final DecimalFormat df2 = new DecimalFormat("#.##");
	
	public String getProcessFormatted() {
		return df2.format(getProcess());
	}
	
	/**
	 * get process value
	 * @return double value
	 */
	public double getProcess() {
		return players != null ? (currentPlayer / (double) players.size()) * 100 : 0.0;
	}
	
	/**
	 * stop update name thread if exists
	 */
	public void endThread() {
		if(thread != null) {
			// end thread
			thread.interrupt();
		}
	}

	/**
	 * show if update names is running
	 * @return true if it is running
	 */
	public boolean hasThread() {
		return thread != null ? thread.isAlive() : false;
	}
}

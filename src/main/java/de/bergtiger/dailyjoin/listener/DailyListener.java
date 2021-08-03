package de.bergtiger.dailyjoin.listener;

import de.bergtiger.dailyjoin.utils.DailyReward;
import de.bergtiger.dailyjoin.utils.PlayerUtils;
import de.bergtiger.dailyjoin.bdo.DailyPlayer;
import de.bergtiger.dailyjoin.DailyJoin;
import de.bergtiger.dailyjoin.dao.impl.PlayerDAOImpl;
import de.bergtiger.dailyjoin.exception.LoadPlayerException;
import de.bergtiger.dailyjoin.exception.NoSQLConnectionException;
import de.bergtiger.dailyjoin.exception.UpdatePlayerException;
import de.bergtiger.dailyjoin.utils.TigerLogger;
import de.bergtiger.dailyjoin.utils.permission.TigerPermission;
import de.bergtiger.dailyjoin.utils.TimeUtils;
import de.bergtiger.dailyjoin.utils.config.DailyConfig;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.logging.Level;

public class DailyListener implements Listener {

	private boolean sql, rewardOnSQLConnectionLost;
	private int delay;

	private static DailyListener instance;

	/**
	 * get DailyListener instance.
	 * 
	 * @return instance of {@link DailyListener}
	 */
	public static DailyListener inst() {
		if (instance == null)
			instance = new DailyListener();
		return instance;
	}

	private DailyListener() {
		setData();
	}

	/**
	 * reload configuration
	 */
	public void reload() {
		setData();
	}

	/**
	 * set configuration data. (delay and saveAsSQL)
	 */
	private void setData() {
		DailyConfig dc = DailyConfig.inst();
		// delay
		if (dc.hasValue(DailyConfig.DELAY))
			delay = dc.getInteger(DailyConfig.DELAY);
		else
			TigerLogger.log(Level.SEVERE, "Missing value for " + DailyConfig.DELAY);
		// data type
		if (dc.hasValue(DailyConfig.DATA_FORMAT))
			sql = dc.getBoolean(DailyConfig.DATA_FORMAT_SQL);
		else
			TigerLogger.log(Level.SEVERE, "Missing value for " + DailyConfig.DATA_FORMAT);
		// rewardOnSQL
		if (dc.hasValue(DailyConfig.REWARD_ON_SQL_CONNECTION_LOST))
			rewardOnSQLConnectionLost = dc.getBoolean(DailyConfig.REWARD_ON_SQL_CONNECTION_LOST);
		else
			TigerLogger.log(Level.SEVERE,
					"Missing value for " + DailyConfig.REWARD_ON_SQL_CONNECTION_LOST);
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		if (TigerPermission.hasPermission(p, TigerPermission.ADMIN, TigerPermission.USER, TigerPermission.JOIN)) {
			long time = this.delay * 20L;
			if (time < 0) {
				time = 10 * 20L;
			}
			Bukkit.getScheduler().scheduleSyncDelayedTask(DailyJoin.inst(), () -> playerJoined(p), time);
		}
	}

	/**
	 * @param p {@link Player}
	 */
	private void playerJoined(Player p) {
		if (p != null && p.isOnline()) {
			// check needed name updates
			updatePlayerName(p);
			// load Player
			DailyPlayer dp = null;
			try {
				dp = load(p.getUniqueId().toString(), sql);
				// process player
				dp = processPlayer(p, dp);
				// if dp was not already online (null if dp was already online)
				if (dp != null) {
					// save
					save(dp, sql);
					// if save successful reward
					DailyReward.inst().giveReward(p, dp.getDaysConsecutive(), dp.getDaysTotal(), dp.getLastjoin());
				}
			} catch (LoadPlayerException e) {
				// could not load DailyPlayer
				if (e.isSQL()) {
					// try loading File -> work with sql not available
					try {
						// no sql, try file instead
						dp = load(p.getUniqueId().toString(), false);
						// process player
						dp = processPlayer(p, dp);
						// if dp was not already online (null if dp was already online)
						if (dp != null) {
							// save
							save(dp, false);
							// if save successful and offline reward
							if (rewardOnSQLConnectionLost) {
								DailyReward.inst().giveReward(p, dp.getDaysConsecutive(), dp.getDaysTotal(),
										dp.getLastjoin());
							}
						}
					} catch (LoadPlayerException loadPlayerException) {
						TigerLogger.log(Level.SEVERE,
								String.format("could absolute not load %s", p.getName()), e);
					} catch (UpdatePlayerException updatePlayerException) {
						updatePlayerException.printStackTrace();
					}
				}
			} catch (UpdatePlayerException e) {
				// could not save DailyPlayer
				if (e.isSQL()) {
					// try saving File
					try {
						save(dp, false);
						// if save successful and offline reward
						if (rewardOnSQLConnectionLost) {
							DailyReward.inst().giveReward(p, dp.getDaysConsecutive(), dp.getDaysTotal(),
									dp.getLastjoin());
						}
					} catch (UpdatePlayerException updatePlayerException) {
						TigerLogger.log(Level.SEVERE,
								String.format("could absolute not save %s", dp.getName()), e);
					}
				}
			}
		}
	}

	/**
	 * updates names when a player joins.
	 * @param p {@link Player} joined
	 */
	private void updatePlayerName(Player p) {
		if(p != null) {
			try {
				List<DailyPlayer> players = PlayerDAOImpl.inst().getPlayers(p.getName());
				if(players != null && !players.isEmpty()) {
					// update name, should be only one
					for(int i = 0; i < players.size(); i++) {
						DailyPlayer dp = players.get(i);
						String currentName;
						if((currentName = PlayerUtils.getLatestName(dp.getUuid())) != null && !currentName.equalsIgnoreCase(dp.getName())) {
							// new Name
							dp.setName(currentName);
							TigerLogger.log(Level.INFO, String.format("Name changed from %s to %s", p.getName(), currentName));
						} else {
							// remove player, needs no update
							players.remove(i);
							i--;
						}
					}
					// save updates
					PlayerDAOImpl.inst().updatePlayers(players);
				}
			} catch (NoSQLConnectionException e) {
				TigerLogger.log(Level.SEVERE, "updatePlayerName: ", e);
			} catch (UpdatePlayerException e) {
				TigerLogger.log(Level.SEVERE, "could not save updated names", e);
			}
		}
	}
	
	/**
	 * process player. check if player was already online and set consecutive and
	 * total days.
	 * 
	 * @param p  {@link Player} for new DailyPlayer
	 * @param dp {@link DailyPlayer} to process
	 * @return DailyPlayer with modified days or null if Player was already online
	 */
	private DailyPlayer processPlayer(Player p, DailyPlayer dp) {
		if (dp != null) {
			// existing Player
			// check name update
			dp.setName(p.getName());
			// check if Player was online today
			if (!checkPlayerOnlineToday(dp)) {
				// Player was not online today and has not given a reward yet
				// check consecutive days
				if (checkPlayerOnlineYesterday(dp)) {
					// Player was online yesterday
					dp.setDaysConsecutive(dp.getDaysConsecutive() + 1);
				} else {
					// Player was not online yesterday
					dp.setDaysConsecutive(1);
				}
				// add total days
				dp.setDaysTotal(dp.getDaysTotal() + 1);
				dp.setLastjoin(Timestamp.from(Instant.now()));
			} else {
				// Player was online today and has given reward
				return null;
			}
		} else {
			// new Player
			dp = new DailyPlayer(p.getName(), p.getUniqueId().toString());
		}
		return dp;
	}

	/**
	 * check if player was already online today
	 * 
	 * @param dp {@link DailyPlayer} to check
	 * @return true if last join equals today
	 */
	private boolean checkPlayerOnlineToday(DailyPlayer dp) {
		if (dp != null) {
			return TimeUtils.isToday(dp.getLastjoin());
		}
		return false;
	}

	/**
	 * check if player was online yesterday.
	 * 
	 * @param dp {@link DailyPlayer} to check
	 * @return true if last join equals yesterday
	 */
	private boolean checkPlayerOnlineYesterday(DailyPlayer dp) {
		if (dp != null) {
			return TimeUtils.isYesterday(dp.getLastjoin());
		}
		return false;
	}

	/**
	 * load DailyPlayer from uuid.
	 * 
	 * @param uuid player to load
	 * @param sql  true when load from database, else file
	 * @return {@link DailyPlayer} or null if player is new
	 * @throws LoadPlayerException could not load DailyPlayer because of an IOException
	 */
	private DailyPlayer load(String uuid, boolean sql) throws LoadPlayerException {
		try {
			return PlayerDAOImpl.inst().getPlayer(uuid, sql);
		} catch (NoSQLConnectionException e) {
			TigerLogger.log(Level.WARNING, String.format("could not load(%s) from Database", uuid), e);
			throw new LoadPlayerException(true, uuid);
		}
	}

	/**
	 * save DailyPlayer.
	 * 
	 * @param dp  {@link DailyPlayer} to save
	 * @param sql true when save in database, else file
	 * @throws UpdatePlayerException could not save DailyPlayer because of an IOException
	 */
	private void save(DailyPlayer dp, boolean sql) throws UpdatePlayerException {
		try {
			PlayerDAOImpl.inst().updatePlayer(dp, sql);
		} catch (NoSQLConnectionException e) {
			TigerLogger.log(Level.WARNING, String.format("could not save(%s) in Database", dp.getName()), e);
			throw new UpdatePlayerException(true, dp);
		}
	}
}

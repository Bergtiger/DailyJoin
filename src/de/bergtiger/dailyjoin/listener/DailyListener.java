package de.bergtiger.dailyjoin.listener;

import de.bergtiger.dailyjoin.utils.DailyReward;
import de.bergtiger.dailyjoin.bdo.DailyPlayer;
import de.bergtiger.dailyjoin.dailyjoin;
import de.bergtiger.dailyjoin.dao.impl.file.DailyFile;
import de.bergtiger.dailyjoin.exception.LoadPlayerException;
import de.bergtiger.dailyjoin.exception.NoSQLConnectionException;
import de.bergtiger.dailyjoin.exception.SavePlayerException;
import de.bergtiger.dailyjoin.exception.UpdatePlayerException;
import de.bergtiger.dailyjoin.utils.TigerPermission;
import de.bergtiger.dailyjoin.utils.TimeUtils;
import de.bergtiger.dailyjoin.utils.config.DailyConfig;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import de.bergtiger.dailyjoin.dao.TigerConnection;

import javax.annotation.Nonnull;
import java.util.logging.Level;

public class DailyListener implements Listener {

	private boolean sql, rewardOnSQLConnectionLost;
	private int delay;

	private static DailyListener instance;

	/**
	 * get DailyListener instance.
	 * @return instance of DailyListener
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
		if(dc.hasValue(DailyConfig.DELAY))
			delay = dc.getInteger(DailyConfig.DELAY);
		else
			dailyjoin.getDailyLogger().log(Level.SEVERE, "Missing value for " + DailyConfig.DELAY);
		// data type
		if(dc.hasValue(DailyConfig.DATA_FORMAT))
			sql = dc.getBoolean(DailyConfig.DATA_FORMAT_SQL);
		else
			dailyjoin.getDailyLogger().log(Level.SEVERE, "Missing value for " + DailyConfig.DATA_FORMAT);
		// rewardOnSQL
		if(dc.hasValue(DailyConfig.REWARD_ON_SQL_CONNECTION_LOST))
			rewardOnSQLConnectionLost = dc.getBoolean(DailyConfig.REWARD_ON_SQL_CONNECTION_LOST);
		else
			dailyjoin.getDailyLogger().log(Level.SEVERE, "Missing value for " + DailyConfig.REWARD_ON_SQL_CONNECTION_LOST);
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		if(TigerPermission.hasPermission(p, TigerPermission.ADMIN, TigerPermission.USER, TigerPermission.JOIN)) {
			long time = this.delay * 20L;
			if (time < 0) {
				time = 10 * 20L;
			}
			Bukkit.getScheduler().scheduleSyncDelayedTask(dailyjoin.inst(), () -> playerJoined(p), time);
		}
	}

	/**
	 * @param p Player
	 */
	private void playerJoined(Player p) {
		if(p != null && p.isOnline()) {
			// load Player
			DailyPlayer dp = null;
			try {
				dp = load(p.getUniqueId().toString(), sql);
				// process player
				dp = processPlayer(p, dp);
				// if dp was not already online (null if dp was already online)
				if(dp != null) {
					// save
					save(dp, sql);
					// if save successful reward
					DailyReward.inst().giveReward(p, dp.getDaysConsecutive(), dp.getDaysTotal(), dp.getLastjoin());
				}
			} catch (LoadPlayerException e) {
				// could not load DailyPlayer
				if(e.isSQL()) {
					// try loading File -> work with sql not available
					try {
						// no sql, try file instead
						dp = load(p.getUniqueId().toString(), false);
						// process player
						dp = processPlayer(p, dp);
						// if dp was not already online (null if dp was already online)
						if(dp != null) {
							// save
							save(dp, false);
							// if save successful and offline reward
							if(rewardOnSQLConnectionLost) {
								DailyReward.inst().giveReward(p, dp.getDaysConsecutive(), dp.getDaysTotal(), dp.getLastjoin());
							}
						}
					} catch (LoadPlayerException loadPlayerException) {
						dailyjoin.getDailyLogger().log(Level.SEVERE, String.format("could absolute not load %s", p.getName()), e);
					} catch (SavePlayerException savePlayerException) {
						savePlayerException.printStackTrace();
					}
				}
			} catch (SavePlayerException e) {
				// could not save DailyPlayer
				if(e.isSQL()) {
					// try saving File
					try {
						save(dp, false);
						// if save successful and offline reward
						if(rewardOnSQLConnectionLost) {
							DailyReward.inst().giveReward(p, dp.getDaysConsecutive(), dp.getDaysTotal(), dp.getLastjoin());
						}
					} catch (SavePlayerException savePlayerException) {
						dailyjoin.getDailyLogger().log(Level.SEVERE, String.format("could absolute not save %s", dp.getName()), e);
					}
				}
			}
		}
	}

	/**
	 * process Player.
	 * check if player was already online and set consecutive and total days.
	 * @param p Player for new DailyPlayer
	 * @param dp DailyPlayer to process
	 * @return DailyPlayer with modified days or null if Player was already online
	 */
	private DailyPlayer processPlayer(Player p, DailyPlayer dp) {
		if(dp != null) {
			// existing Player
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
	 * check if Player was already online today
	 * @param dp DailyPlayer to check
	 * @return true if lastjoin equals today
	 */
	private boolean checkPlayerOnlineToday(DailyPlayer dp) {
		if(dp != null) {
			return TimeUtils.isToday(dp.getLastjoin());
		}
		return false;
	}

	/**
	 * check if Player was online yesterday.
	 * @param dp DailyPlayer to check
	 * @return true if lastjoin equals yesterday
	 */
	private boolean checkPlayerOnlineYesterday(DailyPlayer dp) {
		if(dp != null) {
			return TimeUtils.isYesterday(dp.getLastjoin());
		}
		return false;
	}

	/**
	 * load DailyPlayer from uuid.
	 * @param uuid Player to load
	 * @param sql true when load from Database, else File
	 * @return DailyPlayer or null if Player is new
	 * @throws LoadPlayerException could not load DailyPlayer because of an IOException
	 */
	private DailyPlayer load(@Nonnull String uuid, boolean sql) throws LoadPlayerException {
		if(sql) {
			try {
				return TigerConnection.inst().getPlayerDAO().getPlayer(uuid);
			} catch (NoSQLConnectionException e) {
				dailyjoin.getDailyLogger().log(Level.WARNING, String.format("could not load(%s) from Database", uuid), e);
				throw new LoadPlayerException(true, uuid);
			}
		}
		return new DailyFile().load(uuid);
	}

	/**
	 * save DailyPlayer.
	 * @param dp DailyPlayer to save
	 * @param sql true when save in Database, else File
	 * @throws SavePlayerException could not save DailyPlayer because of an IOException
	 */
	private void save(@Nonnull DailyPlayer dp, boolean sql) throws SavePlayerException {
		if (sql) {
			try {
				TigerConnection.inst().getPlayerDAO().updatePlayer(dp);
			} catch (NoSQLConnectionException e) {
				dailyjoin.getDailyLogger().log(Level.WARNING, String.format("could not save(%s) in Database", dp.getName()), e);
				throw new SavePlayerException(true, dp);
			} catch (UpdatePlayerException e) {
				e.printStackTrace();
			}
		} else {
			new DailyFile().save(dp);
		}
	}
}

package de.bergtiger.dailyjoin.utils;

import java.io.File;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import de.bergtiger.dailyjoin.DailyJoin;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import de.bergtiger.dailyjoin.utils.config.DailyConfig;
import de.bergtiger.dailyjoin.utils.lang.Lang;

public class DailyReward {

	private List<String> daily;
	private List<String> birthday;
	private HashMap<Integer, List<String>> rewardsConsecutive = new HashMap<>();
	private HashMap<Integer, List<String>> rewardsTotal = new HashMap<>();

	private static DailyReward instance;

	public static DailyReward inst() {
		if (instance == null)
			instance = new DailyReward();
		return instance;
	}

	private DailyReward() {
		setData();
	}

	/**
	 * load configuration
	 */
	private void setData() {
		//TODO
		this.daily = DailyJoin.inst().getConfig().getStringList("config.daily");
		this.birthday = DailyJoin.inst().getConfig().getStringList("config.birthday");
		this.loadDaysConsecutive();
		this.loadDaysTotal();
	}

	/**
	 * load configuration for consecutive days rewards.
	 */
	private void loadDaysConsecutive() {
		if (DailyConfig.inst().hasValue(DailyConfig.FILE_DAYS_CONSECUTIVE)) {
			File file = new File(DailyConfig.PLUGIN_DIRECTORY, DailyConfig.inst().getValue(DailyConfig.FILE_DAYS_CONSECUTIVE));
			if (file.exists()) {
				rewardsConsecutive.clear();
				FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
				Set<String> keys = cfg.getKeys(false);
				if (keys != null && !keys.isEmpty()) {
					keys.forEach(k -> {
						try {
							rewardsConsecutive.put(Integer.parseInt(k), cfg.getStringList(k));
						} catch (NumberFormatException e) {
							DailyJoin.getDailyLogger().log(Level.WARNING, String
									.format("loadDaysConsecutive: %s is not a valid number and will be ignored", k), e);
						}
					});
				} else {
					DailyJoin.getDailyLogger().log(Level.INFO, "loadDaysConsecutive: No Keys");
				}
			} else {
				DailyJoin.getDailyLogger().log(Level.INFO,
						String.format("loadDaysConsecutive: No file '%s'", file.getAbsolutePath()));
			}
		} else {
			DailyJoin.getDailyLogger().log(Level.WARNING, "loadDaysConsecutive: missing file in configuration");
		}
	}

	/**
	 * load configuration for total days rewards.
	 */
	private void loadDaysTotal() {
		if (DailyConfig.inst().hasValue(DailyConfig.FILE_DAYS_TOTAL)) {
			File file = new File(DailyConfig.PLUGIN_DIRECTORY, DailyConfig.inst().getValue(DailyConfig.FILE_DAYS_TOTAL));
			if (file.exists()) {
				rewardsTotal.clear();
				FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
				Set<String> keys = cfg.getKeys(false);
				if (keys != null && !keys.isEmpty()) {
					keys.forEach(k -> {
						try {
							this.rewardsTotal.put(Integer.parseInt(k), cfg.getStringList(k));
						} catch (NumberFormatException e) {
							DailyJoin.getDailyLogger().log(Level.WARNING,
									String.format("loadDaysTotal: %s is not a valid number and will be ignored", k), e);
						}
					});
				} else {
					DailyJoin.getDailyLogger().log(Level.INFO, "loadDaysTotal: No Keys");
				}
			} else {
				DailyJoin.getDailyLogger().log(Level.INFO,
						String.format("loadDaysTotal: No file '%s'", file.getAbsolutePath()));
			}
		} else {
			DailyJoin.getDailyLogger().log(Level.WARNING, "loadDaysTotal: missing file in configuratio");
		}
	}

	/**
	 * give player his rewards.
	 * 
	 * @param p               {@link Player} who gets the reward
	 * @param daysConsecutive days consecutive from this player
	 * @param daysTotal	      days total from this player
	 * @param t	              now
	 */
	public void giveReward(Player p, int daysConsecutive, int daysTotal, Timestamp t) {
		giveDaily(p, daysConsecutive);
		giveDaysConsecutive(p, daysConsecutive);
		giveDaysTotal(p, daysTotal);
		giveBirthday(p, daysTotal, t);
	}

	/**
	 * give daily rewards
	 * 
	 * @param p		{@link Player} who gets the reward
	 * @param day 	//TODO
	 */
	private void giveDaily(Player p, int day) {
		if ((daily != null) && (!daily.isEmpty())) {
			performCmds(p, daily);
			p.spigot().sendMessage(Lang.build(Lang.REWARD_DAILY.get().replace(Lang.VALUE, Integer.toString(day))));
		}
	}

	/**
	 * give rewards for consecutive days
	 * 
	 * @param p {@link Player} who gets the reward
	 * @param day player was online
	 */
	private void giveDaysConsecutive(Player p, int day) {
		if ((rewardsConsecutive != null) && rewardsConsecutive.containsKey(day)) {
			performCmds(p, rewardsConsecutive.get(day));
			Bukkit.spigot().broadcast(Lang.build(Lang.REWARD_DAYS_CONSECUTIVE.get()
					.replace(Lang.VALUE, Integer.toString(day)).replace(Lang.PLAYER, p.getName())));
		}
	}

	/**
	 * give rewards for total days
	 * 
	 * @param p {@link Player} who gets the reward
	 * @param totaldays player was online
	 */
	private void giveDaysTotal(Player p, int totaldays) {
		if ((rewardsTotal != null) && rewardsTotal.containsKey(totaldays)) {
			performCmds(p, rewardsTotal.get(totaldays));
			Bukkit.spigot().broadcast(Lang.build(Lang.REWARD_DAYS_TOTAL.get()
					.replace(Lang.VALUE, Integer.toString(totaldays)).replace(Lang.PLAYER, p.getName())));
		}
	}

	/**
	 * give birthday rewards
	 * 
	 * @param p {@link Player} who gets the reward
	 * @param totaldays //TODO
	 * @param t now
	 */
	private void giveBirthday(Player p, int totaldays, Timestamp t) {
		if ((birthday != null) && (!birthday.isEmpty())) {
			Calendar today = Calendar.getInstance();
			Calendar time = today;
			time.setTimeInMillis(t.getTime());
			if ((time.get(Calendar.YEAR) != today.get(Calendar.YEAR))
					&& ((time.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH))
							&& (time.get(Calendar.MONTH) == today.get(Calendar.MONTH)))) {
				performCmds(p, this.birthday);
				Bukkit.spigot().broadcast(Lang.build(Lang.REWARD_BIRTHDAY.get()
						.replace(Lang.VALUE, Integer.toString(totaldays)).replace(Lang.PLAYER, p.getName())));
			}
		}
	}

	/**
	 * perform a list of commands replaces '@p' and 'Lang.PLAYER'.
	 * @param p {@link Player} who gets the reward
	 * @param list of commands
	 */
	private void performCmds(Player p, List<String> list) {
		for (int i = 0; i < list.size(); i++) {
			Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),
					list.get(i).replace(Lang.PLAYER, p.getName()).replace("@p", p.getName()));
		}
	}

	public void reload() {
		setData();
	}
}

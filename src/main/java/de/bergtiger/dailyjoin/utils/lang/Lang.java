package de.bergtiger.dailyjoin.utils.lang;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.hover.content.Text;

public enum Lang implements Constants {

	/** CommandSender has no Permission.*/
	NOPERMISSION
		("&cNo Permission"),
	/** TODO*/
	NOUUID
		("&7Not a correct UUID or Player is not online."),
	/** No such file exists (file as VALUE)*/
	NOFILE
		(String.format("&7Could not find file '%s'.", VALUE)),
	/** Not a correct Number(shows wrong input as VALUE)*/
	NONUMBER
		(String.format("'%s' is not a valid number", VALUE)),
	/** No such player (shows input as PLAYER)*/
	NOPLAYER
		(String.format("&7No such player '%s' could be found", PLAYER)),
	/** No database connection*/
	NOCONNECTION
		("&7Error: SQL-Connection"),
	/** No entries in the database*/
	NOLIST
		("No entries in the database"),

	WRONG_ARGUMENT
		("&7Wrong Argument. Please Check your Command."),

	/** Time format !WITHOUT COLORS!*/
	FORMAT_TIME
		("dd.MM.yyyy, HH:mm"),
	//TODO
	ONLY_SQL
		("&7This Command works only with SQL."),
	
	/** join message days as VALUE*/
	REWARD_DAILY
		(String.format("&7Du hast dich bereits den &e%s&7. Tag in Folge eingeloggt.", VALUE)),
	/** broadcast birthday player as PLAYER*/
	REWARD_BIRTHDAY
		(String.format("&e%s&7hat heute seinen Server-Geburtstag.", PLAYER)),
	/** broadcast player as PLAYER, days as VALUE*/
	REWARD_DAYS_CONSECUTIVE
		(String.format("&e%s&7ist bereits &e%s &7Tage in Folge auf dem Server.", PLAYER, VALUE)),
	/** broadcast player as PLAYER, days as VALUE*/
	REWARD_DAYS_TOTAL
		(String.format("&e%s &7ist bereits insgesamt &e%s &7Tage auf dem Server.", PLAYER, VALUE)),

	/** config change wrong argument value as VALUE, configuration identifier as TYPE*/
	CONFIG_WRONG_ARGUMENT
		(String.format("&e%s &cis not a valid value for &6%s", VALUE, TYPE)),
	/** config changed success configuration identifier as TYPE, DATA old value, VALUE new value*/
	CONFIG_CHANGED_SUCCESS
		(String.format("&achanged &6%s &afrom &e%s &ato &e%s", TYPE, DATA, VALUE)),
	/** config change exception restore old value as DATA, VALUE as new value that did not work, TYPE identifier*/
	CONFIG_CHANGED_ERROR
		(String.format("&ccould not change &e%s &cto &e%s &crestore old value &e%s", TYPE, VALUE, DATA)),

	INFO_CMD
		("&b/daily  -  &7zeigt alle g??ltigen Befehle"),
	INFO_TOP
		("&b/daily top [day, totaldays] [value] -  &7zeigt top Liste"),
	INFO_SET
		("&b/daily set [player, uuid] [day, totaldays] [value]  -  &7Setzt dem Spieler day/totaldays value"),
	INFO_ADD
		("&b/daily add [player, uuid] [day, totaldays] [value]  -  &7Addiert dem Spieler day/totaldays value"),
	INFO_INFO
		("&b/daily info  -  &7zeigt Plugin version"),
	INFO_PLAYER
		("&b/daily player [player, uuid]  -  &7zeigt die Daten des Spielers"),
	INFO_RELOAD
		("&b/daily reload  -  &7l??dt config neu"),
	INFO_CONFIG
		("&b/daily config [ma/system/reward/oldfiles/delay] [true/false/in case of delay number]  -  &7??ndert die config"),
	INFO_MIGRATION
		("&b/daily migration [file_to_sql, sql_to_file] - &7migrates data source"),
	INFO_UPDATE_NAMES
		("&b/daily update_names - &7updates all player names in database"),

	INFO_HOVER_CMD
		("&eshow all commands"),
	INFO_HOVER_TOP
		("&eshow top player\n&etypes:"),
	INFO_HOVER_SET
		("&eset a value from a player"),
	INFO_HOVER_ADD
		("&eadd a value to a player\nremoving from a player with negative value"),
	INFO_HOVER_INFO
		("&eshow plugin info and configuration"),
	INFO_HOVER_PLAYER
		("&eshow player statistic"),
	INFO_HOVER_RELOAD
		("&ereloads plugin configuration and cache"),
	INFO_HOVER_CONFIG
		("&emodify plugin configuration"),
	INFO_HOVER_MIGRATION
		("&emigrate from file/sql to sql/file"),
	INFO_HOVER_UPDATE_NAMES
		("&eupdate all player names in database"),
//	Update Name
	/** start updating names*/
	UPDATE_NAME_START("&aStarting updating player names."),
	/** finished updating names, VALUE amount of updated names*/
	UPDATE_NAME_FINISHED
		(String.format("&aFinished updating player names. Updated &e%s players", VALUE)),
	/** no players for updating names*/
	UPDATE_NAME_NOPLAYERS("&cNo players found to check or update."),
	/** updating names in progress, wait till new process can be started*/
	UPDATE_NAME_RUNNING
		(String.format("&cUpdate is running&e, &cplease wait until it is finished &6(&e%s&f%%&6)", VALUE)),
	/** successfully stopped updating names process*/
	UPDATE_NAME_STOPPED
		("&cStopped updating names"),
	/** updating names not running, can't stop it*/
	UPDATE_NAME_NOTRUNNING
		("&cupdating names not running"),
	/** updating names stopped VALUE as reason why it stopped*/
	UPDATE_NAME_EXCEPTION
		(String.format("&cStopped updating names&6: &e%s", VALUE)),
		
//	DailyAdd
//		("&7/daily add [player/uuid] [day, totaldays] [value]"),
	/** player as PLAYER, column/type as DATA, value/days as VALUE*/
	DAILY_ADD_SUCCESS
		(String.format("&aBei dem Spieler mit der UUID: &e%s &awurde &e%s &aum &e%s &aerh??ht.", PLAYER, DATA, VALUE)),
	/** player as PLAYER, column/type as DATA, value/days as VALUE*/
	DAILY_ADD_ERROR
		(String.format("Could not add ", PLAYER, DATA, VALUE)),

//	DailySet
//		("&7/daily set [player/uuid] [day, totaldays] [value]"),
	/** player as PLAYER, column/type as DATA, value/days as VALUE*/
	DAILY_SET_SUCCESS
		(String.format("&aBei dem Spieler mit der UUID: &e%s &awurde &e%s &aauf &e%s &agesetzt.", PLAYER, DATA, VALUE)),
	/** player as PLAYER, column/type as DATA, value/days as VALUE */
	DAILY_SET_ERROR
		(String.format("Could not save ", PLAYER, DATA, VALUE)),

	DAILY_RELOAD
		("&7DailyJoin reloaded."),

	/** header for list, VALUE for searched parameter */
	TOP_HEADER
			(String.format("&a----<[&6Top Player&f(&e%s&f)&a]>----", VALUE)),
	/** footer for list, current page as PAGE, PAGE_MAX for max page in this list*/
	TOP_FOOTER
			(String.format("&a---<(&e%s&f/&e%s&a)>----", PAGE, PAGE_MAX)),
	/** show next page button*/
	TOP_NEXT
			("&6next"),
	/** show previous page button*/
	TOP_PREV
			("&6prev"),
	/** shows player as PLAYER, searched value as VALUE*/
	TOP_PLAYER
			(String.format("&e%s: &7%s", PLAYER, VALUE)),
	/** hover for next button*/
	TOP_HOVER_NEXT
			("&fShows previous page"),
	/** hover for previous button*/
	TOP_HOVER_PREV
			("&fShows next page"),

	/** player name for hover as VALUE*/
	HOVER_PLAYER_NAME
			(String.format("&ePlayer&6:&f %s", VALUE)),
	/** player uuid for hover as VALUE */
	@Deprecated
	HOVER_PLAYER_UUID
			(String.format("&eUUid&6:&f %s", VALUE)),
	/** player firstjoin for hover as VALUE*/
	HOVER_PLAYER_FIRSTJOIN
			(String.format("&eFirstjoin&6:&f %s", VALUE)),
	/** player lastjoin for hover as VALUE*/
	HOVER_PLAYER_LASTJOIN
			(String.format("&eLastjoin&6:&f %s", VALUE)),
	/** player days total for hover as VALUE*/
	HOVER_PLAYER_DAYS_TOTAL
			(String.format("&eDays total&6:&f %s", VALUE)),
	/** player days consecutive for hover as VALUE*/
	HOVER_PLAYER_DAYS_CONSECUTIVE
			(String.format("&eDays consecutive&6:&f %s", VALUE)),
	/** migration begin to file*/
	MIGRATION_START_SQLTOFILE ("&aStarted migration from &eSQL &ato &eFile."),
	/** migration begin to sql*/
	MIGRATION_START_FILETOSQL ("&aStarted migration from &eFile &ato &eSQL"),
	/** migration successfully ended*/
	MIGRATION_SUCCESS ("&aFinished migration."),

	/** plugin statistic menu header*/
	PLUGIN_HEADER
		("&a----<[&6DailyJoin&a]>----"),
	/** plugin statistic menu footer*/
	PLUGIN_FOOTER
		("&a---------------------"),
	/** plugin statistic version as VALUE*/
	PLUGIN_VERSION
		(String.format("&eVersion&6: &7%s", VALUE)),
	/** plugin statistic time format as VALUE*/
	PLUGIN_TIMEFORMAT
		(String.format("&eTimeFormat&6: &7%s", VALUE)),
	/** plugin statistic file or sql as VALUE*/
	PLUGIN_SYSTEM
		(String.format("&eSystem&6: &7%s", VALUE)),
	/** plugin statistic sql connected as VALUE*/
	PLUGIN_CONNECTED
		(String.format("&eConnected&6: &7%s", VALUE)),
	/** plugin statistic reward as VALUE*/
	PLUGIN_REWARD_RECONNECTION
		(String.format("&eReward on Reconnection&6: &7%s", VALUE)),
	/** plugin statistic merge as VALUE*/
	PLUGIN_MERGE_ON_CONNECTION
		(String.format("&eMerge on connection&6: &7%s", VALUE)),
	/** plugin statistic reward delay as VALUE*/
	PLUGIN_DELAY
		(String.format("&eRewardDelay&6: &7%s", VALUE)),
	/** plugin statistic rewards */
	PLUGIN_PAGE_SIZE
		(String.format("&ePageSize&6: &7%s", VALUE)),
	/** plugin statistic update names running as VALUE*/
	PLUGIN_UPDATE_NAMES
		(String.format("&eUpdateNames&6: &7%s", VALUE)),
	/** plugin statistic update names running as VALUE with process value as DATA*/
	PLUGIN_UPDATE_NAMESVALUE
		(String.format("&eUpdateNames&6: &7%s &6(&e%s&f%%&6)", VALUE, DATA)),

	/** player statistic header player name as PLAYER*/
	PLAYER_HEADER
		(String.format("&a----<[&6%s&a]>----", PLAYER)),
	/** player statistic footer*/
	PLAYER_FOOTER
		("&a--------------------"),
	/** player statistic first join as VALUE*/
	PLAYER_JOIN_FIRST
		(String.format("&eFirstJoin: &7%s", VALUE)),
	/** player statistic last join as VALUE*/
	PLAYER_JOIN_LAST
		(String.format("&eLastJoin: &7%s", VALUE)),
	/** player statistic days consecutive as VALUE*/
	PLAYER_DAYS_CONSECUTIVE
		(String.format("&eDay: &7%s", VALUE)),
	/** player statistic days total as VALUE*/
	PLAYER_DAYS_TOTAL
		(String.format("&eTotalDays: &7%s", VALUE));
	
	private String message;
	
	Lang(String message) {
		this.message = message;
	}
	
	public String get() {
		return message;
	}

	public void set(String message) {
		this.message = message;
	}
	
	public String colored() {
		return ChatColor.translateAlternateColorCodes('&', message);
	}
	
	/**
	 * Builds a TextComponent with text and colors
	 * @param args - Text
	 * @return TextComponent
	 */
	public static TextComponent build(String args) {
		return build(args, null, null, null);
	}
	
	/**
	 * Builds a TextComponent with colored text, and extras
	 * @param args - text
	 * @param cmd - onClick command
	 * @param hover - onHover text
	 * @param cmd_suggestion - onClick suggestion
	 * @return TextComponent
	 */
	public static TextComponent build(String args, String cmd, String hover, String cmd_suggestion) {
		return buildTC2(args, cmd, hover != null ? rgbColor(new TextComponent(hover), null) : null, cmd_suggestion);
	}
	
	/**
	 * Adds Extras to TextComponent no Color or anything!
	 * @param args - String
	 * @param cmd - onClick command
	 * @param hover - onHover text
	 * @param cmd_suggestion - command suggestion will be set in chat
	 * @return TextComponent
	 */
	public static TextComponent buildTC2(String args, String cmd, BaseComponent hover, String cmd_suggestion) {
		if (args != null) {
			TextComponent tc = (TextComponent) rgbColor(new TextComponent(args),null);
			if (cmd != null && !cmd.isEmpty())
				tc.setClickEvent(new ClickEvent(Action.RUN_COMMAND, cmd));
			if (hover != null) {
				tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
						new Text(new BaseComponent[] {hover})));
			}
			if (cmd_suggestion != null && !cmd_suggestion.isEmpty())
				tc.setClickEvent(new ClickEvent(Action.SUGGEST_COMMAND, cmd_suggestion));
			return tc;
		}
		return null;
	}
	
	/**
	 * builds recursive BaseComponent with RGB colors
	 * @param bc - BaseComponent, TextComponent
	 * @param color - Color for BaseComponent
	 * @return colored BaseComponent
	 */
	private static BaseComponent rgbColor(BaseComponent bc, ChatColor color) {
		if(bc instanceof TextComponent) {
			if(color != null)
				bc.setColor(color);
			// getText
			String text = ((TextComponent)bc).getText();
			// if text contains hexColor
			if(text.contains("&#")) {
				// find first hexColor
				int i = text.indexOf("&#");
				// substring first part(old color)
				((TextComponent)bc).setText(ChatColor.translateAlternateColorCodes('&', text.substring(0, i)));
				// substring last part(new color)
				bc.addExtra(
						rgbColor(
								new TextComponent(
										text.substring(i + 8)),
										ChatColor.of(text.substring(i + 1, i + 8))));
			} else {
				// text with replaced + legacy ChatColor
				((TextComponent)bc).setText(ChatColor.translateAlternateColorCodes('&', text));
			}
		}
		return bc;
	}
}

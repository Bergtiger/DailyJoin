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
	
	DAILY_INFO_CMD
		("&b/daily  -  &7zeigt alle gültigen Befehle"),
	DAILY_INFO_TOP
		("&b/daily top [day, totaldays] [value] -  &7zeigt top Liste"),
	DAILY_INFO_SET
		("&b/daily set [player, uuid] [day, totaldays] [value]  -  &7Setzt dem Spieler day/totaldays value"),
	DAILY_INFO_ADD
		("&b/daily add [player, uuid] [day, totaldays] [value]  -  &7Addiert dem Spieler day/totaldays value"),
	DAILY_INFO_INFO
		("&b/daily info  -  &7zeigt Plugin version"),
	DAILY_INFO_RELOAD
		("&b/daily reload  -  &7lädt config neu"),
	DAILY_INFO_PLAYER
		("&b/daily player [player, uuid]  -  &7zeigt die Daten des Spielers"),
	DAILY_INFO_CONFIG
		("&b/daily config [ma/system/reward/oldfiles/delay] [true/false/in case of delay number]  -  &7ändert die config"),
	
//	DailyAdd
//		("&7/daily add [player/uuid] [day, totaldays] [value]"),
	/** player as PLAYER, column/type as DATA, value/days as VALUE*/
	DAILY_ADD_SUCCESS
		(String.format("&aBei dem Spieler mit der UUID: &e%s &awurde &e%s &aum &e%s &aerhöht.", PLAYER, DATA, VALUE)),
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

	TOP_PLAYER_DAYS_CONSECUTIVE
		("&a----<[&6Top Day&a]>----"),
	TOP_PLAYER_DAYS_TOTAL
		("&a----<[&6Top Totaldays&a]>----"),
	/** top list player as PLAYER, VALUE*/
	TOP_PLAYER_LIST
		(String.format("&e%s: &7%s", PLAYER, VALUE)),

	/** plugin statistic menu header*/
	PLUGIN_HEADER
		("&a----<[&6DailyJoin&a]>----"),
	/** plugin statistic menu footer*/
	PLUGIN_FOOTER
		("&a---------------------"),
	/** plugin statistic version as VALUE*/
	PLUGIN_VERSION
		(String.format("&eVersion: &7%s", VALUE)),
	/** plugin statistic time format as VALUE*/
	PLUGIN_TIMEFORMAT
		(String.format("&eTimeFormat: &7%s", VALUE)),
	/** plugin statistic file or sql as VALUE*/
	PLUGIN_SYSTEM
		(String.format("&eSystem: &7%s", VALUE)),
	/** plugin statistic reward as VALUE*/
	PLUGIN_REWARD_RECONNECTION
		(String.format("&eReward on Reconnection: &7%s", VALUE)),
	/** plugin statistic reward delay as VALUE*/
	PLUGIN_DELAY
		(String.format("&eRewardDelay: &7%s", VALUE)),
	/** plugin statistic rewards */
	PLUGIN_PAGE_SIZE
		(String.format("&eTopPlayer: &7%s", VALUE)),

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
				((TextComponent)bc).setText(text.substring(0, i));
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

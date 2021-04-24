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
	NOPERMISSION("&cNo Permission"),
	/** TODO*/
	NOUUID("&7Not a correct UUID or Player is not online."),
	/** No such file exists (file as VALUE)*/
	NOFILE(String.format("&7Could not find file '%s'.", VALUE)),
	/** Not a correct Number(shows wrong input as VALUE)*/
	NONUMBER(String.format("'%s' is not a valid number", VALUE)),
	/** No such player (shows input as PLAYER)*/
	NOPLAYER(String.format("&7No such player '%s' could be found", PLAYER)),
	/** No database connection*/
	NOCONNECTION("&7Error: SQL-Connection"),
	/** No entries in the database*/
	NOLIST("No entries in the database"),

	WRONG_ARGUMENT("&7Wrong Argument. Please Check your Command."),

	/** Time format !WITHOUT COLORS!*/
	FORMAT_TIME			("dd.MM.yyyy, HH:mm"),
	//TODO
	ONLY_SQL("&7This Command works only with SQL."),
	
	REWARD_DAILY("&7Du hast dich bereits den &e-day-&7. Tag in Folge eingeloggt."),
	REWARD_BIRTHDAY("&e" + PLAYER + " &7hat heute seinen Server-Geburtstag."),
	REWARD_DAYS_CONSECUTIVE("&e" + PLAYER + " &7ist bereits &e" + VALUE + " &7Tage in Folge auf dem Server."),
	REWARD_DAYS_TOTAL("&e" + PLAYER + " &7ist bereits insgesamt &e" + VALUE + " &7Tage auf dem Server."),
	
	DailyInfo("&b/daily  -  &7zeigt alle gültigen Befehle"),
	DailyInfoTop("&b/daily top [day, totaldays] [value] -  &7zeigt top Liste"),
	DailyInfoSet("&b/daily set [player, uuid] [day, totaldays] [value]  -  &7Setzt dem Spieler day/totaldays value"),
	DailyInfoAdd("&b/daily add [player, uuid] [day, totaldays] [value]  -  &7Addiert dem Spieler day/totaldays value"),
	DailyInfoInfo("&b/daily info  -  &7zeigt Plugin version"),
	DailyInfoReload("&b/daily reload  -  &7lädt config neu"),
	DailyInfoPlayer("&b/daily player [player, uuid]  -  &7zeigt die Daten des Spielers"),
	DailyInfoConfig("&b/daily config [ma/system/reward/oldfiles/delay] [true/false/in case of delay number]  -  &7ändert die config"),
	
	DailyAdd("&7/daily add [player/uuid] [day, totaldays] [value]"),
	DailyAddData("&aBei dem Spieler mit der UUID: &e-player- &awurde &e-data- &aum &e-value- &aerh?ht."),

	DailySet("&7/daily set [player/uuid] [day, totaldays] [value]"),
	DailySetData("&aBei dem Spieler mit der UUID: &e-player- &awurde &e-data- &aauf &e-value- &agesetzt."),

	DailyReload("&7DailyJoin reloaded."),

	TopPlayerDay("&a----<[&6Top Day&a]>----"),
	TopPlayerTotalDays("&a----<[&6Top Totaldays&a]>----"),
	TopPlayerList("&e-player-: &7-days-"),

	PluginHeader("&a----<[&6DailyJoin&a]>----"),
	PluginFooter("&a---------------------"),
	PluginVersion("&eVersion: &7-version-"),
	PluginMonatsAnzeige("&eMonatsAnzeige: &7-status-"),
	PluginSystem("&eSystem: &7-status-"),
	PluginRewardReconnection("&eReward on Reconnection: &7-status-"),
	PluginDelay("&eRewardDelay: &7-delay-"),
	PluginTopPlayer("&eTopPlayer: &7-amount-"),

	PlayerHeader("&a----<[&6-player-&a]>----"),
	PlayerFooter("&a--------------------"),
	PlayerFirstJoin("&eFirstJoin: &7Day Month Year"),
	PlayerLastJoin("&eLastJoin: &7Day Month Year"),
	PlayerDay("&eDay: &7-day-"),
	PlayerTotalDays("&eTotalDays: &7-day-");
	
	private String message;
	
	Lang(String message) {
		this.message = message;
	}
	
	public String get() {
		return message;
	}
	
	public String colored() {
		return ChatColor.translateAlternateColorCodes('&', message);
	}
	
	/**
	 * Builds a TextComponent with text and colors
	 * @param args - Text
	 * @return TextComponent
	 */
	public static TextComponent buildTC(String args) {
		return buildTC(args, null, null, null);
	}
	
	/**
	 * Builds a TextComponent with colored text, and extras
	 * @param args - text
	 * @param cmd - onClick command
	 * @param hover - onHover text
	 * @param cmd_suggestion - onClick suggestion
	 * @return TextComponent
	 */
	public static TextComponent buildTC(String args, String cmd, String hover, String cmd_suggestion) {
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

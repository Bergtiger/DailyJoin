package de.bergtiger.dailyjoin.lang;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.hover.content.Text;

public enum Lang implements Constants {

	NoPermission("&cNo Permission"),
	NoNumber("No such number (" + VALUE + ")"),
	NoPlayer("&7No such player could be found"),
	WrongArgument("&7Wrong Argument. Please Check your Command."),
	
	RewardDaily("&7Du hast dich bereits den &e-day-&7. Tag in Folge eingeloggt."),
	RewardBirthday("&e" + PLAYER + " &7hat heute seinen Server-Geburtstag."),
	RewardSpezialDay("&e" + PLAYER + " &7ist bereits &e" + VALUE + " &7Tage in Folge auf dem Server."),
	RewardSpezialTotalDays("&e" + PLAYER + " &7ist bereits insgesamt &e" + VALUE + " &7Tage auf dem Server."),
	
	DailyInfo("&b/daily  -  &7zeigt alle gültigen Befehle"),
	DailyInfoTop("&b/daily top [day, totaldays] [value] -  &7zeigt top Liste"),
	DailyInfoSet("&b/daily set [player, uuid] [day, totaldays] [value]  -  &7Setzt dem Spieler day/totaldays value"),
	DailyInfoAdd("&b/daily add [player, uuid] [day, totaldays] [value]  -  &7Addiert dem Spieler day/totaldays value"),
	DailyInfoInfo("&b/daily info  -  &7zeigt Plugin version"),
	DailyInfoReload("&b/daily reload  -  &7lädt config neu"),
	DailyInfoPlayer("&b/daily player [player, uuid]  -  &7zeigt die Daten des Spielers"),
	DailyInfoConfig("&b/daily config [ma/system/reward/oldfiles/delay] [true/false/in case of delay number]  -  &7ändert die config"),
	
	DailyAdd("&7/daily add [player/uuid] [day, totaldays] [value]");
	
	private String message;
	
	private Lang(String message) {
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
	 * @param tc - TextComponent witch will be modified
	 * @param cmd - onClick command
	 * @param hover - onHover text
	 * @param cmd_suggestion
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
	 * @return
	 */
	private static BaseComponent rgbColor(BaseComponent bc, ChatColor color) {
		if(bc instanceof TextComponent) {
			if(color != null)
				((TextComponent)bc).setColor(color);
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

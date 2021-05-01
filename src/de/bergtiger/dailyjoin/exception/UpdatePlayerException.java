package de.bergtiger.dailyjoin.exception;

import de.bergtiger.dailyjoin.bdo.DailyPlayer;

public class UpdatePlayerException extends Exception {

	/**
	 * default serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	private final boolean sql;
	private final DailyPlayer dp;

	public UpdatePlayerException(boolean sql, DailyPlayer dp) {
		super("updatePlayerException: type: " + (sql ? "sql" : "file") + ", player: " + dp.getName());
		this.sql = sql;
		this.dp = dp;
	}

	public boolean isSQL() {
		return sql;
	}

	public boolean isFile() {
		return !sql;
	}

	public DailyPlayer getDp() {
		return dp;
	}
}

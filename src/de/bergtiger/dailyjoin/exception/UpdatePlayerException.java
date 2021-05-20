package de.bergtiger.dailyjoin.exception;

import de.bergtiger.dailyjoin.bdo.DailyPlayer;

public class UpdatePlayerException extends Exception {

	/**
	 * default serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/** data format sql or file*/
	private final boolean sql;
	/** daily player that could not be updated*/
	private final DailyPlayer dp;

	public UpdatePlayerException(boolean sql, DailyPlayer dp) {
		super("updatePlayerException: type: " + (sql ? "sql" : "file") + ", player: " + dp.getName());
		this.sql = sql;
		this.dp = dp;
	}

	/**
	 * check if sql was enabled
	 * @return true when sql was enabled
	 */
	public boolean isSQL() {
		return sql;
	}

	/**
	 * check if file was enabled
	 * @return true when file was enabled
	 */
	public boolean isFile() {
		return !sql;
	}

	/**
	 * get player that could not be updated
	 * @return {@link DailyPlayer}
	 */
	public DailyPlayer getDp() {
		return dp;
	}
}

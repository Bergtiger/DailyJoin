package de.bergtiger.dailyjoin.exception;

import de.bergtiger.dailyjoin.bdo.DailyPlayer;

public class UpdatePlayerException extends Exception {

	/**
	 * default serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	public UpdatePlayerException(DailyPlayer player) {
		super("UpdatePlayerException: " + player != null ? player.toString() : null);
	}
}

package de.bergtiger.dailyjoin.exception;

import java.io.Serial;

/**
 * Exception if sql connection is currently not available.
 */
public class NoSQLConnectionException extends Exception {

	/**
	 * default serialVersionUID
	 */
	@Serial
	private static final long serialVersionUID = 1L;

	public NoSQLConnectionException() {
		super("No SQL Connection");
	}
}

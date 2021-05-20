package de.bergtiger.dailyjoin.exception;

/**
 * Exception if sql connection is currently not available.
 */
public class NoSQLConnectionException extends Exception {

	/**
	 * default serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	public NoSQLConnectionException() {
		super("No SQL Connection");
	}
}

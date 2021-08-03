package de.bergtiger.dailyjoin.dao;

import de.bergtiger.dailyjoin.exception.NoSQLConnectionException;
import de.bergtiger.dailyjoin.utils.TigerLogger;

import java.util.logging.Level;

public interface TableDAO {

	String
			PLAYERS = "players",
			DAILY_JOIN_TABLE = "dailyjoin",
			/** player identification*/
			PID = "pid",
			/** player value name*/
			NAME = "name",
			/** player value uuid*/
			UUID = "uuid",
			/** player value days total*/
			DAYS_TOTAL = "days_total",
			/** player value days consecutive*/
			DAYS_CONSECUTIVE = "days_consecutive",
			/** old player value days total*/
			DAYS_OLD_TOTAL = "totaldays",
			/** old player value days consecutive*/
			DAYS_OLD_CONSECUTIVE = "day",
			/** player value first join date*/
			LASTJOIN = "lastjoin",
			/** player value last join date*/
			FIRSTJOIN = "firstjoin";

	default void createTables() {
		try {
			// create players
			createPlayers();
			// create daily data
			createDaily();
		} catch (NoSQLConnectionException e) {
			TigerLogger.log(Level.SEVERE, "createTables: no sql connection", e);
			TigerConnection.noConnection();
		}
	}

	void createPlayers() throws NoSQLConnectionException;

	void createDaily() throws NoSQLConnectionException;
}

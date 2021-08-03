package de.bergtiger.dailyjoin.dao.impl.sql;

import de.bergtiger.dailyjoin.dao.TableDAO;
import de.bergtiger.dailyjoin.dao.TigerConnection;
import de.bergtiger.dailyjoin.exception.NoSQLConnectionException;
import de.bergtiger.dailyjoin.utils.TigerLogger;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;

public class TableDAOImpl implements TableDAO {

	@Override
	public void createPlayers() throws NoSQLConnectionException {
		if(TigerConnection.hasConnection()) {
			PreparedStatement st = null;
			try {
				st = TigerConnection.conn().prepareStatement(String.format(
						"CREATE TABLE IF NOT EXISTS %s (" +
						"%s INT UNSIGNED AUTO_INCREMENT PRIMARY KEY, " +
						"%s VARCHAR(16) NOT NULL UNIQUE, " +
						"%s CHAR(36) NOT NULL UNIQUE, " +
						"%s TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
						"%s TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP)",
						PLAYERS,
						PID,
						NAME,
						UUID,
						LASTJOIN,
						FIRSTJOIN));
				st.executeUpdate();
			} catch (SQLException e) {
				TigerLogger.log(Level.SEVERE, "createDailyJoinTable: could not create Table.", e);
			} finally {
				TigerConnection.closeResources(null, st);
			}
		} else {
			throw new NoSQLConnectionException();
		}
	}

	@Override
	public void createDaily() throws NoSQLConnectionException {
		if (TigerConnection.hasConnection()) {
			PreparedStatement st = null;
			try {
				st = TigerConnection.conn().prepareStatement(String.format(
						"CREATE TABLE IF NOT EXISTS %s (" +
						"%s INT UNSIGNED NOT NULL UNIQUE, " +
						"%s INT UNSIGNED NOT NULL DEFAULT 1, " +
						"%s INT UNSIGNED NOT NULL DEFAULT 1," +
						"FOREIGN KEY (%s) REFERENCES %s(%s))",
						DAILY_JOIN_TABLE,
						PID,
						DAYS_TOTAL,
						DAYS_CONSECUTIVE,
						PID, PLAYERS, PID));
				st.executeUpdate();
			} catch (SQLException e) {
				TigerLogger.log(Level.SEVERE, "createDaily: could not create table.", e);
			} finally {
				TigerConnection.closeResources(null, st);
			}
		} else {
			throw new NoSQLConnectionException();
		}
	}
}

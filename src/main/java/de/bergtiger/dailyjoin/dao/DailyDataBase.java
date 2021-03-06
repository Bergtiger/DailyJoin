package de.bergtiger.dailyjoin.dao;

import de.bergtiger.dailyjoin.exception.NoSQLConnectionException;
import de.bergtiger.dailyjoin.utils.TigerLogger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class DailyDataBase {

	/**dailyjoin table name*/
	public static final String
			DAILY_JOIN_TABLE = "dailyjoin",
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

	private DailyDataBase() {
		checkTable();
	}

	public static void createTable() {
		new DailyDataBase();
	}

	/**
	 * create and update DailyJoinTable.
	 */
	private void checkTable() {
		try {
			// create DailyJoinTable
			createDailyJoinTable();
			// if existing Table
			// TODO unnecessary ?DailyJoinTable always exists here?
			if(hasDailyJoinTable()) {
				// check for old Columns
				List<String> columns = getColumns();
				if(columns != null && !columns.isEmpty()) {
					// rename DAYS_OLD_TOTAL
					if(columns.contains(DAYS_OLD_TOTAL))
						updateOldColumnName(DAYS_OLD_TOTAL, DAYS_TOTAL);
					// rename DAYS_OLD_CONSECUTIVE
					if(columns.contains(DAYS_OLD_CONSECUTIVE))
						updateOldColumnName(DAYS_OLD_CONSECUTIVE, DAYS_CONSECUTIVE);
				}
			}
		} catch (NoSQLConnectionException e) {
			TigerConnection.noConnection();
		}
	}

	/**
	 * creates DailyJoinTable if not already existing.
	 * @throws NoSQLConnectionException could not connect with database.
	 */
	private void createDailyJoinTable() throws NoSQLConnectionException {
		if(TigerConnection.hasConnection()) {
			PreparedStatement st = null;
			try {
				st = TigerConnection.conn().prepareStatement(String.format(
						"CREATE TABLE IF NOT EXISTS %s (" +
								"%s VARCHAR(16) NOT NULL UNIQUE, " +
								"%s VARCHAR(63) PRIMARY KEY, " +
								"%s INT NOT NULL DEFAULT 1, " +
								"%s INT NOT NULL DEFAULT 1, " +
								"%s TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
								"%s TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP)",
						DAILY_JOIN_TABLE,
						NAME,
						UUID,
						DAYS_TOTAL,
						DAYS_CONSECUTIVE,
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

	/**
	 * get all columns from DailyJoinTable.
	 * @return list of columns.
	 * @throws NoSQLConnectionException could not connect with database.
	 */
	private List<String> getColumns() throws NoSQLConnectionException {
		if (TigerConnection.hasConnection()) {
			ResultSet rs = null;
			try {
				rs = TigerConnection.conn().getMetaData().getColumns(null, null, DAILY_JOIN_TABLE, null);
				List<String> columns = new ArrayList<>();
				while (rs.next()) {
					columns.add(rs.getString("COLUMN_NAME"));
				}
				return columns;
			} catch (SQLException e) {
				TigerLogger.log(Level.SEVERE, "hasOldColumns: could not load ColumnMeta", e);
			} finally {
				TigerConnection.closeResources(rs, null);
			}
		} else {
			throw new NoSQLConnectionException();
		}
		return null;
	}

	/**
	 * check if DailyJoinTable exists in Database.
	 * @return true if table exists.
	 * @throws NoSQLConnectionException could not connect with database.
	 */
	private boolean hasDailyJoinTable() throws NoSQLConnectionException {
		if(TigerConnection.hasConnection()) {
			String[] tabletypes = {"TABLE"};
			ResultSet rs = null;
			try {
				rs = TigerConnection.conn().getMetaData().getTables(null,null,null, tabletypes);
				while(rs.next()) {
					if(rs.getString("TABLE_NAME").equalsIgnoreCase(DAILY_JOIN_TABLE))
						return true;
				}
			} catch (SQLException e) {
				TigerLogger.log(Level.SEVERE, "hasDailyJoinTable: could not load TableMeta", e);
			} finally {
				TigerConnection.closeResources(rs, null);
			}
		} else {
			throw new NoSQLConnectionException();
		}
		return false;
	}

	/**
	 * rename column in DailyJoinTable.
	 * @param oldColumn old column name
	 * @param newColumn new column name
	 * @throws NoSQLConnectionException could not connect with database.
	 */
	private void updateOldColumnName(String oldColumn, String newColumn) throws NoSQLConnectionException {
		if(TigerConnection.hasConnection()) {
			PreparedStatement st = null;
			try {
				st = TigerConnection.conn().prepareStatement(String.format(
						"ALTER TABLE %s CHANGE %s %s INT NOT NULL DEFAULT 1",
						DAILY_JOIN_TABLE,
						oldColumn,
						newColumn));
				st.executeUpdate();
				TigerLogger.log(Level.INFO, String.format("updateOldColumnName: changed %s to %s", oldColumn, newColumn));
			} catch (SQLException e) {
				TigerLogger.log(Level.SEVERE, "updateOldColumnName: could not alter Table", e);
			} finally {
				TigerConnection.closeResources(null, st);
			}
		} else {
			throw  new NoSQLConnectionException();
		}
	}
}

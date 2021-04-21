package de.bergtiger.dailyjoin.dao.impl.sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

import de.bergtiger.dailyjoin.dailyjoin;
import de.bergtiger.dailyjoin.bdo.DailyPlayer;
import de.bergtiger.dailyjoin.dao.TigerConnection;
import de.bergtiger.dailyjoin.dao.playerDAO;
import de.bergtiger.dailyjoin.exception.NoSQLConnectionException;
import de.bergtiger.dailyjoin.exception.UpdatePlayerException;
import static de.bergtiger.dailyjoin.dao.DailyDataBase.*;

public class playerDAOImplSQL implements playerDAO {

	/**
	 * insert Player in Database.
	 * if Player exists update.
	 * @param p Player to update or insert.
	 * @return generated key
	 * @throws NoSQLConnectionException could not connect with database.
	 * @throws UpdatePlayerException could not execute update.
	 */
	@Override
	public Integer updatePlayer(DailyPlayer p) throws NoSQLConnectionException, UpdatePlayerException {
		if(p != null) {
			if(TigerConnection.hasConnection()) {
				ResultSet rs = null;
				PreparedStatement st = null;
				try {
					st = TigerConnection.conn().prepareStatement(String.format("INSERT INTO %s (%s, %s, %s, %s, %s, %s) VALUES (?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE "
							+ "%s = VALUES(%s), "
							+ "%s = VALUES(%s), "
							+ "%s = VALUES(%s), "
							+ "%s = VALUES(%s)",
							DAILY_JOIN_TABLE,
							UUID, NAME, DAYS_TOTAL, DAYS_CONSECUTIVE, FIRSTJOIN, LASTJOIN,
							NAME, NAME,
							DAYS_TOTAL, DAYS_TOTAL,
							DAYS_CONSECUTIVE, DAYS_CONSECUTIVE,
							LASTJOIN, LASTJOIN), Statement.RETURN_GENERATED_KEYS);
					st.setString(1, p.getUuid());
					st.setString(2, p.getName());
					st.setInt(3, p.getDaysTotal());
					st.setInt(4, p.getDaysConsecutive());
					st.setTimestamp(5, p.getFirstjoin());
					st.setTimestamp(6, p.getLastjoin());
					st.executeUpdate();
					rs = st.getGeneratedKeys();
					if(rs.next()) {
						return rs.getInt(1);
					}
				} catch (SQLException e) {
					dailyjoin.getDailyLogger().log(Level.SEVERE, "updatePlayer: " + p, e);
					throw new UpdatePlayerException(p);
				} finally {
					TigerConnection.closeRessources(rs, st);
				}
			} else {
				throw new NoSQLConnectionException();
			}
		}
		return null;
	}

	/**
	 * get Player
	 * @param uuid Player uuid or name.
	 * @return player statistic.
	 * @throws NoSQLConnectionException could not connect with database.
	 */
	@Override
	public DailyPlayer getPlayer(String uuid) throws NoSQLConnectionException {
		if (TigerConnection.hasConnection()) {
			ResultSet rs = null;
			PreparedStatement st = null;
			try {
				if(uuid.length() > 16)
					st = TigerConnection.conn().prepareStatement("SELECT * FROM dailyjoin WHERE uuid LIKE ?", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
				else
					st = TigerConnection.conn().prepareStatement("SELECT * FROM dailyjoin WHERE name LIKE ?", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
				// set uuid
				st.setString(1, uuid);
				// get Query
				rs = st.executeQuery();
				// only one player allowed
				if(rs.next()) {
					DailyPlayer p = new DailyPlayer();
					p.setName(rs.getString(NAME));
					p.setUuid(rs.getString(UUID));
					p.setDaysTotal(rs.getInt(DAYS_TOTAL));
					p.setDaysConsecutive(rs.getInt(DAYS_CONSECUTIVE));
					p.setFirstjoin(rs.getTimestamp(FIRSTJOIN));
					p.setLastjoin(rs.getTimestamp(LASTJOIN));
				}
			} catch (SQLException e) {
				dailyjoin.getDailyLogger().log(Level.SEVERE, "getPlayer: " + uuid, e);
			} finally {
				TigerConnection.closeRessources(rs, st);
			}
		} else {
			throw new NoSQLConnectionException();
		}
		return null;
	}
}

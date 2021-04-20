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

public class playerDAOImplSQL implements playerDAO {

	/**
	 * update Player in Database
	 * @param p
	 * @return
	 * @throws NoSQLConnectionException 
	 * @throws UpdatePlayerException 
	 */
	@Override
	public Integer updatePlayer(DailyPlayer p) throws NoSQLConnectionException, UpdatePlayerException {
		if(p != null) {
			if(TigerConnection.hasConnection()) {
				ResultSet rs = null;
				PreparedStatement st = null;
				try {
					st = TigerConnection.conn().prepareStatement("INSERT INTO dailyjoin (uuid, name, totaldays, day, firstjoin, lastjoin) VALUES (?,?,?,?,?,?) ON DUPLICATE KEY UPDATE "
							+ "name = VALUES(name), "
							+ "day = VALUES(day), "
							+ "totaldays = VALUES(totaldays), "
							+ "lastjoin = VALUES(lastjoin)", Statement.RETURN_GENERATED_KEYS);
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
	 * @param uuid
	 * @return
	 * @throws NoSQLConnectionException 
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
					p.setName(rs.getString("name"));
					p.setUuid(rs.getString("uuid"));
					p.setDaysTotal(rs.getInt("totaldays"));
					p.setDaysConsecutive(rs.getInt("day"));
					p.setFirstjoin(rs.getTimestamp("firstjoin"));
					p.setLastjoin(rs.getTimestamp("lastjoin"));
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

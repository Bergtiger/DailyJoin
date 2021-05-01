package de.bergtiger.dailyjoin.dao.impl;

import de.bergtiger.dailyjoin.bdo.DailyPlayer;
import de.bergtiger.dailyjoin.bdo.TigerList;
import de.bergtiger.dailyjoin.dao.PlayerDAO;
import de.bergtiger.dailyjoin.dao.impl.file.PlayerDAOImplFile;
import de.bergtiger.dailyjoin.dao.impl.sql.PlayerDAOImplSQL;
import de.bergtiger.dailyjoin.exception.NoSQLConnectionException;
import de.bergtiger.dailyjoin.exception.UpdatePlayerException;
import de.bergtiger.dailyjoin.utils.config.DailyConfig;

import java.util.List;

public class PlayerDAOimpl implements PlayerDAO {

	private boolean sql;
	private final PlayerDAOImplSQL daoSQL = new PlayerDAOImplSQL();
	private final PlayerDAOImplFile daoFile = new PlayerDAOImplFile();

	private static PlayerDAOimpl instance;

	public static PlayerDAOimpl inst() {
		if (instance == null)
			instance = new PlayerDAOimpl();
		return instance;
	}

	private PlayerDAOimpl() {
		setSQL();
	}

	private void setSQL() {
		if (DailyConfig.inst().hasValue(DailyConfig.DATA_FORMAT_SQL))
			sql = DailyConfig.inst().getBoolean(DailyConfig.DATA_FORMAT_SQL);
		else
			sql = false;
	}

	public void reload() {
		setSQL();
	}

	@Override
	public Integer updatePlayer(DailyPlayer p) throws NoSQLConnectionException, UpdatePlayerException {
		return updatePlayer(p, sql);
	}

	/**
	 * updatePlayer with explicit choosing between data storage.
	 * @param p DailyPlayer to be stored
	 * @param useSQL choose if sql should be used
	 * @return
	 * @throws NoSQLConnectionException
	 * @throws UpdatePlayerException
	 */
	public Integer updatePlayer(DailyPlayer p, boolean useSQL) throws NoSQLConnectionException, UpdatePlayerException {
		return useSQL ? daoSQL.updatePlayer(p) : daoFile.updatePlayer(p);
	}
	
	@Override
	public DailyPlayer getPlayer(String uuid) throws NoSQLConnectionException {
		return getPlayer(uuid, sql);
	}

	/**
	 * getPlayer with explicit choosing between data storage.
	 * @param uuid
	 * @param useSQL choose if sql should be used
	 * @return
	 * @throws NoSQLConnectionException
	 */
	public DailyPlayer getPlayer(String uuid, boolean useSQL) throws NoSQLConnectionException {
		return useSQL ? daoSQL.getPlayer(uuid) : daoFile.getPlayer(uuid);
	}

	@Override
	public TigerList<DailyPlayer> getOrderedPlayers(String column, String richtung) throws NoSQLConnectionException {
		return sql ? daoSQL.getOrderedPlayers(column, richtung) : daoFile.getOrderedPlayers(column, richtung);
	}

	@Override
	public List<String> getNames(String args) throws NoSQLConnectionException {
		return sql ? daoSQL.getNames(args) : daoFile.getNames(args);
	}

	@Override
	public String getUUid(String name) throws NoSQLConnectionException {
		return sql ? daoSQL.getUUid(name) : daoFile.getUUid(name);
	}
	
	/**
	 * get all players from file.
	 * @return
	 */
	public List<DailyPlayer> getPlayers() {
		return daoFile.getPlayers();
	}
	
	/**
	 * save all players to file.
	 * @param players
	 */
	public void updatePlayers(List<DailyPlayer> players) {
		daoFile.updatePlayers(players);
	}
}

package de.bergtiger.dailyjoin.dao.impl;

import de.bergtiger.dailyjoin.bdo.DailyPlayer;
import de.bergtiger.dailyjoin.bdo.TigerList;
import de.bergtiger.dailyjoin.dao.PlayerDAO;
import de.bergtiger.dailyjoin.dao.impl.file.PlayerDAOImplFile;
import de.bergtiger.dailyjoin.dao.impl.sql.PlayerDAOImplSQL;
import de.bergtiger.dailyjoin.exception.NoSQLConnectionException;
import de.bergtiger.dailyjoin.exception.UpdatePlayerException;
import de.bergtiger.dailyjoin.utils.config.DailyConfig;

import java.util.HashMap;
import java.util.List;

public class PlayerDAOImpl implements PlayerDAO {

	/**
	 * normal data mode
	 */
	private boolean sql;
	/** dao for sql*/
	private final PlayerDAOImplSQL daoSQL = new PlayerDAOImplSQL();
	/** dao for file*/
	private final PlayerDAOImplFile daoFile = new PlayerDAOImplFile();

	private static PlayerDAOImpl instance;

	public static PlayerDAOImpl inst() {
		if (instance == null)
			instance = new PlayerDAOImpl();
		return instance;
	}

	private PlayerDAOImpl() {
		setSQL();
	}

	/**
	 * set normal data mode
	 */
	private void setSQL() {
		if (DailyConfig.inst().hasValue(DailyConfig.DATA_FORMAT_SQL))
			sql = DailyConfig.inst().getBoolean(DailyConfig.DATA_FORMAT_SQL);
		else
			sql = false;
	}

	/**
	 * reload normal data mode
	 */
	public void reload() {
		setSQL();
	}

	@Override
	public Integer updatePlayer(DailyPlayer p) throws NoSQLConnectionException, UpdatePlayerException {
		return updatePlayer(p, sql);
	}

	/**
	 * update player with explicit choosing between data storage.
	 * @param p {@link DailyPlayer} to be stored
	 * @param useSQL true if sql should be used
	 * @return id from database
	 * @throws NoSQLConnectionException when no sql connection is available
	 * @throws UpdatePlayerException when player could not be updated
	 */
	public Integer updatePlayer(DailyPlayer p, boolean useSQL) throws NoSQLConnectionException, UpdatePlayerException {
		return useSQL ? daoSQL.updatePlayer(p) : daoFile.updatePlayer(p);
	}

	@Override
	public void updatePlayers(List<DailyPlayer> players) throws NoSQLConnectionException, UpdatePlayerException {
		updatePlayers(players, sql);
	}

	/**
	 * update players in database with explicit choosing between data storage.
	 * @param players list of {@link DailyPlayer} to save
	 * @param useSQL true if sql should be used
	 * @throws NoSQLConnectionException when no sql connection is available
	 * @throws UpdatePlayerException when player could not be updated
	 */
	public void updatePlayers(List<DailyPlayer> players, boolean useSQL) throws NoSQLConnectionException, UpdatePlayerException {
		if(useSQL)
			daoSQL.updatePlayers(players);
		else
			daoFile.updatePlayers(players);
	}

	@Override
	public DailyPlayer getPlayer(String uuid) throws NoSQLConnectionException {
		return getPlayer(uuid, sql);
	}

	/**
	 * get player with explicit choosing between data storage.
	 * @param uuid player identifier
	 * @param useSQL true if sql should be used
	 * @return {@link DailyPlayer}
	 * @throws NoSQLConnectionException when no sql connection is available
	 */
	public DailyPlayer getPlayer(String uuid, boolean useSQL) throws NoSQLConnectionException {
		return useSQL ? daoSQL.getPlayer(uuid) : daoFile.getPlayer(uuid);
	}

	@Override
	public List<DailyPlayer> getPlayers() throws NoSQLConnectionException {
		return getPlayers(sql);
	}

	/**
	 * get all players with explicit choosing between data storage.
	 * @param useSQL true if sql should be used
	 * @return list of {@link DailyPlayer}
	 * @throws NoSQLConnectionException when no sql connection is available
	 */
	public List<DailyPlayer> getPlayers(boolean useSQL) throws NoSQLConnectionException {
		return useSQL ? daoSQL.getPlayers() : daoFile.getPlayers();
	}

	@Override
	public List<DailyPlayer> getPlayers(String name) throws NoSQLConnectionException {
		return getPlayers(name, sql);
	}

	/**
	 * get all players whose name starts with args and explicit choosing between data storage.
	 * @param args string to look after
	 * @param useSQL true if sql should be used
	 * @return List of all players whose name starts with args
	 * @throws NoSQLConnectionException when no sql connection is available
	 */
	public List<DailyPlayer> getPlayers(String args, boolean useSQL) throws NoSQLConnectionException {
		return useSQL ? daoSQL.getPlayers(args) : daoFile.getPlayers(args);
	}
	
	@Override
	public HashMap<String, DailyPlayer> getPlayersAsMap() throws NoSQLConnectionException {
		return getPlayersAsMap(sql);
	}

	/**
	 * get all players as map with explicit choosing between data storage.
	 * @param useSQL true if sql should be used
	 * @return HashMap containing uuid as identifier and player as value
	 * @throws NoSQLConnectionException when no sql connection is available
	 */
	public HashMap<String, DailyPlayer> getPlayersAsMap(boolean useSQL) throws NoSQLConnectionException {
		return useSQL ? daoSQL.getPlayersAsMap() : daoFile.getPlayersAsMap();
	}

	@Override
	public TigerList<DailyPlayer> getOrderedPlayers(String column, String order) throws NoSQLConnectionException {
		return sql ? daoSQL.getOrderedPlayers(column, order) : daoFile.getOrderedPlayers(column, order);
	}

	@Override
	public List<String> getNames(String args) throws NoSQLConnectionException {
		return sql ? daoSQL.getNames(args) : daoFile.getNames(args);
	}

	@Override
	public String getUUid(String name) throws NoSQLConnectionException {
		return sql ? daoSQL.getUUid(name) : daoFile.getUUid(name);
	}
}

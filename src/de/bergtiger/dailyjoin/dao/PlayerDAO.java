package de.bergtiger.dailyjoin.dao;

import de.bergtiger.dailyjoin.bdo.DailyPlayer;
import de.bergtiger.dailyjoin.bdo.TigerList;
import de.bergtiger.dailyjoin.exception.NoSQLConnectionException;
import de.bergtiger.dailyjoin.exception.UpdatePlayerException;

import java.util.HashMap;
import java.util.List;

public interface PlayerDAO {

	/**
	 * update Player in Database
	 * @param p
	 * @return
	 * @throws NoSQLConnectionException 
	 * @throws UpdatePlayerException 
	 */
	public Integer updatePlayer(DailyPlayer p) throws NoSQLConnectionException, UpdatePlayerException;
	
	/**
	 * get Player
	 * @param uuid
	 * @return
	 * @throws NoSQLConnectionException 
	 */
	public DailyPlayer getPlayer(String uuid) throws NoSQLConnectionException;

	/**
	 * get all players.
	 * @return List of all players
	 * @throws NoSQLConnectionException
	 */
	public List<DailyPlayer> getPlayers() throws NoSQLConnectionException;

	/**
	 * get all players as map
	 * @return HashMap containing uuid as identifier and players
	 * @throws NoSQLConnectionException
	 */
	public HashMap<String, DailyPlayer> getPlayersAsMap() throws NoSQLConnectionException;

	/**
	 * get top Players
	 * @return TigerList of all players
	 * @throws NoSQLConnectionException
	 */
	public TigerList<DailyPlayer> getOrderedPlayers(String column, String richtung) throws NoSQLConnectionException;

	/**
	 * get all names from Players
	 * @return List containing all player names
	 * @throws NoSQLConnectionException
	 */
	public List<String> getNames(String args) throws NoSQLConnectionException;

	/**
	 * get players uuid from database
	 * @param name searched player
	 * @return uuid of searched name
	 * @throws NoSQLConnectionException
	 */
	public String getUUid(String name) throws NoSQLConnectionException;
}

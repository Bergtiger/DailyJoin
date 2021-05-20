package de.bergtiger.dailyjoin.dao;

import de.bergtiger.dailyjoin.bdo.DailyPlayer;
import de.bergtiger.dailyjoin.bdo.TigerList;
import de.bergtiger.dailyjoin.exception.NoSQLConnectionException;
import de.bergtiger.dailyjoin.exception.UpdatePlayerException;

import java.util.HashMap;
import java.util.List;

public interface PlayerDAO {

	/**
	 * update player in database.
	 * if Player does not exists insert.
	 * @param p {@link DailyPlayer} to update or insert
	 * @return id from database
	 * @throws NoSQLConnectionException only if sql data mode and no connection
	 * @throws UpdatePlayerException when player could not be updated
	 */
	Integer updatePlayer(DailyPlayer p) throws NoSQLConnectionException, UpdatePlayerException;

	/**
	 * update players in database.
	 * @param players list of {@link DailyPlayer} to save
	 * @throws NoSQLConnectionException only if sql data mode and no connection
	 * @throws UpdatePlayerException when player could not be updated
	 */
	void updatePlayers(List<DailyPlayer> players) throws NoSQLConnectionException, UpdatePlayerException;
	
	/**
	 * get player.
	 * @param uuid player identifier
	 * @return {@link DailyPlayer} with matching uuid or name
	 * @throws NoSQLConnectionException only if sql data mode and no connection
	 */
	DailyPlayer getPlayer(String uuid) throws NoSQLConnectionException;

	/**
	 * get all players.
	 * @return list of all {@link DailyPlayer}
	 * @throws NoSQLConnectionException only if sql data mode and no connection
	 */
	List<DailyPlayer> getPlayers() throws NoSQLConnectionException;

	/**
	 * get all players whose name starts with args.
	 * @param args string to look after
	 * @return list of all players whose name starts with args
	 * @throws NoSQLConnectionException only if sql data mode and no connection
	 */
	List<DailyPlayer> getPlayers(String args) throws NoSQLConnectionException;

	/**
	 * get all players as map.
	 * @return HashMap containing uuid as identifier and player as value
	 * @throws NoSQLConnectionException only if sql data mode and no connection
	 */
	HashMap<String, DailyPlayer> getPlayersAsMap() throws NoSQLConnectionException;

	/**
	 * get players ordered by column in given order.
	 * @param column column used to order elements
	 * @param order ascending or descending order
	 * @return TigerList of all players sorted by
	 * @throws NoSQLConnectionException only if sql data mode and no connection
	 */
	TigerList<DailyPlayer> getOrderedPlayers(String column, String order) throws NoSQLConnectionException;

	/**
	 * get all names from players.
	 * @param args player names have to start with args
	 * @return List containing all player names
	 * @throws NoSQLConnectionException only if sql data mode and no connection
	 */
	List<String> getNames(String args) throws NoSQLConnectionException;

	/**
	 * get players uuid from database.
	 * @param name of searched player
	 * @return uuid of searched players name, if name length greater than 16 returns input to save resources
	 * @throws NoSQLConnectionException only if sql data mode and no connection
	 */
	String getUUid(String name) throws NoSQLConnectionException;
}

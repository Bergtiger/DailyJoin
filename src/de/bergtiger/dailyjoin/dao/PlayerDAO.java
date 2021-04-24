package de.bergtiger.dailyjoin.dao;

import de.bergtiger.dailyjoin.bdo.DailyPlayer;
import de.bergtiger.dailyjoin.bdo.TigerList;
import de.bergtiger.dailyjoin.exception.NoSQLConnectionException;
import de.bergtiger.dailyjoin.exception.UpdatePlayerException;

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
	 * get top Players
	 * @return
	 * @throws NoSQLConnectionException
	 */
	public TigerList<DailyPlayer> getOrderedPlayers(String column, String richtung) throws NoSQLConnectionException;

	/**
	 * get players uuid from database
	 * @param name searched player
	 * @return uuid of searched name
	 * @throws NoSQLConnectionException
	 */
	public String getUUid(String name) throws NoSQLConnectionException;
}

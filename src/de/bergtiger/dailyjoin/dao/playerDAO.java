package de.bergtiger.dailyjoin.dao;

import de.bergtiger.dailyjoin.bdo.DailyPlayer;
import de.bergtiger.dailyjoin.exception.NoSQLConnectionException;
import de.bergtiger.dailyjoin.exception.UpdatePlayerException;

public interface playerDAO {

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
	
}

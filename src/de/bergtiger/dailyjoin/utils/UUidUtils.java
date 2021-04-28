package de.bergtiger.dailyjoin.utils;

import de.bergtiger.dailyjoin.dao.TigerConnection;
import de.bergtiger.dailyjoin.dao.impl.file.DailyFile;
import de.bergtiger.dailyjoin.exception.NoSQLConnectionException;
import de.bergtiger.dailyjoin.utils.config.DailyConfig;

import org.apache.commons.lang.NotImplementedException;

import java.util.HashMap;

public class UUidUtils {

	/**
	 * name, uuid
	 */
	private final HashMap<String, String> storedUUids = new HashMap<>();

	private static UUidUtils instance;

	public static UUidUtils inst() {
		if (instance == null)
			instance = new UUidUtils();
		return instance;
	}

	private UUidUtils() {
	}

	/**
	 * get a uuid from a name.
	 * 
	 * @param uuid name of searched player
	 * @return uuid or null
	 */
	public static String getUUid(String uuid) {
		if (uuid.length() <= 16) {
			// uuid in range of name
			return UUidUtils.inst().findUUidFromName(uuid);
		}
		return uuid;
	}

	private String findUUidFromName(String name) {
		// uuid was searched before
		if (storedUUids.containsKey(name))
			return storedUUids.get(name);
		String uuid;
		// tigerlist implemented
		// sql active
		if (DailyConfig.inst().getBoolean(DailyConfig.DATA_FORMAT_SQL))
			uuid = getUUidFromDatabase(name);
		else
			uuid = getUUidFromFile(name);
		if (uuid != null)
			return storedUUids.put(name, uuid);
		return null;
	}

	/**
	 * get a uuid from a name from database. if connection lost will try to get from
	 * file.
	 * 
	 * @param name name of searched player
	 * @return uuid if player exists in database
	 */
	private String getUUidFromDatabase(String name) {
		try {
			return TigerConnection.inst().getPlayerDAO().getUUid(name);
		} catch (NoSQLConnectionException e) {
			// sql not available
			return getUUidFromFile(name);
		}
	}

	/**
	 * get a uuid from a name from file.
	 * 
	 * @param name name of searched player
	 * @return uuid if player exists in file
	 */
	private String getUUidFromFile(String name) {
		return DailyFile.getUUid(name);
	}

	/**
	 * get a uuid from a name from tigerlist plugin.
	 * 
	 * @param name name of searched player
	 * @return uuid if player exists in tigerlist database
	 */
	private String getUUidFromTigerList(String name) {
		throw new NotImplementedException();
	}
}

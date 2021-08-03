package de.bergtiger.dailyjoin.utils;

import de.bergtiger.dailyjoin.DailyJoin;
import de.bergtiger.dailyjoin.dao.impl.PlayerDAOImpl;
import de.bergtiger.dailyjoin.dao.impl.file.PlayerDAOImplFile;
import de.bergtiger.dailyjoin.exception.NoSQLConnectionException;

import org.apache.commons.lang.NotImplementedException;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.logging.Level;

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

	/**
	 * get UUid from a player name.
	 * @param name of the searched player
	 * @return uuid
	 */
	private String findUUidFromName(String name) {
		// uuid was searched before
		if (storedUUids.containsKey(name))
			return storedUUids.get(name);
		String uuid = null;
		// tigerlist implemented
		try {
			if (Bukkit.getPluginManager().isPluginEnabled("TigerList"))
				uuid = getUUidFromTigerList(name);
		} catch (Exception e) {
			TigerLogger.log(Level.WARNING, String.format("findUUidFromName: TigerList (%s)", name) ,e);
		}
		try {
			uuid = PlayerDAOImpl.inst().getUUid(name);
		} catch (NoSQLConnectionException e) {
			// explicit File
			uuid = new PlayerDAOImplFile().getUUid(name);
		}
		if (uuid != null)
			storedUUids.put(name, uuid);
		return storedUUids.get(name);
	}

	/**
	 * get a uuid from a name from Tigerlist plugin.
	 * 
	 * @param name name of searched player
	 * @return uuid if player exists in Tigerlist database
	 */
	private String getUUidFromTigerList(String name) {
		throw new NotImplementedException();
	}
}

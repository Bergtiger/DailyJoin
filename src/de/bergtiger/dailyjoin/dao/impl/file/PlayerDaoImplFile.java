package de.bergtiger.dailyjoin.dao.impl.file;

import de.bergtiger.dailyjoin.bdo.DailyPlayer;
import de.bergtiger.dailyjoin.bdo.TigerList;
import de.bergtiger.dailyjoin.dao.PlayerDAO;
import de.bergtiger.dailyjoin.exception.NoSQLConnectionException;
import de.bergtiger.dailyjoin.exception.UpdatePlayerException;

public class PlayerDaoImplFile implements PlayerDAO {
    @Override
    public Integer updatePlayer(DailyPlayer p) throws NoSQLConnectionException, UpdatePlayerException {
        return null;
    }

    @Override
    public DailyPlayer getPlayer(String uuid) throws NoSQLConnectionException {
        return null;
    }

    @Override
    public TigerList<DailyPlayer> getOrderedPlayers(String column, String richtung) throws NoSQLConnectionException {
        return null;
    }

    @Override
    public String getUUid(String name) throws NoSQLConnectionException {
        return null;
    }
}

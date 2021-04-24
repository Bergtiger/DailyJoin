package de.bergtiger.dailyjoin.dao.impl;

import de.bergtiger.dailyjoin.bdo.DailyPlayer;
import de.bergtiger.dailyjoin.bdo.TigerList;
import de.bergtiger.dailyjoin.dao.PlayerDAO;
import de.bergtiger.dailyjoin.dao.impl.file.PlayerDaoImplFile;
import de.bergtiger.dailyjoin.dao.impl.sql.PlayerDAOImplSQL;
import de.bergtiger.dailyjoin.exception.NoSQLConnectionException;
import de.bergtiger.dailyjoin.exception.UpdatePlayerException;

public class playerDAOimpl implements PlayerDAO {

    private boolean sql;
    private final PlayerDAOImplSQL daoSQL = new PlayerDAOImplSQL();
    private final PlayerDaoImplFile daoFile = new PlayerDaoImplFile();

    @Override
    public Integer updatePlayer(DailyPlayer p) throws NoSQLConnectionException, UpdatePlayerException {
        return sql ? daoSQL.updatePlayer(p) : daoFile.updatePlayer(p);
    }

    @Override
    public DailyPlayer getPlayer(String uuid) throws NoSQLConnectionException {
        return sql ? daoSQL.getPlayer(uuid) : daoFile.getPlayer(uuid);
    }

    @Override
    public TigerList<DailyPlayer> getOrderedPlayers(String column, String richtung) throws NoSQLConnectionException {
        return sql ? daoSQL.getOrderedPlayers(column, richtung) : daoFile.getOrderedPlayers(column, richtung);
    }

    @Override
    public String getUUid(String name) throws NoSQLConnectionException {
        return sql ? daoSQL.getUUid(name) : daoFile.getUUid(name);
    }
}

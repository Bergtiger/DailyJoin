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
        if(instance == null)
            instance = new PlayerDAOimpl();
        return instance;
    }

    private PlayerDAOimpl() {
        setSQL();
    }

    private void setSQL() {
        if(DailyConfig.inst().hasValue(DailyConfig.DATA_FORMAT_SQL))
            sql = DailyConfig.inst().getBoolean(DailyConfig.DATA_FORMAT_SQL);
        sql = false;
    }

    public void reload() {
        setSQL();
    }

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
    public List<String> getNames(String args) throws NoSQLConnectionException {
        return sql ? daoSQL.getNames(args) : daoFile.getNames(args);
    }

    @Override
    public String getUUid(String name) throws NoSQLConnectionException {
        return sql ? daoSQL.getUUid(name) : daoFile.getUUid(name);
    }
}

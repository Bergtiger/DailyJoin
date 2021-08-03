package de.bergtiger.dailyjoin.exception;

import java.io.Serial;

public class LoadPlayerException extends Exception {

    /**
     * default serialVersionUID
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /** data format sql or file*/
    private final boolean sql;
    /** uuid that could not be loaded*/
    private final String uuid;

    public LoadPlayerException(boolean sql, String uuid) {
        super("LoadPlayerException: type: " + (sql ? "sql" : "file") + ", player: " + uuid);
        this.sql = sql;
        this.uuid = uuid;
    }

    /**
     * check if sql was enabled
     * @return true when sql was enabled
     */
    public boolean isSQL() {return sql;}

    /**
     * check if file was enabled
     * @return true when file was enabled
     */
    public boolean isFile() {return !sql;}

    /**
     * get uuid
     * @return uuid
     */
    public String getUuid() {return uuid;}
}

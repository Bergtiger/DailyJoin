package de.bergtiger.dailyjoin.exception;

public class LoadPlayerException extends Exception {

    /**
     * default serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    private final boolean sql;

    public LoadPlayerException(boolean sql, String uuid) {
        super("LoadPlayerException: type: " + (sql ? "sql" : "file") + ", player: " + uuid);
        this.sql = sql;
    }

    public boolean isSQL() {
        return sql;
    }

    public boolean isFile() {
        return !sql;
    }
}

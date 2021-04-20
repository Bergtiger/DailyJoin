package de.bergtiger.dailyjoin.dao;

public class DailyDataBase {
	public DailyDataBase(TigerConnection sql){
		sql.queryUpdate("CREATE TABLE IF NOT EXISTS dailyjoin (name VARCHAR(23), uuid VARCHAR(63) PRIMARY KEY, day INT, totaldays INT, lastjoin TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, firstjoin TIMESTAMP NOT NULL)");		
	}
}

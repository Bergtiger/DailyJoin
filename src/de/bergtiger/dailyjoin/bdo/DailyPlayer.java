package de.bergtiger.dailyjoin.bdo;

import java.sql.Timestamp;

public class DailyPlayer {

	private String name;
	private String uuid;
	private int day;
	private int totaldays;
	private Timestamp firstjoin;
	private Timestamp lastjoin;

	public DailyPlayer() {}
	
	public DailyPlayer(String name, String uuid, int day, int totaldays, Timestamp firstjoin, Timestamp lastjoin){
		this.name = name;
		this.uuid = uuid;
		this.day = day;
		this.totaldays = totaldays;
		this.firstjoin = firstjoin;
		this.lastjoin = lastjoin;
	}
	
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the uuid
	 */
	public String getUuid() {
		return uuid;
	}

	/**
	 * @param uuid the uuid to set
	 */
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	/**
	 * @return the day
	 */
	public int getDay() {
		return day;
	}

	/**
	 * @param day the day to set
	 */
	public void setDay(int day) {
		this.day = day;
	}

	/**
	 * @return the totaldays
	 */
	public int getTotaldays() {
		return totaldays;
	}

	/**
	 * @param totaldays the totaldays to set
	 */
	public void setTotaldays(int totaldays) {
		this.totaldays = totaldays;
	}

	/**
	 * @return the firstjoin
	 */
	public Timestamp getFirstjoin() {
		return firstjoin;
	}

	/**
	 * @param firstjoin the firstjoin to set
	 */
	public void setFirstjoin(Timestamp firstjoin) {
		this.firstjoin = firstjoin;
	}

	/**
	 * @return the lastjoin
	 */
	public Timestamp getLastjoin() {
		return lastjoin;
	}

	/**
	 * @param lastjoin the lastjoin to set
	 */
	public void setLastjoin(Timestamp lastjoin) {
		this.lastjoin = lastjoin;
	}

	@Deprecated
	public String name(){return this.name;}
	@Deprecated
	public String uuid(){return this.uuid;}
	@Deprecated
	public int day(){return this.day;}
	@Deprecated
	public int totaldays(){return this.totaldays;}
	@Deprecated
	public Timestamp firstjoin(){return this.firstjoin;}
	@Deprecated
	public Timestamp lastjoin(){return this.lastjoin;}
}

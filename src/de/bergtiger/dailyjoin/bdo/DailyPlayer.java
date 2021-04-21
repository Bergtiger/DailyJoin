package de.bergtiger.dailyjoin.bdo;

import java.sql.Time;
import java.sql.Timestamp;

public class DailyPlayer {

	private String name;
	private String uuid;
	private int daysConsecutive;
	private int daysTotal;
	private Timestamp firstjoin;
	private Timestamp lastjoin;

	public DailyPlayer() {}

	public DailyPlayer(String name, String uuid) {
		this.name = name;
		this.uuid = uuid;
		this.daysTotal = 1;
		this.daysConsecutive = 1;
		this.firstjoin = new Timestamp(System.currentTimeMillis());
		this.lastjoin = new Timestamp(System.currentTimeMillis());
	}

	public DailyPlayer(String name, String uuid, int daysConsecutive, int daysTotal, Timestamp firstjoin, Timestamp lastjoin){
		this.name = name;
		this.uuid = uuid;
		this.daysTotal = daysTotal;
		this.daysConsecutive = daysConsecutive;
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
	 * @return the daysConsecutive
	 */
	public int getDaysConsecutive() {
		return daysConsecutive;
	}

	/**
	 * @param daysConsecutive the daysConsecutive to set
	 */
	public void setDaysConsecutive(int daysConsecutive) {
		this.daysConsecutive = daysConsecutive;
	}

	/**
	 * @return the daysTotal
	 */
	public int getDaysTotal() {
		return daysTotal;
	}

	/**
	 * @param daysTotal the daysTotal to set
	 */
	public void setDaysTotal(int daysTotal) {
		this.daysTotal = daysTotal;
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
	public void setLastjoin(Timestamp lastjoin) { this.lastjoin = lastjoin; }
}

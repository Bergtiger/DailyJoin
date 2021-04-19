package de.bergtiger.dailyjoin;

import java.sql.Timestamp;

public class DailyPlayer {

	private String name;
	private String uuid;
	private int day;
	private int totaldays;
	private Timestamp firstjoin;
	private Timestamp lastjoin;

	public DailyPlayer(String name, String uuid, int day, int totaldays, Timestamp firstjoin, Timestamp lastjoin){
		this.name = name;
		this.uuid = uuid;
		this.day = day;
		this.totaldays = totaldays;
		this.firstjoin = firstjoin;
		this.lastjoin = lastjoin;
	}
	
	public String name(){return this.name;}
	public String uuid(){return this.uuid;}
	public int day(){return this.day;}
	public int totaldays(){return this.totaldays;}
	public Timestamp firstjoin(){return this.firstjoin;}
	public Timestamp lastjoin(){return this.lastjoin;}
}

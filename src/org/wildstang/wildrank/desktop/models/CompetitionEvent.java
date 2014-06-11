package org.wildstang.wildrank.desktop.models;

import java.util.Date;

public class CompetitionEvent {

	private String key;
	private String name;
	private String shortName;
	private String location;
	private Date startDate;
	private Date endDate;

	public CompetitionEvent(String key, String name, String shortName, String location, Date startDate, Date endDate) {
		this.key = key;
		this.name = name;
		this.shortName = shortName;
		this.location = location;
		this.startDate = startDate;
		this.endDate = endDate;
	}

	public String getKey() {
		return key;
	}

	public String getName() {
		return name;
	}

	public String getShortName() {
		return shortName;
	}

	public String getLocation() {
		return location;
	}

	public Date getStartDate() {
		return startDate;
	}

	public Date getEndDate() {
		return endDate;
	}
}

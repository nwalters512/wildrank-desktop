package org.wildstang.wildrank.desktop.models;

public class CompetitionTeam {

	private String name;
	private int number;

	public CompetitionTeam(String name, int number) {
		this.name = name;
		this.number = number;
	}

	public String getName() {
		return name;
	}

	public int getNumber() {
		return number;
	}

}

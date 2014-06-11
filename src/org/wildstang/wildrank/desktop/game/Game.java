package org.wildstang.wildrank.desktop.game;

import java.util.ArrayList;
import java.util.List;

public class Game {
	private String name;
	private String mainKey;
	private List<Section> sections = new ArrayList<Section>();

	public Game(String name, List<Section> sHeaders) {
		this.name = name;
		this.sections = sHeaders;
	}

	public Game(String name) {
		this.name = name;
	}

	public Game() {
		//Empty constructor
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMainKey() {
		return mainKey;
	}

	public void setMainKey(String mainKey) {
		this.mainKey = mainKey;
	}

	public List<Section> getSections() {
		return sections;
	}

	public Section getSectionByKey(String key) {
		for (Section section : sections) {
			if (section.getKey().equals(key)) {
				return section;
			}
		}
		Section section = new Section(key);
		addSection(section);
		return section;
	}

	public void addSection(Section section) {
		sections.add(section);
	}
}

package org.wildstang.wildrank.desktop.game;

import java.util.ArrayList;
import java.util.List;

public class Section {
	private String key;
	private List<Item> items = new ArrayList<Item>();

	public Section(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public List<Item> getItems() {
		return items;
	}

	public void addItem(Item item) {
		items.add(item);
	}
}

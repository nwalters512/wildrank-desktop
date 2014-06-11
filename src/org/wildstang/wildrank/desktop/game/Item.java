package org.wildstang.wildrank.desktop.game;

import org.wildstang.wildrank.desktop.game.GameReader.GameReaderException;

public class Item {
	private String name;
	private String key;
	private ItemType type;

	public enum ItemType {
		TYPE_NUM,
		TYPE_BOOLEAN,
		TYPE_STRING,
	}

	public Item(String name, String key, String type) throws GameReaderException {
		this.name = name;
		this.key = key;
		this.type = itemTypeFromString(type);
	}

	public Item(String name, String key, ItemType type) {
		this.name = name;
		this.key = key;
		this.type = type;
	}

	public ItemType itemTypeFromString(String type) throws GameReaderException {
		if (type.equals("num")) {
			return ItemType.TYPE_NUM;
		} else if (type.equals("bool")) {
			return ItemType.TYPE_BOOLEAN;
		} else if (type.equals("text")) {
			return ItemType.TYPE_STRING;
		} else {
			throw new GameReaderException("Invalid item type: " + type);
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public ItemType getType() {
		return type;
	}

	public String getTypeString() {
		switch (type) {
		case TYPE_NUM:
			return "num";
		case TYPE_BOOLEAN:
			return "bool";
		default:
		case TYPE_STRING:
			return "text";
		}
	}

	public void setType(ItemType type) {
		this.type = type;
	}
}

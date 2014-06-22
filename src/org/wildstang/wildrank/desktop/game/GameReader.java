package org.wildstang.wildrank.desktop.game;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class GameReader {

	public static Game readFile(File file) throws IOException, GameReaderException {
		Game game = new Game();
		if(!file.exists())
		{
			System.out.println("NO GAME FILE!!!!!!111one");
		}
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line = null;
		String currentSection = null;
		boolean done = false;
		while ((line = br.readLine()) != null && !done) {
			if (line.indexOf(": ") != -1) {
				String tag = line.substring(0, line.indexOf(": "));
				String data = line.substring(line.indexOf(": ") + 2);
				switch (tag) {
				case "game-name":
					game.setName(data);
					break;
				case "main-key":
					game.setMainKey(data);
					break;
				case "section-key":
					game.addSection(new Section(data));
					currentSection = data;
					break;
				case "item":
					if (currentSection == null) {
						throw new GameReaderException("Items must be preceded by a section-key!");
					} else {
						String key = data.substring(data.indexOf(",") + 2, data.indexOf(";"));
						String name = data.substring(0, data.indexOf(","));
						String type = data.substring(data.indexOf(";") + 2);
						game.getSectionByKey(currentSection).addItem(new Item(name, key, type));
						break;
					}
				}
			}
		}
		br.close();
		return game;
	}

	public static class GameReaderException extends Exception {
		public GameReaderException() {
			//Empty constructor
		}

		public GameReaderException(String message) {
			super(message);
		}

		public GameReaderException(Throwable cause) {
			super(cause);
		}

		public GameReaderException(String message, Throwable cause) {
			super(message, cause);
		}
	}
}

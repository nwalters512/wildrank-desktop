package org.wildstang.wildrank.desktop;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.json.JSONObject;
import org.wildstang.wildrank.desktop.game.Game;
import org.wildstang.wildrank.desktop.utils.JSONTools;
import org.wildstang.wildrank.desktop.utils.Logger;

public class AppData {
	private File flashDriveLocation;
	private File localLocation;
	private String eventKey;
	private Game game;

	//this holds the data that everything needs to access and saves and loads it
	public AppData(File flashDriveLocation, File localLocation, String eventKey) {
		this.flashDriveLocation = flashDriveLocation;
		this.localLocation = localLocation;
		this.eventKey = eventKey;
	}

	public File getFlashDriveLocation() {
		return flashDriveLocation;
	}

	public File getLocalLocation() {
		return localLocation;
	}

	public String getEventKey() {
		return eventKey;
	}

	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public void setFlashDriveLocation(File flashDriveLocation) {
		this.flashDriveLocation = flashDriveLocation;
	}

	public void setLocalLocation(File localLocation) {
		this.localLocation = localLocation;
	}

	public void setEventKey(String eventKey) {
		this.eventKey = eventKey;
	}

	public void save() {
		try {
			File file = new File("save.json");
			Logger.getInstance().log("Local path: " + file.getAbsolutePath());
			if (!file.exists()) {
				file.createNewFile();
			}
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			JSONObject rootObject = new JSONObject();
			if (flashDriveLocation != null) {
				rootObject.put("flash_drive_location", flashDriveLocation.toString());
			} else {
				rootObject.put("flash_drive_location", "");
			}
			if (localLocation != null) {
				rootObject.put("local_location", localLocation.toString());
			} else {
				rootObject.put("local_location", "");
			}
			if (eventKey != null) {
				rootObject.put("event_key", eventKey.toString());
			} else {
				rootObject.put("event_key", "");
			}
			bw.write(rootObject.toString());
			bw.flush();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void read() throws IOException {
		File file = new File("save.json");
		String fileString = JSONTools.getJsonFromFile(file);
		JSONObject json = new JSONObject(fileString);
		flashDriveLocation = new File(json.getString("flash_drive_location"));
		localLocation = new File(json.getString("local_location"));
		eventKey = json.getString("event_key");
	}
}
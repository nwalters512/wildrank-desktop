package org.wildstang.wildrank.desktop;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.json.JSONObject;
import org.wildstang.wildrank.desktop.utils.JSONTools;

public class PitReader {
	File dir;

	public PitReader(File txt) throws IOException {
		dir = txt;
	}

	public void readAerialAssist(File json) throws IOException {
		String fileString = JSONTools.getJsonFromFile(json);
		JSONObject jsonPit = new JSONObject(fileString);
		JSONObject scoring = jsonPit.getJSONObject("scoring");
		File txt = new File(dir, File.separator + Integer.toString(jsonPit.getInt("team_number")) + ".txt");
		if (!txt.exists()) {
			txt.createNewFile();
		}
		BufferedWriter br = new BufferedWriter(new FileWriter(txt));
		br.write("Scouted by: " + jsonPit.getString("scouter_id") + "\n");
		br.write("Weight: " + Integer.toString(scoring.getInt("robot_weight")) + "\n");
		br.write("(L,W,H): (" + Integer.toString(scoring.getInt("robot_length")) + "," + Integer.toString(scoring.getInt("robot_width")) + "," + Integer.toString(scoring.getInt("robot_height"))
				+ ")\n");
		br.write("Drivetrain: " + scoring.getString("drivetrain") + "\n");
		br.write("Shooter: " + scoring.getString("shooter") + "\n");
		br.write("Accumulator: " + convertBool(scoring.getBoolean("accumulator")) + "\n");
		br.write("Catcher: " + convertBool(scoring.getBoolean("catcher")) + "\n");
		br.write("Receives from HP: " + convertBool(scoring.getBoolean("receive_from_hp")));
		br.flush();
		br.close();
	}

	public String convertBool(boolean bool) {
		String output;
		if (bool) {
			output = "Yes";
		} else {
			output = "No";
		}
		return output;
	}
}

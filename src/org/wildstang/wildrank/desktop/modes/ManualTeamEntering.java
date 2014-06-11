package org.wildstang.wildrank.desktop.modes;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JTextField;

import org.json.JSONArray;
import org.json.JSONObject;
import org.wildstang.wildrank.desktop.TextFieldHintHandler;
import org.wildstang.wildrank.desktop.utils.FileUtilities;
import org.wildstang.wildrank.desktop.utils.Logger;

public class ManualTeamEntering extends Mode implements ActionListener {

	private class TeamInfo {
		public String name;
		public String number;

		public TeamInfo(String number, String name) {
			this.number = number;
			this.name = name;
		}
	}

	JTextField teamNumber;
	JTextField teamName;
	JButton addTeam;
	JButton createList;
	private ArrayList<TeamInfo> teamsList = new ArrayList<TeamInfo>();

	@Override
	protected void initializePanel() {
		teamNumber = new JTextField("Team number");
		teamNumber.addFocusListener(new TextFieldHintHandler(teamNumber, "Team number"));
		teamName = new JTextField("Team name");
		teamName.addFocusListener(new TextFieldHintHandler(teamName, "Team name"));
		addTeam = new JButton("Add team");
		addTeam.addActionListener(this);
		createList = new JButton("Create list");
		createList.addActionListener(this);

		c.gridx = 0;
		c.gridy = 0;
		panel.add(teamNumber, c);
		c.gridx = 1;
		panel.add(teamName, c);
		c.gridx = 2;
		panel.add(addTeam, c);
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 3;
		panel.add(createList, c);
		update.setMode("Team Adder");
	}

	private void generateList() {
		// Load eisting teams list
		JSONObject rootObject = new JSONObject();
		JSONArray teamsjson = new JSONArray();
		try {
			File existing = new File(FileUtilities.getSyncedDirectory() + File.separator + "event" + File.separator + "pit.json");
			if (existing.exists()) {
				BufferedReader br = new BufferedReader(new FileReader(existing));
				StringBuilder builder = new StringBuilder();
				String line = new String();
				while ((line = br.readLine()) != null) {
					builder.append(line);
				}
				br.close();
				rootObject = new JSONObject(builder.toString());
				teamsjson = rootObject.getJSONArray("team");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		List<String> tabletNames = new ArrayList<String>();
		tabletNames.add(0, "red_1");
		tabletNames.add(1, "red_2");
		tabletNames.add(2, "red_3");
		tabletNames.add(3, "blue_1");
		tabletNames.add(4, "blue_2");
		tabletNames.add(5, "blue_3");
		int posCounter = 0;
		for (int i = 0; i < teamsList.size(); i++) {
			JSONObject team = new JSONObject();
			team.put("number", ((TeamInfo) teamsList.get(i)).number);
			team.put("name", ((TeamInfo) teamsList.get(i)).name);
			team.put("pit_team_id", tabletNames.get(posCounter));
			posCounter++;
			if (posCounter == 6) {
				posCounter = 0;
			}
			teamsjson.put(team);
		}
		rootObject.put("team", teamsjson);
		File file = new File(FileUtilities.getSyncedDirectory() + File.separator + "event" + File.separator + "pit.json");
		File flash = new File(FileUtilities.getFlashDriveSyncedDirectory() + File.separator + "event" + File.separator + "pit.json");
		Logger.getInstance().log("Local path: " + file.getAbsolutePath());
		Logger.getInstance().log("Flash path: " + flash.getAbsolutePath());
		file.getParentFile().mkdirs();
		flash.getParentFile().mkdirs();
		try {
			if (!file.exists()) {
				file.createNewFile();
			}
			if (!flash.exists()) {
				flash.createNewFile();
			}
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			BufferedWriter bwt = new BufferedWriter(new FileWriter(flash));
			bw.write(rootObject.toString());
			bwt.write(rootObject.toString());
			bw.flush();
			bw.close();
			bwt.flush();
			bwt.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void actionPerformed(ActionEvent source) {
		if (source.getSource() == addTeam) {
			TeamInfo team = new TeamInfo(teamNumber.getText().trim(), teamName.getText().trim());
			teamsList.add(team);
			update.updateData("Team " + teamNumber.getText() + " Added", 0, 0);
			teamNumber.setText("Team number");
			teamName.setText("Team name");
			teamNumber.requestFocus();
		} else if (source.getSource() == createList) {
			generateList();
		}

	}

}

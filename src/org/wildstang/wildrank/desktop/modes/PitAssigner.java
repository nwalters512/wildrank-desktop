package org.wildstang.wildrank.desktop.modes;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JProgressBar;
import javax.swing.JTextField;

import org.json.JSONArray;
import org.json.JSONObject;
import org.wildstang.wildrank.desktop.GlobalAppHandler;
import org.wildstang.wildrank.desktop.TextFieldHintHandler;
import org.wildstang.wildrank.desktop.utils.FileUtilities;
import org.wildstang.wildrank.desktop.utils.JSONTools;

public class PitAssigner extends Mode implements ActionListener, Runnable {
	JButton go;
	JTextField numberOfTablets;
	static JProgressBar progress;
	Thread thread;

	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == go) {
			thread = new Thread(this);
			thread.start();
		}
	}

	@Override
	protected void initializePanel() {
		go = new JButton("Write pit configuration!");
		go.addActionListener(this);
		progress = new JProgressBar();
		numberOfTablets = new JTextField("Number of tablets (default is 3)");
		numberOfTablets.addFocusListener(new TextFieldHintHandler(numberOfTablets, "Number of tablets (default is 3)"));
		c.gridx = 0;
		c.gridy = 0;
		panel.add(numberOfTablets, c);
		c.gridx = 1;
		panel.add(go, c);
		c.gridy = 1;
		c.gridx = 0;
		c.gridwidth = 2;
		panel.add(progress, c);
		update.setMode("Pit Assigner");
	}

	@Override
	public void run() {
		GlobalAppHandler.getInstance().disableBackButton();
		progress.setIndeterminate(true);
		JSONArray eventTeamsListObject = new JSONArray(JSONTools.getJsonFromUrl("http://www.thebluealliance.com/api/v2/event/" + appData.getEventKey() + "/teams"));
		JSONArray teamsArray = new JSONArray();

		int posCounter = 0;
		int numTablets;
		try {
			numTablets = Integer.parseInt(numberOfTablets.getText().toString());
		} catch (NumberFormatException e) {
			numTablets = 3;
		}
		for (int i = 0; i < eventTeamsListObject.length(); i++) {
			JSONObject currentTeam = eventTeamsListObject.getJSONObject(i);
			JSONObject team = new JSONObject();
			team.put("number", currentTeam.get("team_number"));
			team.put("name", currentTeam.getString("nickname"));
			team.put("pit_group", posCounter);
			posCounter++;
			if (posCounter == numTablets) {
				posCounter = 0;
			}
			teamsArray.put(team);
		}
		JSONObject rootObject = new JSONObject();
		rootObject.put("team", teamsArray);
		File file = new File(FileUtilities.getSyncedDirectory() + File.separator + "event" + File.separator + "pit.json");
		file.getParentFile().mkdirs();
		try {
			if (!file.exists()) {
				file.createNewFile();
			}
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			bw.write(rootObject.toString());
			bw.flush();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		GlobalAppHandler.getInstance().enableBackButton();
		setMode(new MainMenu());
	}
}

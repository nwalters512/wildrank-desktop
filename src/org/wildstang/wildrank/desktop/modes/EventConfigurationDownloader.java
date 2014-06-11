package org.wildstang.wildrank.desktop.modes;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

import org.json.JSONArray;
import org.json.JSONObject;
import org.wildstang.wildrank.desktop.GlobalAppHandler;
import org.wildstang.wildrank.desktop.utils.FileUtilities;
import org.wildstang.wildrank.desktop.utils.JSONTools;
import org.wildstang.wildrank.desktop.utils.Logger;

public class EventConfigurationDownloader extends Mode implements ActionListener, Runnable {
	JButton download;
	JComboBox<String> spinner;
	static JLabel note;
	static JLabel downloaded;
	static JProgressBar progress;
	Thread thread;

	@Override
	public void initializePanel() {
		progress = new JProgressBar();
		download = new JButton("Download!");
		download.addActionListener(this);
		note = new JLabel("Not downloading");
		downloaded = new JLabel("0/0");
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 3;
		panel.add(download, c);
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 2;
		panel.add(note, c);
		c.gridx = 2;
		c.gridwidth = 1;
		panel.add(downloaded, c);
		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 3;
		panel.add(progress, c);
		update.setMode("Event Config Downloader");
	}

	@Override
	public void run() {
		GlobalAppHandler.getInstance().disableBackButton();
		note.setText("Loading event details");
		progress.setIndeterminate(true);
		String eventDetailsJson = JSONTools.getJsonFromUrl("http://www.thebluealliance.com/api/v2/event/" + appData.getEventKey());

		Logger.getInstance().log("EventDetails: " + eventDetailsJson.toString());

		JSONObject jsonEvent = new JSONObject(eventDetailsJson);

		String eventMatches = JSONTools.getJsonFromUrl("http://www.thebluealliance.com/api/v2/event/" + appData.getEventKey() + "/matches");
		JSONArray matchesJSONArray = new JSONArray(eventMatches);
		jsonEvent.put("matches", matchesJSONArray);

		File localFile = new File(FileUtilities.getSyncedDirectory() + File.separator + "event" + File.separator + "event.json");
		try {
			localFile.createNewFile();
			BufferedWriter bw = new BufferedWriter(new FileWriter(localFile));
			bw.write(jsonEvent.toString());
			bw.flush();
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		GlobalAppHandler.getInstance().enableBackButton();
		setMode(new PitAssigner());
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == download) {
			thread = new Thread(this);
			try {
				thread.start();
			} catch (Exception e) {
				e.printStackTrace();
			}
			download.setEnabled(false);
		}
	}
}

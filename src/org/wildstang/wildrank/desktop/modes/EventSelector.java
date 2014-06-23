package org.wildstang.wildrank.desktop.modes;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.wildstang.wildrank.desktop.GlobalAppHandler;
import org.wildstang.wildrank.desktop.TextFieldHintHandler;
import org.wildstang.wildrank.desktop.utils.JSONTools;
import org.wildstang.wildrank.desktop.utils.Logger;

public class EventSelector extends Mode implements ActionListener {
	List<JButton> eventButtons = new ArrayList<JButton>();
	List<String> eventKeys = new ArrayList<String>();
	JTextField team;
	JTextField year;
	JButton loadEvents;
	JLabel current;
	JButton skip;

	@Override
	public void initializePanel() {
		team = new JTextField("Team");
		team.addFocusListener(new TextFieldHintHandler(team, "Team"));
		year = new JTextField("Year");
		year.addFocusListener(new TextFieldHintHandler(year, "Year"));
		loadEvents = new JButton("Load events");
		loadEvents.addActionListener(this);
		current = new JLabel(appData.getEventKey());
		skip = new JButton("Skip");
		skip.addActionListener(this);
		c.gridx = 0;
		c.gridy = 0;
		panel.add(team, c);
		c.gridx = 1;
		panel.add(year, c);
		c.gridx = 2;
		panel.add(loadEvents, c);
		c.gridx = 0;
		c.gridy = 2;
		panel.add(current, c);
		c.gridx = 1;
		panel.add(skip, c);
		update.setMode("Event Selector");
	}

	public void loadEvents() {
		// Retrieve team data from TBA
		// This API subject to change
		// As of 1/2/2014, API v2 was still in beta
		String url = "http://www.thebluealliance.com/api/v2/team/frc" + team.getText() + "/" + year.getText();
		final String teamInfoString = JSONTools.getJsonFromUrl(url);
		Logger.getInstance().log("TeamInfo: " + teamInfoString);
		// Extract event list from that data
		Logger.getInstance().log("Parsing team data");
		try {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					JSONObject teamInfo = new JSONObject(teamInfoString);
					JSONArray teamEvents = teamInfo.getJSONArray("events");
					int x = 0;
					int y = 0;
					panel.removeAll();
					for (int i = 0; i < teamEvents.length(); i++) {
						JSONObject currentEvent = teamEvents.getJSONObject(i);
						eventKeys.add(currentEvent.getString("key"));
						String shortName = currentEvent.getString("short_name");
						eventButtons.add(new JButton(shortName));
						eventButtons.get(i).addActionListener(EventSelector.this);
						c.gridx = x;
						c.gridy = y;
						panel.add(eventButtons.get(i), c);
						x++;
						if (x == 3) {
							y++;
							x = 0;
						}
					}
				}
			});
		} catch (JSONException e) {
			e.printStackTrace();
		}
		GlobalAppHandler.getInstance().refreshPanel();
		Logger.getInstance().log("Parsing complete");
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == skip && appData.getEventKey() != null) {
			setMode(new EventConfigurationDownloader());
		}
		if (event.getSource() == loadEvents) {
			loadEvents();
		}
		for (int i = 0; i < eventButtons.size(); i++) {
			if (event.getSource() == eventButtons.get(i)) {
				appData.setEventKey(eventKeys.get(i));
				appData.save();
				setMode(new EventConfigurationDownloader());
			}
		}
	}
}

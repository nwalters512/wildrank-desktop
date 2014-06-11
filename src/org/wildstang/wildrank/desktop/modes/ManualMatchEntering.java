package org.wildstang.wildrank.desktop.modes;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.json.JSONObject;
import org.wildstang.wildrank.desktop.game.Game;
import org.wildstang.wildrank.desktop.game.Item;
import org.wildstang.wildrank.desktop.game.Item.ItemType;
import org.wildstang.wildrank.desktop.game.Section;
import org.wildstang.wildrank.desktop.utils.Logger;

public class ManualMatchEntering extends Mode implements ActionListener {
	List<JTextField> fields = new ArrayList<JTextField>();
	List<JCheckBox> boxes = new ArrayList<JCheckBox>();
	List<JLabel> labels = new ArrayList<JLabel>();
	Game game;
	JButton save;

	@Override
	protected void initializePanel() {
		game = appData.getGame();
		save = new JButton("Save");
		save.addActionListener(this);
		labels.add(new JLabel("Match"));
		labels.add(new JLabel("Team"));
		labels.add(new JLabel("Scouter"));
		fields.add(new JTextField());
		fields.add(new JTextField());
		fields.add(new JTextField());
		int startingx = 0;
		c.gridx = startingx;
		c.gridy = 0;
		for (int i = 0; i < fields.size(); i++) {
			c.gridx = startingx;
			panel.add(labels.get(i), c);
			c.gridx = startingx + 1;
			panel.add(fields.get(i), c);
			c.gridy++;
		}
		for (Section section : game.getSections()) {
			startingx += 2;
			c.gridy = 0;
			for (Item item : section.getItems()) {
				labels.add(new JLabel(item.getName()));
				if (item.getType() == ItemType.TYPE_BOOLEAN) {
					boxes.add(new JCheckBox());
					c.gridx = startingx;
					panel.add(labels.get(labels.size() - 1), c);
					c.gridx = startingx + 1;
					panel.add(boxes.get(boxes.size() - 1), c);
					c.gridy++;
				} else {
					fields.add(new JTextField());
					c.gridx = startingx;
					panel.add(labels.get(labels.size() - 1), c);
					c.gridx = startingx + 1;
					panel.add(fields.get(fields.size() - 1), c);
					c.gridy++;
				}
			}
		}
		c.gridy++;
		panel.add(save, c);
		Vector<JComponent> order = new Vector<JComponent>();
		for (int i = 0; i < fields.size(); i++) {
			order.add(fields.get(i));
		}
		for (int i = 0; i < boxes.size(); i++) {
			order.add(boxes.get(i));
		}
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == save) {
			JSONObject json = new JSONObject();
			JSONObject object;
			json.put("match_number", Integer.parseInt(fields.get(0).getText()));
			json.put("team_number", Integer.parseInt(fields.get(1).getText()));
			json.put("scouter_id", fields.get(2).getText());
			int counter = 3;
			int bcounter = 0;
			JSONObject scoring = new JSONObject();
			for (Section section : game.getSections()) {
				object = new JSONObject();
				for (Item item : section.getItems()) {
					switch (item.getType()) {
					case TYPE_NUM:
						object.put(item.getKey(), Integer.parseInt(fields.get(counter).getText()));
						counter++;
						break;
					case TYPE_BOOLEAN:
						object.put(item.getKey(), boxes.get(bcounter).isSelected());
						bcounter++;
						break;
					case TYPE_STRING:
						object.put(item.getKey(), fields.get(counter).getText());
						counter++;
						break;
					}
				}
				scoring.put(section.getKey(), object);
			}
			File file = new File(appData.getLocalLocation() + File.separator + "synced" + File.separator + "matches" + File.separator + Integer.parseInt(fields.get(0).getText()) + File.separator
					+ Integer.parseInt(fields.get(1).getText()) + ".json");
			Logger.getInstance().log(file.toString());
			if (!file.exists()) {
				file.getParentFile().mkdir();
				try {
					file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			json.put("scoring", scoring);
			try {
				BufferedWriter bw = new BufferedWriter(new FileWriter(file));
				bw.write(json.toString());
				bw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			for (int i = 0; i < fields.size(); i++) {
				fields.get(i).setText("");
			}
			for (int i = 0; i < boxes.size(); i++) {
				boxes.get(i).setSelected(false);
			}
		}
	}

}

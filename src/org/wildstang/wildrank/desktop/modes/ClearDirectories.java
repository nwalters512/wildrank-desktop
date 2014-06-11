package org.wildstang.wildrank.desktop.modes;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JProgressBar;
import javax.swing.JTextField;

import org.wildstang.wildrank.desktop.GlobalAppHandler;
import org.wildstang.wildrank.desktop.TextFieldHintHandler;
import org.wildstang.wildrank.desktop.utils.Logger;

public class ClearDirectories extends Mode implements ActionListener {
	JButton clear;
	JProgressBar bar;
	JTextField field;
	BufferedReader br;
	BufferedWriter bw;

	@Override
	protected void initializePanel() {
		bar = new JProgressBar();
		clear = new JButton("Clear Directories");
		clear.addActionListener(this);
		field = new JTextField("Enter Password");
		field.addFocusListener(new TextFieldHintHandler(field, "Enter Password"));
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 3;
		panel.add(field, c);
		c.gridy = 1;
		panel.add(clear, c);
		c.gridy = 2;
		panel.add(bar, c);
		update.setMode("Directory Clearer");
	}

	public void clearDirectories() {
		GlobalAppHandler.getInstance().disableBackButton();
		bar.setMaximum(5);
		File file = appData.getLocalLocation();
		if (file.exists()) {
			Logger.getInstance().log("Emptying: " + file.getAbsolutePath());
			deleteFilesInDirectory(file);
		}
		bar.setValue(3);
		bar.setValue(5);
		GlobalAppHandler.getInstance().enableBackButton();
	}

	public static void deleteFilesInDirectory(File directory) {
		Logger.getInstance().log("listFilesInDirectory; directory: " + directory.getAbsolutePath());
		File[] files = directory.listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.isFile() && !file.getName().equals("game.wild") && !file.getName().equals("unknown.png")) {
					file.delete();
				} else {
					deleteFilesInDirectory(file);
				}
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == clear && field.getText().equals("Wild111Stang")) {
			clearDirectories();
			setMode(new MainMenu());
		} else if (event.getSource() == clear) {
			field.setText("WRONG PASSWORD");
			field.addFocusListener(new TextFieldHintHandler(field, "WRONG PASSWORD"));
		}
	}
}
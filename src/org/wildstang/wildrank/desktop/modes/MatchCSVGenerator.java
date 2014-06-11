package org.wildstang.wildrank.desktop.modes;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JProgressBar;

import org.wildstang.wildrank.desktop.GlobalAppHandler;
import org.wildstang.wildrank.desktop.MatchReader;
import org.wildstang.wildrank.desktop.utils.FileUtilities;
import org.wildstang.wildrank.desktop.utils.Logger;

public class MatchCSVGenerator extends Mode implements ActionListener {
	JButton retrieve;
	JProgressBar bar;
	MatchReader reader;
	List<File> files = new ArrayList<File>();

	@Override
	public void initializePanel() {
		retrieve = new JButton("Generate Match CSV!");
		retrieve.addActionListener(this);
		bar = new JProgressBar();
		c.gridx = 0;
		c.gridy = 0;
		panel.add(retrieve, c);
		c.gridy = 1;
		panel.add(bar, c);
		update.setMode("Match CVS Gen");
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		GlobalAppHandler.getInstance().disableBackButton();
		if (event.getSource() == retrieve) {
			File csv = new File(FileUtilities.getNonsyncedDirectory() + File.separator + "event" + File.separator + "WildRank.csv");
			File matchesLocation = new File(FileUtilities.getSyncedDirectory() + File.separator + "matches");
			csv.getParentFile().mkdirs();
			matchesLocation.getParentFile().mkdirs();
			try {
				if (!csv.exists()) {
					update.updateData("Creating Local XML", 0, 0);
					csv.createNewFile();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				reader = new MatchReader(csv, appData);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			Logger.getInstance().log("Matches locaiton: " + matchesLocation.getAbsolutePath());
			listFilesForFolder(matchesLocation);
			update.updateData("Reading Matches", 0, files.size());
			bar.setMaximum(files.size());
			for (int i = 0; i < files.size(); i++) {
				try {
					update.updateData("Reading Matches", i + 1, files.size());
					reader.readAerialAssist(files.get(i));
					bar.setValue(i + 1);
				} catch (Exception e) {
					Logger.getInstance().log("Error in file " + files.get(i).getAbsolutePath());
					e.printStackTrace();
				}
			}
			update.updateData("Finished!", 0, 0);
			try {
				reader.finish();
			} catch (IOException e) {
				e.printStackTrace();
			}
			GlobalAppHandler.getInstance().enableBackButton();
			setMode(new MainMenu());
		}
	}

	public void listFilesForFolder(final File folder) {
		if (!folder.exists()) {
			folder.mkdir();
		}
		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				listFilesForFolder(fileEntry);
			} else {
				files.add(fileEntry);
			}
		}
	}
}

package org.wildstang.wildrank.desktop.modes;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JProgressBar;

import org.wildstang.wildrank.desktop.GlobalAppHandler;
import org.wildstang.wildrank.desktop.PitReader;
import org.wildstang.wildrank.desktop.utils.FileUtilities;
import org.wildstang.wildrank.desktop.utils.Logger;

public class PitCSVGenerator extends Mode implements ActionListener {
	JButton retrieve;
	JProgressBar progress;
	PitReader pitReader;

	@Override
	public void initializePanel() {
		retrieve = new JButton("Retrieve Pit!");
		retrieve.addActionListener(this);
		progress = new JProgressBar();
		c.gridx = 0;
		c.gridy = 0;
		panel.add(retrieve, c);
		c.gridy = 1;
		panel.add(progress, c);
		update.setMode("Pit CVS Gen");
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == retrieve) {
			GlobalAppHandler.getInstance().disableBackButton();

			File pitTextsDirectory = new File(FileUtilities.getNonsyncedDirectory() + File.separator + "pittexts");
			pitTextsDirectory.mkdir();
			try {
				pitReader = new PitReader(pitTextsDirectory);
			} catch (IOException e) {
				e.printStackTrace();
			}

			File folder = new File(FileUtilities.getSyncedDirectory() + File.separator + "pit" + File.separator);
			Logger.getInstance().log("Folder path: " + folder.getAbsolutePath());
			ArrayList<File> pitFiles = new ArrayList<File>();
			FileUtilities.listFilesInDirectory(folder, pitFiles);
			progress.setMaximum(pitFiles.size());
			int i = 0;
			for (File pitFile : pitFiles) {
				try {
					update.updateData("Reading Pit Data", i, pitFiles.size());
					pitReader.readAerialAssist(pitFile);
					progress.setValue(i);
				} catch (IOException e) {
					e.printStackTrace();
				}
				i++;
			}
			GlobalAppHandler.getInstance().enableBackButton();
			setMode(new MainMenu());
		}
	}
}

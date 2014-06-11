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
import javax.swing.JProgressBar;

import org.wildstang.wildrank.desktop.GlobalAppHandler;
import org.wildstang.wildrank.desktop.utils.FileUtilities;
import org.wildstang.wildrank.desktop.utils.Logger;

public class NoteCompiler extends Mode implements ActionListener {
	JButton compile;
	JProgressBar progress;
	BufferedReader br;
	BufferedWriter bw;

	@Override
	protected void initializePanel() {
		progress = new JProgressBar();
		compile = new JButton("Compile");
		compile.addActionListener(this);
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 3;
		panel.add(compile, c);
		c.gridy = 1;
		panel.add(progress, c);
		update.setMode("Note Compiler");
	}

	public void compile() throws IOException {
		GlobalAppHandler.getInstance().disableBackButton();
		File folder = new File(FileUtilities.getUnintegratedDirectory() + File.separator + "notes");
		Logger.getInstance().log("Folder path: " + folder.getAbsolutePath());
		List<File> files = new ArrayList<File>();
		FileUtilities.listFilesInDirectory(folder, files);
		File notesToCheck = new File(FileUtilities.getNonsyncedDirectory() + File.separator + "event" + File.separator + "notes_to_check.txt");
		if (!notesToCheck.exists()) {
			notesToCheck.getParentFile().mkdirs();
			notesToCheck.createNewFile();
		}
		BufferedWriter toCheck = new BufferedWriter(new FileWriter(notesToCheck, true));
		if (files != null) {
			for (File unintegratedFile : files) {
				File compiledFile = new File(FileUtilities.getSyncedDirectory() + File.separator + "notes" + File.separator + unintegratedFile.getName());
				if (!compiledFile.getParentFile().exists()) {
					compiledFile.getParentFile().mkdir();
				}
				if (!compiledFile.exists()) {
					compiledFile.createNewFile();
				}
				bw = new BufferedWriter(new FileWriter(compiledFile, true));
				br = new BufferedReader(new FileReader(unintegratedFile));
				String line;
				boolean firstLine = false;
				if (compiledFile.length() == 0) {
					firstLine = true;
				}
				while ((line = br.readLine()) != null) {
					if (!firstLine) {
						bw.newLine();
					}
					firstLine = false;
					bw.write(line);
				}
				if (unintegratedFile.length() != 0) {
					toCheck.write(unintegratedFile.getName());
					toCheck.newLine();
				}
				toCheck.flush();
				bw.flush();
				bw.close();
				br.close();
				unintegratedFile.delete();
			}
		}
		toCheck.close();
		GlobalAppHandler.getInstance().enableBackButton();
		setMode(new MainMenu());
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == compile) {
			try {
				compile();
			} catch (IOException e) {
				e.printStackTrace();
				GlobalAppHandler.getInstance().enableBackButton();
			}
		}
	}

}

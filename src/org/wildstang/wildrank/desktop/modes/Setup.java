package org.wildstang.wildrank.desktop.modes;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import org.wildstang.wildrank.desktop.GlobalAppHandler;
import org.wildstang.wildrank.desktop.game.GameReader;
import org.wildstang.wildrank.desktop.game.GameReader.GameReaderException;
import org.wildstang.wildrank.desktop.utils.FileUtilities;

public class Setup extends Mode implements ActionListener {
	JButton yes;
	JButton no;
	JLabel text;
	boolean shouldInitialize;

	public Setup() {
		if (FileUtilities.isSavedConfigFilePresent()) {
			shouldInitialize = true;
		} else {
			shouldInitialize = false;
		}
	}

	@Override
	protected void initializePanel() {
		if (shouldInitialize) {
			text = new JLabel("Load saved configuration?");
			yes = new JButton("Yes");
			yes.addActionListener(this);
			no = new JButton("No");
			no.addActionListener(this);
			c.gridx = 0;
			c.gridy = 0;
			c.gridwidth = 2;
			panel.add(text, c);
			c.gridwidth = 1;
			c.gridy = 1;
			panel.add(yes, c);
			c.gridx = 1;
			panel.add(no, c);
		} else {
			promptForSetup();
		}
		update.setMode("Setup");
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == yes) {
			if (!FileUtilities.isSavedConfigFilePresent()) {
				promptForSetup();
			} else {
				try {
					GlobalAppHandler.getInstance().disableBackButton();
					yes.setEnabled(false);
					no.setEnabled(false);
					appData.read();
					setupGame();
				} catch (IOException e) {
					e.printStackTrace();
				}
				GlobalAppHandler.getInstance().enableBackButton();
				setMode(new MainMenu());
			}

		} else if (event.getSource() == no) {
			promptForSetup();
		}
	}

	private void setupGame() {
		File gameFile = new File(appData.getLocalLocation() + File.separator + "game.wild");
		try {
			appData.setGame(GameReader.readFile(gameFile));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (GameReaderException e) {
			e.printStackTrace();
			JOptionPane.showOptionDialog(new JFrame(), "Error in game file!", "Error", JOptionPane.OK_OPTION, JOptionPane.ERROR_MESSAGE, null, null, null);
		}
	}

	private void promptForSetup() {
		appData.setFlashDriveLocation(MainMenu.getFlashDriveLocation());
		appData.setLocalLocation(MainMenu.getLocalLocation());
		setupGame();
		setMode(new EventSelector());
	}
}
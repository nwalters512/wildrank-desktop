package org.wildstang.wildrank.desktop.modes;

import java.awt.GridBagConstraints;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.wildstang.wildrank.desktop.AppData;
import org.wildstang.wildrank.desktop.GlobalAppHandler;
import org.wildstang.wildrank.desktop.Update;
import org.wildstang.wildrank.desktop.utils.Logger;

public abstract class Mode {
	protected static JPanel panel;
	private boolean initialized;
	protected AppData appData;
	Update update;
	GridBagConstraints c = new GridBagConstraints();

	public void initialize() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				appData = GlobalAppHandler.getInstance().getAppData();
				Logger.getInstance().log("Panel created");
				panel = GlobalAppHandler.getInstance().getGlobalPanel();
				panel.removeAll();
				c.fill = GridBagConstraints.BOTH;
				c.weightx = 1;
				c.weighty = 1;
				initialized = true;
				initializePanel();
			}
		});
		update = new Update();
	}

	protected abstract void initializePanel();

	public JPanel getModePanel() throws IllegalStateException {

		if (initialized && panel == null) {
			throw new IllegalStateException("Panel must not be null! Check initializePanel() for errors");
		} else if (initialized) {
			return panel;
		} else {
			throw new IllegalStateException("Mode must be initialized before you can get the panel!");
		}
	}

	public void setMode(Mode mode) {
		GlobalAppHandler.getInstance().setMode(mode);
	}
}

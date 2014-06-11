package org.wildstang.wildrank.desktop;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.wildstang.wildrank.desktop.modes.MainMenu;
import org.wildstang.wildrank.desktop.modes.Mode;
import org.wildstang.wildrank.desktop.modes.Setup;
import org.wildstang.wildrank.desktop.utils.Logger;

public class GlobalAppHandler implements ActionListener {
	private static GlobalAppHandler instance;
	private static JFrame window;
	private static JPanel content;
	private static AppData appData;
	private static JPanel backBar;
	private static JButton back;

	private static Mode mode;

	public static void main(String[] args) {
		getInstance().setMode(new Setup());
	}

	public static GlobalAppHandler getInstance() {
		if (instance == null) {
			instance = new GlobalAppHandler();
		}
		return instance;
	}

	public GlobalAppHandler() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				Logger.getInstance().printToSystem(true);
				Logger.getInstance().log("Created!");
				window = new JFrame("WildRank: Desktop");
				appData = new AppData(null, null, null);
				content = new JPanel();
				content.setLayout(new GridBagLayout());
				content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
				window.setMinimumSize(new Dimension(500, 300));
				window.setLocationRelativeTo(null);
				window.setResizable(true);
				window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				backBar = new JPanel();
				backBar.setLayout(new BorderLayout());
				backBar.setPreferredSize(new Dimension(350, 30));
				back = new JButton("Back to Main Menu");
				back.addActionListener(GlobalAppHandler.this);
				backBar.add(back, BorderLayout.WEST);
				window.getContentPane().setLayout(new BorderLayout());
				window.getContentPane().add(backBar, BorderLayout.NORTH);
				window.getContentPane().add(content, BorderLayout.CENTER);
				window.setVisible(true);
			}
		});
	}

	private void initializeNewMode() {
		mode.initialize();
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if (mode instanceof MainMenu) {
					back.setEnabled(false);
				} else {
					back.setEnabled(true);
				}
				window.setVisible(true);
				mode.getModePanel().repaint();
			}
		});
	}

	public void setMode(Mode newMode) {
		mode = newMode;
		initializeNewMode();
	}

	public AppData getAppData() {
		return appData;
	}

	public JPanel getGlobalPanel() {
		return content;
	}

	public JFrame getWindow() {
		return window;
	}

	public void refreshPanel() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				content.repaint();
				window.pack();
				window.setLocationRelativeTo(null);
				window.setVisible(true);
			}
		});

	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == back) {
			setMode(new MainMenu());
		}
	}

	public void disableBackButton() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				back.setEnabled(false);
			}
		});
	}

	public void enableBackButton() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				back.setEnabled(true);
			}
		});
	}
}

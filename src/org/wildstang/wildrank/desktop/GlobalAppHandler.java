package org.wildstang.wildrank.desktop;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
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
	private static JPanel sidebar;
	private static AppData appData;
	private static JPanel backBar;
	private static JButton back;
	private static JButton setLocal;
	private static JButton setFlashDrive;
	private static JButton save;
	private static JButton logPanel;
	private static JLabel event;

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
				
				//left side
				sidebar = new JPanel();
				sidebar.setLayout(new GridBagLayout());
				sidebar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 0));
				GridBagConstraints c = new GridBagConstraints();
				c.gridx = 0;
				c.gridy = 0;
				c.gridwidth = 1;
				c.anchor = GridBagConstraints.NORTH;
				c.fill = GridBagConstraints.VERTICAL;
				JLabel wr = new JLabel("<html><b>WildRank<br>Desktop");
				wr.setFont(new Font(wr.getFont().getName(), Font.PLAIN, 25));
				sidebar.add(wr, c);
				c.gridy = 1;
				c.anchor = GridBagConstraints.NORTHWEST;
				sidebar.add(new JLabel("<html><u>Directories"), c);
				c.gridy = 2;
				//sidebar.add(new JLabel("Local:"), c);
				//c.gridy = 4;
				setLocal = new JButton("Set Local Dir");
				setLocal.addActionListener(GlobalAppHandler.this);
				c.fill = GridBagConstraints.BOTH;
				sidebar.add(setLocal, c);
				c.gridy = 3;
				//sidebar.add(new JLabel("Flash Drive:"), c);
				//c.gridy = 6;
				setFlashDrive = new JButton("Set Flash Dir");
				setFlashDrive.addActionListener(GlobalAppHandler.this);
				sidebar.add(setFlashDrive, c);
				c.gridy = 4;
				sidebar.add(new JLabel("<html><u>Event</u></html>"), c);
				c.gridy = 5;
				event = new JLabel("none");
				sidebar.add(event, c);
				c.gridy = 6;
				save = new JButton("Save Config");
				save.addActionListener(GlobalAppHandler.this);
				sidebar.add(save, c);
				
				window.setMinimumSize(new Dimension(400, 350));
				window.setLocationRelativeTo(null);
				window.setResizable(true);
				window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				backBar = new JPanel();
				backBar.setLayout(new BorderLayout());
				backBar.setPreferredSize(new Dimension(350, 30));
				back = new JButton("Back to Main Menu");
				back.addActionListener(GlobalAppHandler.this);
				backBar.add(back, BorderLayout.WEST);
				logPanel = new JButton("Show/Hide Log");
				logPanel.addActionListener(GlobalAppHandler.this);
				backBar.add(logPanel, BorderLayout.EAST);
				window.getContentPane().setLayout(new BorderLayout());
				window.getContentPane().add(sidebar, BorderLayout.WEST);
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

	public static void updateDirs()
	{
		setLocal.setToolTipText(appData.getLocalLocation().toString());
		setFlashDrive.setToolTipText(appData.getFlashDriveLocation().toString());
	}
	
	public static void updateEvent()
	{
		event.setText(appData.getEventKey());
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
		else if (event.getSource() == setLocal) {
			appData.setFlashDriveLocation(MainMenu.getLocalLocation());
			GlobalAppHandler.updateDirs();
		}
		else if (event.getSource() == setFlashDrive) {
			appData.setFlashDriveLocation(MainMenu.getFlashDriveLocation());
			GlobalAppHandler.updateDirs();
		}
		else if (event.getSource() == save) {
			appData.save();
		} 
		else if (event.getSource() == logPanel) {
			Logger.getInstance().toggleVisiblity();
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

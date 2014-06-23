package org.wildstang.wildrank.desktop.utils;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class Logger {
	
	private static final int MAX_ARRAY_SIZE = 100;

	private static Logger instance;
	private JFrame frame;
	private JTextArea log;
	private JScrollPane scrollpane;
	private boolean printToSystem = true;
	private List<String> messages = new ArrayList<String>();

	public static Logger getInstance() {
		if (instance == null) {
			instance = new Logger();
		}
		return instance;
	}

	private Logger() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				frame = new JFrame("Log");
				log = new JTextArea(MAX_ARRAY_SIZE, 1);
				log.setEditable(false);
				scrollpane = new JScrollPane(log);
				frame.setSize(new Dimension(300, 500));
				frame.setResizable(true);
				frame.getContentPane().setLayout(new BorderLayout());
				frame.getContentPane().add(scrollpane, BorderLayout.CENTER);
				frame.setLocation(new Point(100, 100));
				frame.setVisible(true);
			}
		});
	}

	public void toggleVisiblity()
	{
		frame.setVisible(!frame.isVisible());
	}
	
	public void printToSystem(boolean print) {
		printToSystem = print;
	}

	public void log(String message) {
		// We store logs in an ArrayList instead of writing them straight to the
		// JTextArea so that we aren't trying to write to the JTextArea before it is created
		messages.add(message + "\n");
		if (log != null) {
			for (String string : messages) {
				log.append(string);
			}
			while(messages.size() > 0) {
				messages.remove(messages.size() - 1);
			}
		}
		if (printToSystem) {
			System.out.println(message);
		}
	}

	public void log(int message) {
		log("" + message);
	}

	public void log(double message) {
		log("" + message);
	}

	public void log(Object message) {
		log(message.toString());
	}

}

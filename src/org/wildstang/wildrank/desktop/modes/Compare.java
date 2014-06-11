package org.wildstang.wildrank.desktop.modes;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.json.JSONObject;
import org.wildstang.wildrank.desktop.game.Game;
import org.wildstang.wildrank.desktop.game.Item;
import org.wildstang.wildrank.desktop.game.Section;
import org.wildstang.wildrank.desktop.game.Stat;
import org.wildstang.wildrank.desktop.utils.FileUtilities;
import org.wildstang.wildrank.desktop.utils.JSONTools;
import org.wildstang.wildrank.desktop.utils.Logger;

public class Compare extends Mode implements ActionListener {
	JButton refresh;
	JTextField teama;
	JTextField teamb;
	JLabel aLabela;
	JLabel bLabela;
	JLabel aLabelb;
	JLabel bLabelb;
	JLabel titles;
	JLabel titlesb;

	@Override
	protected void initializePanel() {
		refresh = new JButton("Refresh");
		refresh.addActionListener(this);
		teama = new JTextField();
		teamb = new JTextField();
		aLabela = new JLabel("select a team");
		bLabela = new JLabel("select a team");
		aLabelb = new JLabel("select a team");
		bLabelb = new JLabel("select a team");
		titles = new JLabel("select a team");
		titlesb = new JLabel("select a team");
		c.gridx = 0;
		c.gridy = 1;
		panel.add(teama, c);
		c.gridx = 0;
		panel.add(refresh, c);
		c.gridx = 2;
		panel.add(teamb, c);
		c.gridy = 1;
		c.gridx = 0;
		panel.add(titles, c);
		c.gridx = 1;
		panel.add(aLabela, c);
		c.gridx = 2;
		panel.add(bLabela, c);
		c.gridx = 3;
		panel.add(titlesb, c);
		c.gridx = 4;
		panel.add(aLabelb, c);
		c.gridx = 5;
		panel.add(bLabelb, c);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == refresh) {
			File dir = new File(FileUtilities.getSyncedDirectory() + File.separator + "matches");
			List<File> aFiles = new ArrayList<File>();
			getMatches(dir, aFiles, Integer.parseInt(teama.getText()));
			List<File> bFiles = new ArrayList<File>();
			getMatches(dir, bFiles, Integer.parseInt(teamb.getText()));
			List<Stat> aStats = setup();
			List<Stat> bStats = setup();
			for (int i = 0; i < aFiles.size(); i++) {
				try {
					read(aFiles.get(i), aStats);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			for (int i = 0; i < bFiles.size(); i++) {
				try {
					read(bFiles.get(i), bStats);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			setupTitles(setup());
			statAdder(aLabela, aLabelb, aStats);
			statAdder(bLabela, bLabelb, bStats);
		}
	}

	public void getMatches(File directory, List<File> list, int team) {
		Logger.getInstance().log("listFilesInDirectory; directory: " + directory.getAbsolutePath());
		File[] files = directory.listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.isFile() && !file.isHidden() && file.getName().equals(Integer.toString(team) + ".json")) {
					list.add(file);
				} else {
					getMatches(file, list, team);
				}
			}
		}
	}

	public List<Stat> setup() {
		Game game = appData.getGame();
		List<Stat> stats = new ArrayList<Stat>();
		for (Section section : game.getSections()) {
			for (Item item : section.getItems()) {
				stats.add(new Stat(item));
			}
		}
		return stats;
	}

	public void read(File json, List<Stat> stats) throws IOException {
		Game game = appData.getGame();
		String fileString = JSONTools.getJsonFromFile(json);
		JSONObject jsonMatch = new JSONObject(fileString);
		JSONObject main = jsonMatch.getJSONObject(game.getMainKey());
		JSONObject object;
		for (Section section : game.getSections()) {
			object = main.getJSONObject(section.getKey());
			for (Item item : section.getItems()) {
				for (Stat stat : stats) {
					if (stat.value.getKey().equals(item.getKey())) {
						switch (item.getType()) {
						case TYPE_NUM:
							stat.amount += object.getInt(item.getKey());
							break;
						case TYPE_BOOLEAN:
							stat.amount += object.getBoolean(item.getKey()) ? 1 : 0;
							break;
						default:
							break;
						}
						stat.matches++;
					}
				}
			}
		}
	}

	public void statAdder(JLabel a, JLabel b, List<Stat> stats) {
		StringBuilder sba = new StringBuilder();
		StringBuilder sbb = new StringBuilder();
		sba.append("<html>");
		sbb.append("<html>");
		for (int i = 0; i < stats.size(); i++) {
			Stat stat = stats.get(i);
			if (i <= stats.size() / 2) {
				sba.append((double) stat.amount / (double) stat.matches + "<br>");
			} else {
				sbb.append((double) stat.amount / (double) stat.matches + "<br>");
			}
		}
		sba.append("</html>");
		sbb.append("</html>");
		a.setText(sba.toString());
		b.setText(sbb.toString());
	}

	public void setupTitles(List<Stat> stats) {
		StringBuilder sb = new StringBuilder();
		sb.append("<html>");
		StringBuilder sbb = new StringBuilder();
		sbb.append("<html>");
		for (int i = 0; i < stats.size(); i++) {
			Stat stat = stats.get(i);
			if (i <= stats.size() / 2) {
				sb.append(stat.value.getName() + "<br>");
			} else {
				sbb.append(stat.value.getName() + "<br>");
			}
		}
		sb.append("</html>");
		sbb.append("</html>");
		titles.setText(sb.toString());
		titlesb.setText(sbb.toString());
	}

}

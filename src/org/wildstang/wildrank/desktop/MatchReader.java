package org.wildstang.wildrank.desktop;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.wildstang.wildrank.desktop.game.Game;
import org.wildstang.wildrank.desktop.game.Item;
import org.wildstang.wildrank.desktop.game.Section;
import org.wildstang.wildrank.desktop.utils.JSONTools;
import org.wildstang.wildrank.desktop.utils.Logger;

public class MatchReader {
	CSVWriter writer = null;
	Game game;

	public MatchReader(File xml, AppData appData) throws IOException {
		writer = new CSVWriter(xml);
		game = appData.getGame();
		List<String> aaTop = new ArrayList<String>();
		aaTop.add("Match");
		aaTop.add("Team");
		aaTop.add("Scouter");
		for (Section section : game.getSections()) {
			for (Item item : section.getItems()) {
				aaTop.add(item.getName());
			}
		}
		writer.setTopValue(aaTop);
	}

	public void readAerialAssist(File json) throws IOException, JSONException {
		String fileString = JSONTools.getJsonFromFile(json);
		JSONObject jsonMatch = new JSONObject(fileString);
		List<String> values = new ArrayList<String>();
		values.add(Integer.toString(jsonMatch.getInt("match_number")));
		values.add(Integer.toString(jsonMatch.getInt("team_number")));
		Logger.getInstance().log(jsonMatch.getInt("team_number"));
		values.add("\"" + jsonMatch.getString("scouter_id") + "\"");
		JSONObject main = jsonMatch.getJSONObject(game.getMainKey());
		JSONObject object;
		for (Section section : game.getSections()) {
			object = main.getJSONObject(section.getKey());
			for (Item item : section.getItems()) {
				switch (item.getType()) {
				case TYPE_NUM:
					values.add(Integer.toString(object.getInt(item.getKey())));
					break;
				case TYPE_BOOLEAN:
					values.add(Integer.toString((object.getBoolean(item.getKey())) ? 1 : 0));
					break;
				case TYPE_STRING:
					values.add("\"" + object.getString(item.getKey()) + "\"");
					break;
				}
			}
		}
		writer.addLine(values);
	}

	public void finish() throws IOException {
		writer.finish();
	}
}

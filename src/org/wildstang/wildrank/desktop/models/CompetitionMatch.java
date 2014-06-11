package org.wildstang.wildrank.desktop.models;

public class CompetitionMatch {

	String key;
	int[] redAllaince;
	int[] blueAllaince;

	public enum CompetitionLevel {
		QUALIFICATION,
		QUARTERFINALS,
		SEMIFINALS,
		FINALS,
		DEFAULT
	}

	public CompetitionMatch(String key, int[] redAlliance, int[] blueAlliance) {
		// Key comes in the form "2010cmp_f1m1"
		this.key = key;
		this.redAllaince = redAlliance;
		this.blueAllaince = blueAlliance;
	}

	public static CompetitionLevel competitionLevelFromMatchKey(String key) {
		// Parse event level and match number from match key
		// qm = Qualification, qf = Quarterfinal, sf = Semifinal, f = Final
		CompetitionLevel level = CompetitionLevel.DEFAULT;
		String matchKey = matchKeyFromKey(key);
		if (matchKey.charAt(0) == 'q') {
			if (matchKey.charAt(1) == 'm') {
				// Qualification match
				level = CompetitionLevel.QUALIFICATION;
			} else {
				// Quarterfinal match
				level = CompetitionLevel.QUARTERFINALS;
			}
		} else if (matchKey.charAt(0) == 's') {
			// Semifinal match
			level = CompetitionLevel.SEMIFINALS;
		} else if (matchKey.charAt(0) == 'f') {
			// Final match
			level = CompetitionLevel.FINALS;
		}
		return level;
	}

	public static int matchNumberFromMatchKey(String key) {
		return Integer.parseInt(key.substring(key.lastIndexOf('m') + 1));
	}

	public static String eventKeyFromKey(String key) {
		return key.substring(0, key.indexOf("_") - 1);
	}

	public static String matchKeyFromKey(String key) {
		return key.substring(key.indexOf("_") + 1);
	}

	public String getKey() {
		return key;
	}

	public String getEventKey() {
		return eventKeyFromKey(key);
	}

	public String getMatchKey() {
		return matchKeyFromKey(key);
	}

	public CompetitionLevel getCompetitionLevel() {
		return competitionLevelFromMatchKey(key);
	}

	public int getMatchNumber() {
		return matchNumberFromMatchKey(key);
	}

	public int[] getRedAlliance() {
		return redAllaince;
	}

	public int[] getBlueAlliance() {
		return blueAllaince;
	}

	public boolean isElimMatch() {
		CompetitionLevel compLevel = getCompetitionLevel();
		if (compLevel == CompetitionLevel.QUARTERFINALS || compLevel == CompetitionLevel.SEMIFINALS || compLevel == CompetitionLevel.FINALS) {
			return true;
		} else {
			return false;
		}
	}

	public int getSetNumber() {
		return 0;
	}

}

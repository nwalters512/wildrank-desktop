package org.wildstang.wildrank.desktop;

import org.wildstang.wildrank.desktop.utils.Logger;

public class Update {
	public String mode;
	public String text;
	public int total = 0;
	public int current = 0;

	public Update() {
		mode = "WildRank";
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public void updateData(String text, int current, int total) {
		this.text = text;
		this.current = current;
		this.total = total;
		if (total == 0) {
			displayNotification();
		} else {
			if (current % 10 == 0 || current == total) {
				displayProgress();
			}
		}
	}

	public void displayProgress() {
		Logger.getInstance().log("[" + mode + "] " + text + " " + current + "/" + total);
	}

	public void displayNotification() {
		Logger.getInstance().log("[" + mode + "] " + text);
	}
}
package org.wildstang.wildrank.desktop;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CSVWriter {
	BufferedWriter bw;
	Boolean topWrote = false;

	public CSVWriter(File file) {
		try {
			bw = new BufferedWriter(new FileWriter(file));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setTopValue(List<String> values) {
		for (int i = 0; i < values.size(); i++) {
			try {
				if (i != values.size() - 1) {
					bw.write(values.get(i) + ",");
				} else {
					bw.write(values.get(i) + "\n");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		topWrote = true;
	}

	public void setTopValueA(String[] values) {
		for (int i = 0; i < values.length; i++) {
			try {
				if (i != values.length - 1) {
					bw.write(values[i] + ",");
				} else {
					bw.write(values[i] + "\n");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		topWrote = true;
	}

	public boolean addLine(List<String> values) {
		if (topWrote) {
			for (int i = 0; i < values.size(); i++) {
				try {
					if (i != values.size() - 1) {
						bw.write(values.get(i) + ",");
					} else {
						bw.write(values.get(i) + "\n");
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return true;
		} else {
			return false;
		}
	}

	public void finish() throws IOException {
		bw.close();
	}
}

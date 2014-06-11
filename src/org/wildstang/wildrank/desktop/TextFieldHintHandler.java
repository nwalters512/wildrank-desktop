package org.wildstang.wildrank.desktop;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTextField;

public class TextFieldHintHandler implements FocusListener {
	JTextField textField;
	String originalString;

	public TextFieldHintHandler(JTextField textField, String originalString) {
		this.textField = textField;
		this.originalString = originalString;
	}

	@Override
	public void focusGained(FocusEvent e) {
		if (textField.getText().trim().equals(originalString)) {
			textField.setText("");
		}
	}

	@Override
	public void focusLost(FocusEvent e) {
		if (textField.getText().trim().isEmpty()) {
			textField.setText(originalString);
		}
	}

}

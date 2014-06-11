package org.wildstang.wildrank.desktop.modes;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JTextField;

import org.wildstang.wildrank.desktop.GlobalAppHandler;
import org.wildstang.wildrank.desktop.TextFieldHintHandler;
import org.wildstang.wildrank.desktop.game.GameReader.GameReaderException;
import org.wildstang.wildrank.desktop.game.Item;
import org.wildstang.wildrank.desktop.game.Section;

//this is a class that allows you to create config files for reading match files
public class ConfigCreator extends Mode implements ActionListener {
	JTextField gameName;
	JTextField mainKey;
	JTextField sectionKey;
	JButton addItem;
	JButton write;
	String[] types = { "num", "bool", "text" };
	JTextField itemName;
	JTextField itemKey;
	JComboBox<String> itemType;
	List<Section> sections = new ArrayList<Section>();

	@Override
	protected void initializePanel() {
		gameName = new JTextField("Game Name");
		gameName.addFocusListener(new TextFieldHintHandler(gameName, "Game Name"));
		mainKey = new JTextField("Main Key");
		mainKey.addFocusListener(new TextFieldHintHandler(mainKey, "Main Key"));
		itemName = new JTextField("Item Name");
		itemName.addFocusListener(new TextFieldHintHandler(itemName, "Item Name"));
		itemKey = new JTextField("Item Key");
		itemKey.addFocusListener(new TextFieldHintHandler(itemKey, "Item Key"));
		sectionKey = new JTextField("Section Key");
		sectionKey.addFocusListener(new TextFieldHintHandler(sectionKey, "Section Key"));
		itemType = new JComboBox<String>(types);
		addItem = new JButton("Add Item");
		addItem.addActionListener(this);
		write = new JButton("Create Config");
		write.addActionListener(this);
		c.gridy = 0;
		c.gridx = 0;
		panel.add(addItem, c);
		c.gridx = 2;
		panel.add(write, c);
		c.gridx = 0;
		c.gridy = 1;
		panel.add(gameName, c);
		c.gridx = 1;
		panel.add(mainKey, c);
		c.gridx = 2;
		panel.add(sectionKey, c);
		c.gridx = 0;
		c.gridy = 2;
		panel.add(itemName, c);
		c.gridx = 1;
		panel.add(itemKey, c);
		c.gridx = 2;
		panel.add(itemType, c);
		update.setMode("Config Creator");
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == addItem) {
			String name = itemName.getText();
			String key = itemKey.getText();
			String type = types[itemType.getSelectedIndex()];
			Item item;
			try {
				item = new Item(name, key, type);
			} catch (GameReaderException e) {
				e.printStackTrace();
				return;
			}
			boolean sectionFound = false;
			// Look for existing section. If it exists, add the item
			for (Section section : sections) {
				if (section.getKey().equals(sectionKey.getText())) {
					sectionFound = true;
					section.addItem(item);
					break;
				}
			}
			// If the section does not exist, create it and add the item to it
			if (!sectionFound) {
				Section section = new Section(sectionKey.getText());
				section.addItem(item);
				sections.add(section);
			}
			// Reset text fields
			itemName.setText("Item Name");
			itemKey.setText("Item Key");
		}
		// Create the config file
		else if (event.getSource() == write) {
			GlobalAppHandler.getInstance().disableBackButton();
			try {
				BufferedWriter bw = new BufferedWriter(new FileWriter(new File(appData.getLocalLocation() + File.separator + "config.wild")));
				bw.write("game-name: " + gameName.getText() + "\n");
				bw.write("main-key: " + mainKey.getText() + "\n");
				for (Section section : sections) {
					bw.write("section-key: " + section.getKey() + "\n");
					for (Item item : section.getItems()) {
						bw.write("item: " + item.getName() + ", " + item.getKey() + "; " + item.getTypeString() + "\n");
					}
				}
				bw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			GlobalAppHandler.getInstance().enableBackButton();
		}
	}

}

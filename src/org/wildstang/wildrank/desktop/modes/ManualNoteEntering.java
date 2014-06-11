package org.wildstang.wildrank.desktop.modes;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JTextField;

import org.wildstang.wildrank.desktop.TextFieldHintHandler;
import org.wildstang.wildrank.desktop.utils.FileUtilities;

public class ManualNoteEntering extends Mode implements ActionListener
{
	JTextField team;
	JTextField note;
	JButton add;
	
	@Override
	protected void initializePanel()
	{
		team = new JTextField();
		team.addFocusListener(new TextFieldHintHandler(team, "Team"));
		note = new JTextField();
		note.addFocusListener(new TextFieldHintHandler(note, "Note"));
		add = new JButton("Add");
		add.addActionListener(this);
		c.gridx = 0;
		c.gridy = 0;
		panel.add(team, c);
		c.gridx = 1;
		panel.add(add, c);
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 2;
		panel.add(note, c);
	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		if(event.getSource() == add)
		{
			String teamnum = team.getText();
			String notetext = note.getText();
			File file = new File(FileUtilities.getSyncedDirectory() + File.separator + "notes" + File.separator + teamnum + ".txt");
			try
			{
				if(!file.exists())
				{
					file.createNewFile();
				}
				BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
				bw.write(notetext + "\n");
				note.setText("Note");
				team.setText("Team");
				bw.flush();
				bw.close();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

}

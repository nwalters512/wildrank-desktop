package org.wildstang.wildrank.desktop.modes;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JProgressBar;

import org.wildstang.wildrank.desktop.GlobalAppHandler;
import org.wildstang.wildrank.desktop.utils.FileUtilities;
import org.wildstang.wildrank.desktop.utils.Logger;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class PDFWriter extends Mode implements ActionListener {
	JButton generate;
	JProgressBar progress;
	BufferedReader br;
	BufferedWriter bw;
	JCheckBox all;
	JCheckBox notes;
	JCheckBox pit;
	List<String> teamsDone = new ArrayList<String>();

	public enum PDFType {
		TYPE_NOTES,
		TYPE_PIT,
		TYPE_ALL
	}

	@Override
	protected void initializePanel() {
		progress = new JProgressBar();
		generate = new JButton("Generate Selected PDFs");
		generate.addActionListener(this);
		all = new JCheckBox("All");
		all.setSelected(true);
		notes = new JCheckBox("Notes");
		notes.setSelected(true);
		pit = new JCheckBox("Pit");
		pit.setSelected(true);
		c.gridx = 0;
		c.gridy = 0;
		panel.add(all, c);
		c.gridx = 1;
		panel.add(notes, c);
		c.gridx = 2;
		panel.add(pit, c);
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 3;
		panel.add(generate, c);
		c.gridy = 2;
		panel.add(progress, c);
		update.setMode("PDF Writer");
	}

	public void generatePDF(PDFType type) throws IOException, DocumentException {
		GlobalAppHandler.getInstance().disableBackButton();
		String fileName = "WildRank";
		switch (type) {
		case TYPE_NOTES:
			fileName = "notes";
			update.updateData("Creating Notes PDF", 0, 0);
			break;
		case TYPE_PIT:
			fileName = "pit";
			update.updateData("Creating Pit PDF", 0, 0);
			break;
		case TYPE_ALL:
			fileName = "alldata";
			update.updateData("Creating All Data PDF", 0, 0);
			break;
		}
		File outputFile = new File(FileUtilities.getNonsyncedDirectory() + File.separator + "pdfs" + File.separator + fileName + ".pdf");
		outputFile.getParentFile().mkdirs();
		if (outputFile.exists()) {
			outputFile.delete();
		}
		outputFile.createNewFile();

		Document document = new Document();
		// step 2
		PdfWriter.getInstance(document, new FileOutputStream(outputFile));
		// step 3
		document.open();
		// step 4
		switch (type) {
		case TYPE_NOTES:
			document.add(new Phrase("WildRank Notes"));
			break;
		case TYPE_PIT:
			document.add(new Phrase("WildRank Pit Data"));
			break;
		case TYPE_ALL:
			document.add(new Phrase("WildRank Notes and Pit Data"));
		}
		document.add(createTable(type));
		// step 5
		document.close();
		GlobalAppHandler.getInstance().enableBackButton();
	}

	public PdfPTable createTable(PDFType type) throws IOException {
		progress.setValue(0);
		progress.setMaximum(1);
		// a table with two columns
		PdfPTable table = new PdfPTable(4);
		// List all our notes files
		File notesFolder = new File(FileUtilities.getSyncedDirectory() + File.separator + "notes");
		File pitFolder = new File(FileUtilities.getNonsyncedDirectory() + File.separator + "pittexts");
		Logger.getInstance().log("Folder path: " + pitFolder.getAbsolutePath());
		List<File> pitFiles = new ArrayList<File>();
		FileUtilities.listFilesInDirectory(pitFolder, pitFiles);
		if (type == PDFType.TYPE_PIT || type == PDFType.TYPE_ALL) {
			for (File file : pitFiles) {
				table.addCell(teamCell(file));
				boolean sisterFound = false;
				if (type == PDFType.TYPE_ALL) {
					List<File> otherFiles = new ArrayList<File>();
					FileUtilities.listFilesInDirectory(notesFolder, otherFiles);
					for (File otherFile : otherFiles) {
						if (getTeam(file).equals(getTeam(otherFile))) {
							sisterFound = true;
							teamsDone.add(getTeam(otherFile));
							table.addCell(dataCell(otherFile, file, type));
						}
					}
				}
				if (!sisterFound) {
					table.addCell(dataCell(null, file, type));
				}
			}
		}
		if (type == PDFType.TYPE_NOTES || type == PDFType.TYPE_ALL) {
			List<File> otherFiles = new ArrayList<File>();
			FileUtilities.listFilesInDirectory(notesFolder, otherFiles);
			for (File otherFile : otherFiles) {
				boolean found = false;
				if (type == PDFType.TYPE_ALL) {
					for (int i = 0; i < teamsDone.size(); i++) {
						if (getTeam(otherFile).equals(teamsDone.get(i))) {
							found = true;
						}
					}
				}
				if (!found) {
					table.addCell(teamCell(otherFile));
					table.addCell(dataCell(otherFile, null, type));
				}
			}
		}
		return table;
	}

	public PdfPCell teamCell(File file) {
		String teamNumber = getTeam(file);
		// we add a cell with colspan 3pit
		PdfPCell cell = new PdfPCell();
		cell.setPadding(5);
		cell.setColspan(1);
		Phrase tm = new Phrase(teamNumber);
		cell.addElement(new Phrase(tm));
		File imageFile = new File(FileUtilities.getSyncedDirectory() + File.separator + "images" + File.separator + teamNumber + ".jpg");
		if (imageFile.exists()) {
			try {
				cell.addElement(Image.getInstance(imageFile.getPath()));
			} catch (BadElementException | IOException e1) {
				e1.printStackTrace();
			}
		} else {
			try {
				cell.addElement(Image.getInstance(new File(FileUtilities.getSyncedDirectory() + File.separator + "images" + File.separator + "unknown.png").getPath()));
			} catch (BadElementException | IOException e1) {
				e1.printStackTrace();
			}
		}
		return cell;
	}

	public PdfPCell dataCell(File notes, File pit, PDFType type) throws IOException {
		PdfPCell cell = new PdfPCell();
		//Read the content from the saves notes file
		StringBuilder builder = new StringBuilder();
		BufferedReader br;
		if ((type == PDFType.TYPE_NOTES || type == PDFType.TYPE_ALL) && notes != null) {
			br = new BufferedReader(new FileReader(notes));
			String line = new String();
			builder.append("                                    Notes").append("\n");
			if (notes != null) {
				while ((line = br.readLine()) != null) {
					builder.append(line).append("\n");
				}
			}
			br.close();
		}
		if ((type == PDFType.TYPE_PIT || type == PDFType.TYPE_ALL) && pit != null) {
			br = new BufferedReader(new FileReader(pit));
			String line = new String();
			builder.append("                                   PitData").append("\n");
			if (pit != null) {
				while ((line = br.readLine()) != null) {
					builder.append(line).append("\n");
				}
			}
		}
		Logger.getInstance().log("content: " + builder.toString());
		cell = new PdfPCell(new Phrase(builder.toString()));
		cell.setPadding(5);
		cell.setColspan(5);
		return cell;
	}

	public String getTeam(File file) {
		return file.getName().replace(".txt", "");
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == generate) {
			try {
				if (all.isSelected()) {
					generatePDF(PDFType.TYPE_ALL);
				}
				if (notes.isSelected()) {
					generatePDF(PDFType.TYPE_NOTES);
				}
				if (pit.isSelected()) {
					generatePDF(PDFType.TYPE_PIT);
				}
			} catch (IOException | DocumentException e) {
				e.printStackTrace();
			}
			progress.setValue(1);
			update.updateData("Done!", 0, 0);
			setMode(new MainMenu());
		}
	}

}

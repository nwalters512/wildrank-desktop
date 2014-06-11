package org.wildstang.wildrank.desktop.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.io.FileUtils;
import org.wildstang.wildrank.desktop.GlobalAppHandler;

public class FileUtilities {

	public static boolean isUSBConnected() {
		// Test if USB is connected
		String flashDriveSyncedDirectoryString = GlobalAppHandler.getInstance().getAppData().getFlashDriveLocation() + File.separator + "synced";
		File flashDriveSyncedDirectory = new File(flashDriveSyncedDirectoryString);
		flashDriveSyncedDirectory.mkdir();
		if (flashDriveSyncedDirectory.exists()) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isSavedConfigFilePresent() {
		return new File("save.json").exists();
	}

	public static void syncWithFlashDrive() throws IOException {
		// IMPORTANT! First, make a backup of all the data we have now, both on the flash drive and on local
		Calendar c = Calendar.getInstance(TimeZone.getTimeZone("CST"));
		File localBackupDir = new File(getNonsyncedDirectory() + File.separator + "backups" + File.separator + "local" + File.separator + c.get(Calendar.YEAR) + "-" + c.get(Calendar.MONTH) + "-"
				+ c.get(Calendar.DAY_OF_MONTH) + "-" + c.get(Calendar.HOUR_OF_DAY) + "." + c.get(Calendar.MINUTE) + "." + c.get(Calendar.SECOND));
		localBackupDir.mkdirs();
		FileUtils.copyDirectory(getSyncedDirectory(), new File(localBackupDir + File.separator + "synced"));
		FileUtils.copyDirectory(getUnintegratedDirectory(), new File(localBackupDir + File.separator + "unintegrated"));
		Logger.getInstance().log("backup of local created");
		File flashBackupDir = new File(getNonsyncedDirectory() + File.separator + "backups" + File.separator + "flash" + File.separator + c.get(Calendar.YEAR) + "-" + c.get(Calendar.MONTH) + "-"
				+ c.get(Calendar.DAY_OF_MONTH) + "-" + c.get(Calendar.HOUR_OF_DAY) + "." + c.get(Calendar.MINUTE) + "." + c.get(Calendar.SECOND));
		flashBackupDir.mkdirs();
		FileUtils.copyDirectory(getFlashDriveSyncedDirectory(), new File(flashBackupDir + File.separator + "synced"));
		FileUtils.copyDirectory(getFlashDriveUnintegratedDirectory(), new File(flashBackupDir + File.separator + "unintegrated"));
		Logger.getInstance().log("backup of flash createdd");

		syncLocalAndFlashDirectories();
		Logger.getInstance().log("syncing synced directories done!");

		// Now we copy the contents of the unintegrated files on the flash drive to the local storage
		// We append the data instead of overwriting it
		List<File> fileList = new ArrayList<File>();
		listFilesInDirectory(getFlashDriveUnintegratedDirectory(), fileList);
		for (File file : fileList) {
			File destinationFile = new File(file.getAbsolutePath().replace(getFlashDriveUnintegratedDirectory().getAbsolutePath(), getUnintegratedDirectory().getAbsolutePath()));
			Logger.getInstance().log("destination: " + destinationFile.getAbsolutePath());
			copyFileWithAppend(file, destinationFile);
		}
		Logger.getInstance().log("flash unsynced done");

		// Next, wipe the unintegrated directory on the flash drive. All unintegrated
		// files are now stored locally and ready for integration.
		FileUtils.cleanDirectory(getFlashDriveUnintegratedDirectory());

		Logger.getInstance().log("flash unintegrated wiped");
	}

	private static void copyFileWithAppend(File source, File destination) throws IOException {
		if (!destination.exists()) {
			destination.getParentFile().mkdirs();
			destination.createNewFile();
		}
		if (!source.exists()) {
			throw new IOException("Source file must exist!");
		}
		BufferedReader sourceReader = new BufferedReader(new FileReader(source));
		BufferedWriter destinationWriter = new BufferedWriter(new FileWriter(destination, true));
		String line;
		while ((line = sourceReader.readLine()) != null) {
			destinationWriter.write(line);
			destinationWriter.newLine();
		}
		destinationWriter.flush();
		destinationWriter.close();
		sourceReader.close();
	}

	public static String getRelativePathForLocal(File file) {
		String absolutePath = file.getAbsolutePath();

		// First we find the length of the root path
		int startIndex = GlobalAppHandler.getInstance().getAppData().getLocalLocation().getAbsolutePath().length();
		// Next, we search for the next file separator character after that
		int fileSeparatorIndex = absolutePath.indexOf(File.separator, startIndex + 1);
		// If we remove all of the string before that character, we have the relative path!
		String relativePath = absolutePath.substring(fileSeparatorIndex);
		Logger.getInstance().log("relative path: " + relativePath);
		return relativePath;
	}

	public static String getRelativePathForFlashDrive(File file) {
		String absolutePath = file.getAbsolutePath();
		// First we find the length of the root path
		int startIndex = GlobalAppHandler.getInstance().getAppData().getFlashDriveLocation().getAbsolutePath().length();
		// Next, we search for the next file separator character after that
		int fileSeparatorIndex = absolutePath.indexOf(File.separator, startIndex + 1);
		// If we remove all of the string before that character, we have the relative path!
		String relativePath = absolutePath.substring(fileSeparatorIndex);
		Logger.getInstance().log("relative path: " + relativePath);
		return relativePath;
	}

	public static File getSyncedDirectory() {
		File file = new File(GlobalAppHandler.getInstance().getAppData().getLocalLocation() + File.separator + "synced");
		if (!file.exists()) {
			file.mkdirs();
		}
		return file;
	}

	public static File getUnintegratedDirectory() {
		File file = new File(GlobalAppHandler.getInstance().getAppData().getLocalLocation() + File.separator + "unintegrated");
		if (!file.exists()) {
			file.mkdirs();
		}
		return file;
	}

	public static File getNonsyncedDirectory() {
		File file = new File(GlobalAppHandler.getInstance().getAppData().getLocalLocation() + File.separator + "nonsynced");
		if (!file.exists()) {
			file.mkdirs();
		}
		return file;
	}

	public static File getFlashDriveSyncedDirectory() {
		File file = new File(GlobalAppHandler.getInstance().getAppData().getFlashDriveLocation() + File.separator + "synced");
		if (!file.exists()) {
			file.mkdirs();
		}
		return file;
	}

	public static File getFlashDriveUnintegratedDirectory() {
		File file = new File(GlobalAppHandler.getInstance().getAppData().getFlashDriveLocation() + File.separator + "unintegrated");
		if (!file.exists()) {
			file.mkdirs();
		}
		return file;
	}

	public static void listFilesInDirectory(File directory, List<File> list) {
		Logger.getInstance().log("listFilesInDirectory; directory: " + directory.getAbsolutePath());
		File[] files = directory.listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.isFile() && !file.isHidden()) {
					list.add(file);
				} else if (file.isDirectory()) {
					listFilesInDirectory(file, list);
				}
			}
		}
	}

	private static void syncLocalAndFlashDirectories() {
		long startTime = System.currentTimeMillis();
		int totalFiles = 0;
		// Get lists of files in both directories
		List<File> localSyncedFiles = new ArrayList<File>();
		listFilesInDirectory(getSyncedDirectory(), localSyncedFiles);
		List<File> flashSyncedFiles = new ArrayList<File>();
		listFilesInDirectory(getFlashDriveSyncedDirectory(), flashSyncedFiles);
		for (File f : localSyncedFiles) {
			Logger.getInstance().log("local: " + f.getAbsolutePath());
		}
		for (File f : flashSyncedFiles) {
			Logger.getInstance().log("flash: " + f.getAbsolutePath());
		}
		// Filter each list to have only relative locations

		List<String> localPaths = new ArrayList<String>();
		List<String> flashPaths = new ArrayList<String>();
		for (File file : localSyncedFiles) {
			localPaths.add(getRelativePathForLocal(file));
		}
		for (File file : flashSyncedFiles) {
			flashPaths.add(getRelativePathForFlashDrive(file));
		}
		Iterator<String> flashIterator = flashPaths.iterator();
		while (flashIterator.hasNext()) {
			String flashPath = flashIterator.next();
			totalFiles++;
			if (localPaths.contains(flashPath)) {
				File flashFile = new File(getFlashDriveSyncedDirectory() + File.separator + flashPath);
				File localFile = new File(getSyncedDirectory() + File.separator + flashPath);
				syncFile(flashFile, localFile);
				flashIterator.remove();
				localPaths.remove(flashPath);
			} else {
				File flashFile = new File(getFlashDriveSyncedDirectory() + File.separator + flashPath);
				File localFile = new File(getSyncedDirectory() + File.separator + flashPath);
				localFile.getParentFile().mkdirs();
				try {
					localFile.createNewFile();
					FileUtils.copyFile(flashFile, localFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
				flashIterator.remove();
			}
		}
		Iterator<String> localIterator = localPaths.iterator();
		while (localIterator.hasNext()) {
			totalFiles++;
			String localPath = localIterator.next();
			if (flashPaths.contains(localPath)) {
				File flashFile = new File(getFlashDriveSyncedDirectory() + File.separator + localPath);
				File localFile = new File(getSyncedDirectory() + File.separator + localPath);
				syncFile(flashFile, localFile);
				flashPaths.remove(localPaths);
				localIterator.remove();
			} else {
				File flashFile = new File(getFlashDriveSyncedDirectory() + File.separator + localPath);
				File localFile = new File(getSyncedDirectory() + File.separator + localPath);
				localFile.getParentFile().mkdirs();
				try {
					localFile.createNewFile();
					FileUtils.copyFile(localFile, flashFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
				localIterator.remove();
			}
		}
		long totalTime = System.currentTimeMillis() - startTime;
		Logger.getInstance().log("Total time for sync: " + totalTime + "ms");
		if (totalFiles != 0) {
			Logger.getInstance().log("Average time per file: " + (totalTime / totalFiles) + "ms");
		}
	}

	private static void syncFile(File file1, File file2) {
		long timestamp1 = file1.lastModified();
		long timestamp2 = file2.lastModified();
		try {
			if (timestamp1 > timestamp2) {
				FileUtils.copyFile(file1, file2);
			} else if (timestamp1 < timestamp2) {
				FileUtils.copyFile(file2, file1);
			} else {
				// If timestamp is the same, we can assume that the files are identical
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

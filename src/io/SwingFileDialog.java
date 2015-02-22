package io;

import java.awt.HeadlessException;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import draw.Settings;

public class SwingFileDialog implements SaveFileDialog, OpenFileDialog {

	private JFileChooser chooser;
	private Settings settings;

	public SwingFileDialog(Settings settings) {
		this.settings = settings;
		chooser = new JFileChooser();
		FileFilter imageFilter = new FileNameExtensionFilter(
				"Portable Network Graphics (PNG)", "png");
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.setFileFilter(imageFilter);
		chooser.setMultiSelectionEnabled(false);
		loadInitialPathFromSettings();
	}

	private void loadInitialPathFromSettings() {
		String path = settings.getString("file_path", "");
		if (path != "") {
			File filePath = new File(path);
			if (filePath.exists())
				chooser.setCurrentDirectory(filePath);
		}
	}

	@Override
	public boolean askUserForSaveFileName() {
		boolean accepted = chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION;
		if (accepted && fileExists(getSaveFileName()))
			accepted = askIfUserWantsToOverwriteFile();
		storeCurrentDirectoryInSettings();
		return accepted;
	}

	private void storeCurrentDirectoryInSettings() {
		settings.setString("file_path", chooser.getCurrentDirectory()
				.getAbsolutePath());
	}

	private boolean fileExists(String fileName) {
		return new File(fileName).exists();
	}

	private boolean askIfUserWantsToOverwriteFile() {
		final String message = "The file already exists. Do you want to overwrite it?";
		try {
			return JOptionPane.showConfirmDialog(null, message) == JOptionPane.OK_OPTION;
		} catch (HeadlessException e) {
			return true;
		}
	}

	@Override
	public String getSaveFileName() {
		return ensurePngExtension(chooser.getSelectedFile().getAbsolutePath());
	}

	private String ensurePngExtension(String path) {
		if (path.endsWith(".png"))
			return path;
		return path + ".png";
	}

	@Override
	public boolean askUserForOpenFileName() {
		int result = chooser.showOpenDialog(null);
		storeCurrentDirectoryInSettings();
		return result == JFileChooser.APPROVE_OPTION;
	}

	@Override
	public String getOpenFileName() {
		return ensurePngExtension(chooser.getSelectedFile().getAbsolutePath());
	}

}

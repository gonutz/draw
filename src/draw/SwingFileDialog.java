package draw;

import java.awt.HeadlessException;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

public class SwingFileDialog implements SaveFileDialog, OpenFileDialog {

	private JFileChooser chooser;

	public SwingFileDialog() {
		chooser = new JFileChooser();
		FileFilter imageFilter = new FileNameExtensionFilter(
				"Portable Network Graphics (PNG)", "png");
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.setFileFilter(imageFilter);
		chooser.setMultiSelectionEnabled(false);
	}

	@Override
	public boolean askUserForSaveFileName() {
		boolean accepted = chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION;
		if (accepted && fileExists(getSaveFileName()))
			accepted = askIfUserWantsToOverwriteFile();
		return accepted;
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
		return chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION;
	}

	@Override
	public String getOpenFileName() {
		return ensurePngExtension(chooser.getSelectedFile().getAbsolutePath());
	}

}

package draw;

import java.awt.HeadlessException;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

public class SwingSaveFileDialog implements SaveFileDialog {

	private JFileChooser chooser;

	public SwingSaveFileDialog() {
		chooser = new JFileChooser();
		FileFilter imageFilter = new FileNameExtensionFilter(
				"Portable Network Graphics (PNG)", "png");
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.setFileFilter(imageFilter);
		chooser.setMultiSelectionEnabled(false);
	}

	@Override
	public boolean askUserForFileName() {
		boolean accepted = chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION;
		if (accepted && fileExists(getFileName()))
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
	public String getFileName() {
		return ensurePngExtension(chooser.getSelectedFile().getAbsolutePath());
	}

	private String ensurePngExtension(String path) {
		if (path.endsWith(".png"))
			return path;
		return path + ".png";
	}

}

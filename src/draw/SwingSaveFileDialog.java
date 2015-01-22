package draw;

import javax.swing.JFileChooser;
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
	}

	@Override
	public boolean askUserForFileName() {
		return chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION;
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

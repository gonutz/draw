package draw;

public class ImageSaveController {

	private SaveFileDialog saveDialog;
	private ImageProvider imageProvider;
	private ImageSaver saver;
	private ErrorDisplay errorDisplay;
	private String previousFileName;

	public ImageSaveController(SaveFileDialog saveDialog,
			ImageProvider imageProvider, ImageSaver saver,
			ErrorDisplay errorDisplay) {
		this.saveDialog = saveDialog;
		this.imageProvider = imageProvider;
		this.saver = saver;
		this.errorDisplay = errorDisplay;
	}

	public void save() {
		if (previousFileName == null)
			saveAsNewFile();
		else
			saveAs(previousFileName);
	}

	public void saveAsNewFile() {
		if (saveDialog.askUserForSaveFileName())
			saveAs(saveDialog.getSaveFileName());
	}

	private void saveAs(String path) {
		try {
			saver.save(imageProvider.getImage(), path);
			previousFileName = path;
		} catch (ImageSaver.SaveFailedException e) {
			errorDisplay.showError(e.getMessage());
		}
	}

	public void newImageWasCreated() {
		previousFileName = null;
	}
}

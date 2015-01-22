package draw;

public class ImageSaveController {

	private SaveFileDialog saveDialog;
	private ImageProvider imageProvider;
	private ImageSaver saver;
	private ErrorDisplay errorDisplay;

	public ImageSaveController(SaveFileDialog saveDialog,
			ImageProvider imageProvider, ImageSaver saver,
			ErrorDisplay errorDisplay) {
		this.saveDialog = saveDialog;
		this.imageProvider = imageProvider;
		this.saver = saver;
		this.errorDisplay = errorDisplay;
	}

	public void save() {
		// TODO Auto-generated method stub

	}

	public void saveAsNewFile() {
		if (saveDialog.askUserForFileName())
			saveAs(saveDialog.getFileName());
	}

	private void saveAs(String path) {
		try {
			saver.save(imageProvider.getImage(), path);
		} catch (ImageSaver.SaveFailedException e) {
			errorDisplay.showError(e.getMessage());
		}
	}
}

package draw;

public class ImageLoadController {

	private OpenFileDialog openDialog;
	private ImageLoader loader;
	private ImageDisplay imageDisplay;
	private ErrorDisplay errorDisplay;

	public ImageLoadController(OpenFileDialog openDialog, ImageLoader loader,
			ImageDisplay imageDisplay, ErrorDisplay errorDisplay) {
		this.openDialog = openDialog;
		this.loader = loader;
		this.imageDisplay = imageDisplay;
		this.errorDisplay = errorDisplay;
	}

	public void load() {
		if (openDialog.askUserForFileName())
			loadImage(openDialog.getFileName());
	}

	private void loadImage(String fileName) {
		try {
			imageDisplay.showLoadedImage(loader.load(openDialog.getFileName()));
		} catch (ImageLoader.LoadFailedException e) {
			errorDisplay.showError(e.getMessage());
		}
	}
}

package io;

import draw.CurrentFileNameObserver;
import draw.ErrorDisplay;
import draw.ImageProvider;

public class ImageSaveController implements ImageLoadObserver {

	private SaveFileDialog saveDialog;
	private ImageProvider imageProvider;
	private ImageSaver saver;
	private ErrorDisplay errorDisplay;
	private String previousFileName;
	private CurrentFileNameObserver observer;

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
			setPreviousFileName(path);
		} catch (ImageSaver.SaveFailedException e) {
			errorDisplay.showError(e.getMessage());
		}
	}

	private void setPreviousFileName(String path) {
		previousFileName = path;
		observer.currentFileNameChangedTo(path);
	}

	public void newImageWasCreated() {
		setPreviousFileName(null);
	}

	@Override
	public void imageWasLoaded(String fileName) {
		setPreviousFileName(fileName);
	}

	public void setCurrentFileNameObserver(CurrentFileNameObserver observer) {
		this.observer = observer;
	}
}

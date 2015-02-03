package draw;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.awt.image.BufferedImage;

import org.junit.Before;
import org.junit.Test;

public class TestImageLoadController {

	class SpyOpenDialog implements OpenFileDialog {
		boolean userAccepts;
		String fileName;
		boolean userWasAked;

		@Override
		public boolean askUserForFileName() {
			userWasAked = true;
			return userAccepts;
		}

		@Override
		public String getFileName() {
			return fileName;
		}
	}

	class SpyImageLoader implements ImageLoader {
		private ImageLoader.LoadFailedException exception;
		private String fileName;
		private BufferedImage image;
		private boolean wasLoaded;

		@Override
		public BufferedImage load(String fileName) {
			wasLoaded = true;
			this.fileName = fileName;
			if (exception != null)
				throw exception;
			return image;
		}
	}

	class SpyImageDisplay implements ImageDisplay {
		private BufferedImage image;
		private boolean imageShown;

		@Override
		public void showLoadedImage(BufferedImage image) {
			imageShown = true;
			this.image = image;
		}
	}

	class SpyErrorDisplay implements ErrorDisplay {
		private String errorMessage;

		@Override
		public void showError(String message) {
			errorMessage = message;
		}
	}

	@Before
	public void setup() {
		openDialog = new SpyOpenDialog();
		loader = new SpyImageLoader();
		imageDisplay = new SpyImageDisplay();
		errorDisplay = new SpyErrorDisplay();
		controller = new ImageLoadController(openDialog, loader, imageDisplay,
				errorDisplay);
	}

	private SpyOpenDialog openDialog;
	private SpyImageLoader loader;
	private SpyImageDisplay imageDisplay;
	private SpyErrorDisplay errorDisplay;
	private ImageLoadController controller;

	@Test
	public void userProvidedFileNameIsLoaded() {
		openDialog.userAccepts = true;
		openDialog.fileName = "file";

		controller.load();

		assertTrue(openDialog.userWasAked);
		assertEquals("file", loader.fileName);
	}

	@Test
	public void ifUserAborts_NoFileIsLoaded() {
		openDialog.userAccepts = false;
		controller.load();
		assertFalse(loader.wasLoaded);
	}

	@Test
	public void loadedImageIsSetInImageDisplay() {
		openDialog.userAccepts = true;
		loader.image = new BufferedImage(2, 3, BufferedImage.TYPE_4BYTE_ABGR);

		controller.load();

		assertSame(loader.image, imageDisplay.image);
	}

	@Test
	public void ifLoadingFails_ErrorIsShownAndImageNotSet() {
		openDialog.userAccepts = true;
		loader.exception = new ImageLoader.LoadFailedException("message");

		controller.load();

		assertFalse(imageDisplay.imageShown);
		assertEquals("message", errorDisplay.errorMessage);
	}
}

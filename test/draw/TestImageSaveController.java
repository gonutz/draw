package draw;

import static org.junit.Assert.*;

import java.awt.image.BufferedImage;

import org.junit.Test;

public class TestImageSaveController {

	private class MockSaveFileDialog implements SaveFileDialog {
		public boolean fileNameWasAsked = false;
		public boolean userAccepts;
		public String fileName;

		@Override
		public boolean askUserForFileName() {
			fileNameWasAsked = true;
			return userAccepts;
		}

		@Override
		public String getFileName() {
			return fileName;
		}
	}

	private class StubImageProvider implements ImageProvider {
		private BufferedImage image;

		@Override
		public BufferedImage getImage() {
			return image;
		}
	}

	private class MockImageSaver implements ImageSaver {
		private BufferedImage image;
		private String fileName;
		private ImageSaver.SaveFailedException throwException;

		@Override
		public void save(BufferedImage image, String fileName) {
			this.image = image;
			this.fileName = fileName;
			if (throwException != null)
				throw throwException;
		}
	}

	private class SpyErrorDisplay implements ErrorDisplay {
		private String errorMessage;

		@Override
		public void showError(String message) {
			errorMessage = message;
		}
	}

	@Test
	public void savingNewImage_AsksUserForFileName() {
		MockSaveFileDialog dialog = new MockSaveFileDialog();
		dialog.userAccepts = false;
		ImageSaveController c = new ImageSaveController(dialog, null, null,
				null);

		c.saveAsNewFile();

		assertTrue(dialog.fileNameWasAsked);
	}

	@Test
	public void userSavingToNewImage_GetsImageAndSavesIt() {
		MockSaveFileDialog dialog = new MockSaveFileDialog();
		dialog.userAccepts = true;
		dialog.fileName = "some file";
		StubImageProvider imageProvider = new StubImageProvider();
		imageProvider.image = new BufferedImage(10, 5,
				BufferedImage.TYPE_4BYTE_ABGR);
		MockImageSaver saver = new MockImageSaver();
		ImageSaveController c = new ImageSaveController(dialog, imageProvider,
				saver, null);

		c.saveAsNewFile();

		assertTrue(dialog.fileNameWasAsked);
		assertEquals("some file", saver.fileName);
		assertSame(imageProvider.image, saver.image);
	}

	@Test
	public void ifSavingNewImageFails_UserIsPresentedWithErrorMessage() {
		MockSaveFileDialog dialog = new MockSaveFileDialog();
		dialog.userAccepts = true;
		StubImageProvider imageProvider = new StubImageProvider();
		imageProvider.image = new BufferedImage(10, 5,
				BufferedImage.TYPE_4BYTE_ABGR);
		MockImageSaver saver = new MockImageSaver();
		saver.throwException = new ImageSaver.SaveFailedException(
				"this is the message");
		SpyErrorDisplay errorDisplay = new SpyErrorDisplay();
		ImageSaveController c = new ImageSaveController(dialog, imageProvider,
				saver, errorDisplay);

		c.saveAsNewFile();

		assertTrue(dialog.fileNameWasAsked);
		assertSame(imageProvider.image, saver.image);
		assertEquals("this is the message", errorDisplay.errorMessage);
	}

}

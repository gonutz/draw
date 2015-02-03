package draw;

import static org.junit.Assert.*;

import java.awt.image.BufferedImage;

import org.junit.Before;
import org.junit.Test;

public class TestImageSaveController {

	private class MockSaveFileDialog implements SaveFileDialog {
		public boolean fileNameWasAsked = false;
		public boolean userAccepts;
		public String fileName;

		@Override
		public boolean askUserForSaveFileName() {
			fileNameWasAsked = true;
			return userAccepts;
		}

		@Override
		public String getSaveFileName() {
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
		private BufferedImage savedImage;
		private String fileName;
		private ImageSaver.SaveFailedException throwException;

		@Override
		public void save(BufferedImage image, String fileName) {
			this.savedImage = image;
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

	private class SpyCurrentFileNameObserver implements CurrentFileNameObserver {
		private String fileName;

		@Override
		public void currentFileNameChangedTo(String fileName) {
			this.fileName = fileName;
		}
	}

	@Before
	public void setup() {
		dialog = new MockSaveFileDialog();
		saver = new MockImageSaver();
		errorDisplay = new SpyErrorDisplay();
		imageProvider = new StubImageProvider();
		imageProvider.image = new BufferedImage(10, 5,
				BufferedImage.TYPE_4BYTE_ABGR);
		controller = new ImageSaveController(dialog, imageProvider, saver,
				errorDisplay);
		observer = new SpyCurrentFileNameObserver();
		controller.setCurrentFileNameObserver(observer);
	}

	private MockSaveFileDialog dialog;
	private StubImageProvider imageProvider;
	private MockImageSaver saver;
	private SpyErrorDisplay errorDisplay;
	private ImageSaveController controller;
	private SpyCurrentFileNameObserver observer;

	@Test
	public void savingNewImage_AsksUserForFileName() {
		dialog.userAccepts = false;
		controller.saveAsNewFile();
		assertTrue(dialog.fileNameWasAsked);
	}

	@Test
	public void userSavingToNewImage_GetsImageAndSavesIt() {
		dialog.userAccepts = true;
		dialog.fileName = "some file";

		controller.saveAsNewFile();

		assertTrue(dialog.fileNameWasAsked);
		assertEquals("some file", saver.fileName);
		assertSame(imageProvider.image, saver.savedImage);
	}

	@Test
	public void ifSavingNewImageFails_UserIsPresentedWithErrorMessage() {
		dialog.userAccepts = true;
		saver.throwException = new ImageSaver.SaveFailedException(
				"this is the message");

		controller.saveAsNewFile();

		assertTrue(dialog.fileNameWasAsked);
		assertSame(imageProvider.image, saver.savedImage);
		assertEquals("this is the message", errorDisplay.errorMessage);
	}

	@Test
	public void savingForTheFirstTime_BehavesLikeSavingAsNewFile() {
		dialog.userAccepts = true;
		dialog.fileName = "first time save";

		controller.save();

		assertTrue(dialog.fileNameWasAsked);
		assertEquals("first time save", saver.fileName);
		assertSame(imageProvider.image, saver.savedImage);
	}

	@Test
	public void savingTheSecondTime_UsesPreviousFileName() {
		saveAs("file");
		dialog.fileNameWasAsked = false;
		saver.fileName = null;

		controller.save();

		assertFalse(dialog.fileNameWasAsked);
		assertEquals("file", saver.fileName);
	}

	private void saveAs(String name) {
		dialog.userAccepts = true;
		dialog.fileName = name;
		controller.save();
	}

	@Test
	public void afterNewImage_SaveAsksForFileAgain() {
		saveAs("file");
		dialog.fileNameWasAsked = false;
		dialog.fileName = "new file";
		saver.fileName = null;

		controller.newImageWasCreated();
		controller.save();

		assertTrue(dialog.fileNameWasAsked);
		assertEquals("new file", saver.fileName);
	}

	@Test
	public void afterLoadingImage_FileNameIsUsedForSaving() {
		controller.imageWasLoaded("path");
		controller.save();
		assertEquals("path", saver.fileName);
	}

	@Test
	public void savingFileNotifiesObserverOfCurrentFileName() {
		dialog.userAccepts = true;
		dialog.fileName = "file path";

		controller.save();

		assertEquals("file path", observer.fileName);
	}

	@Test
	public void newImageNotifiesObserverThatNoFileNameIsCurrent() {
		dialog.userAccepts = true;
		dialog.fileName = "some file";
		controller.save();

		controller.newImageWasCreated();

		assertNull(observer.fileName);
	}

	@Test
	public void loadingImageNotifiesObserverOfCurrentFileName() {
		controller.imageWasLoaded("filename");
		assertEquals("filename", observer.fileName);
	}
}

package draw;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageToFileSaver implements ImageSaver {

	@Override
	public void save(BufferedImage image, String fileName) {
		File f = new File(fileName);
		boolean success = true;
		try {
			success = ImageIO.write(image, getUpperCaseExtension(fileName), f);
		} catch (IOException e) {
			throw new SaveFailedException(e.getMessage());
		} catch (Exception e) {
			throw new SaveFailedException(
					"Cannot save image at the given path.");
		}
		if (!success)
			throw new SaveFailedException(
					"The file format can not be written, no writer available.");
	}

	private String getUpperCaseExtension(String fileName) {
		String ext = getExtension(fileName);
		return ext.toUpperCase();
	}

	private String getExtension(String fileName) {
		int i = fileName.lastIndexOf('.');
		if (i > 0)
			return fileName.substring(i + 1);
		return "";
	}
}

package draw;

import java.awt.image.BufferedImage;

public interface ImageSaver {

	public static class SaveFailedException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		public SaveFailedException(String message) {
			super(message);
		}
	}

	/**
	 * Tries to save the image to the given file. If it fails, a
	 * SaveFailedException is thrown.
	 */
	void save(BufferedImage image, String fileName);

}

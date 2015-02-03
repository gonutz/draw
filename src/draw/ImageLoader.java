package draw;

import java.awt.image.BufferedImage;

public interface ImageLoader {

	public static class LoadFailedException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		public LoadFailedException(String message) {
			super(message);
		}
	}

	/**
	 * Tries to load the image at the given path. If it fails, a
	 * LoadFailedException is thrown.
	 */
	BufferedImage load(String fileName);

}

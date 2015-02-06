package draw;

import java.awt.image.BufferedImage;

public interface Clipboard {

	void storeImage(BufferedImage image);

	/**
	 * @return the image the clipboard if available or null if no data is stored
	 *         or if the data is not an image. Copy the image to be sure it is
	 *         not changed while you use it.
	 */
	BufferedImage getImage();

}

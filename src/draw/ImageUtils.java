package draw;

import java.awt.image.BufferedImage;

public class ImageUtils {

	public static BufferedImage copyImage(BufferedImage image) {
		if (image == null)
			return null;
		BufferedImage copy = new BufferedImage(image.getWidth(),
				image.getHeight(), image.getType());
		copy.getGraphics().drawImage(image, 0, 0, null);
		return copy;
	}
}

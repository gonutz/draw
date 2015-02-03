package draw;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageFromFileLoader implements ImageLoader {

	@Override
	public BufferedImage load(String fileName) {
		BufferedImage image;
		try {
			image = ImageIO.read(new File(fileName));
		} catch (IOException e) {
			throw new LoadFailedException(e.getMessage());
		}
		if (image == null)
			throw new LoadFailedException(
					"There is no decoder for this image format.");
		return image;
	}
}

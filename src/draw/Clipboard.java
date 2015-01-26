package draw;

import java.awt.Image;
import java.awt.image.BufferedImage;

public interface Clipboard {

	void storeImage(BufferedImage image);

	Image getImage();

}

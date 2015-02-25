package draw;

import java.awt.Color;
import java.awt.Graphics;
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

	public static void fillWithAlternatingColoredSquares(Graphics g, int width,
			int height) {
		final int size = 10;
		for (int x = 0; x <= width / size; x++)
			for (int y = 0; y <= height / size; y++) {
				g.setColor(lightSquare(x, y) ? Color.white : Color.lightGray);
				g.fillRect(x * size, y * size, size, size);
			}
	}

	private static boolean lightSquare(int x, int y) {
		return (x + y) % 2 == 0;
	}

}

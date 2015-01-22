package draw;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class NewImageCommand implements UndoableCommand {

	private BufferedImage oldImage;
	private int newWidth;
	private int newHeight;
	private Color newBackgroundColor;

	public NewImageCommand(BufferedImage oldImage, int newWidth, int newHeight,
			Color newBackgroundColor) {
		this.oldImage = oldImage;
		this.newWidth = newWidth;
		this.newHeight = newHeight;
		this.newBackgroundColor = newBackgroundColor;
	}

	@Override
	public void undoTo(ImageKeeper image) {
		image.setImage(oldImage);
	}

	@Override
	public void doTo(ImageKeeper image) {
		BufferedImage newImage = new BufferedImage(newWidth, newHeight,
				BufferedImage.TYPE_4BYTE_ABGR);
		clearImageTo(newImage, newBackgroundColor);
		image.setImage(newImage);
	}

	private void clearImageTo(BufferedImage image, Color c) {
		Graphics2D g = (Graphics2D) image.getGraphics();
		g.setBackground(c);
		g.clearRect(0, 0, image.getWidth(), image.getHeight());
	}

}

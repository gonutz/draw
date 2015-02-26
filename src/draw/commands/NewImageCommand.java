package draw.commands;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import draw.UndoContext;
import draw.ImageUtils;

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
	public void undoTo(UndoContext context) {
		context.setImage(ImageUtils.copyImage(oldImage));
	}

	@Override
	public void doTo(UndoContext context) {
		BufferedImage newImage = new BufferedImage(newWidth, newHeight,
				BufferedImage.TYPE_4BYTE_ABGR);
		clearImageTo(newImage, newBackgroundColor);
		context.setImage(newImage);
	}

	private void clearImageTo(BufferedImage image, Color c) {
		Graphics2D g = (Graphics2D) image.getGraphics();
		g.setBackground(c);
		g.clearRect(0, 0, image.getWidth(), image.getHeight());
	}

	@Override
	public boolean hasAnyEffect() {
		return true;
	}

}

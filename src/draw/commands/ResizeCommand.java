package draw.commands;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import draw.UndoContext;
import draw.ImageUtils;

public class ResizeCommand implements UndoableCommand {

	private int width;
	private int height;
	private Color backgroundColor;
	private BufferedImage originalImage;

	public ResizeCommand(int width, int height, Color backgroundColor) {
		this.width = width;
		this.height = height;
		this.backgroundColor = backgroundColor;
	}

	@Override
	public void undoTo(UndoContext context) {
		context.setImage(ImageUtils.copyImage(originalImage));
	}

	@Override
	public void doTo(UndoContext context) {
		BufferedImage image = context.getImage();
		originalImage = ImageUtils.copyImage(image);
		BufferedImage copy = new BufferedImage(width, height, image.getType());
		Graphics2D g = (Graphics2D) copy.getGraphics();
		g.setBackground(backgroundColor);
		g.clearRect(0, 0, width, height);
		g.drawImage(image, 0, 0, null);
		context.setImage(copy);
	}

	@Override
	public boolean hasAnyEffect() {
		return true;
	}

}

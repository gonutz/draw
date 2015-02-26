package draw.commands;

import java.awt.image.BufferedImage;

import draw.UndoContext;
import draw.ImageUtils;

public class ImageDisplayCommand implements UndoableCommand {

	private BufferedImage original;
	private BufferedImage loaded;

	public ImageDisplayCommand(BufferedImage previousImage, BufferedImage loaded) {
		original = ImageUtils.copyImage(previousImage);
		this.loaded = loaded;
	}

	@Override
	public void undoTo(UndoContext context) {
		context.setImage(ImageUtils.copyImage(original));
	}

	@Override
	public void doTo(UndoContext context) {
		context.setImage(ImageUtils.copyImage(loaded));
	}

	@Override
	public boolean hasAnyEffect() {
		return true;
	}

}

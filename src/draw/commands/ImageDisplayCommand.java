package draw.commands;

import java.awt.image.BufferedImage;

import draw.ImageKeeper;
import draw.ImageUtils;
import draw.ToolController;

public class ImageDisplayCommand implements UndoableCommand {

	private BufferedImage original;
	private BufferedImage loaded;

	public ImageDisplayCommand(BufferedImage previousImage, BufferedImage loaded) {
		original = ImageUtils.copyImage(previousImage);
		this.loaded = loaded;
	}

	@Override
	public void undoTo(ImageKeeper keeper, ToolController toolController) {
		keeper.setImage(ImageUtils.copyImage(original));
	}

	@Override
	public void doTo(ImageKeeper keeper, ToolController toolController) {
		keeper.setImage(ImageUtils.copyImage(loaded));
	}

}

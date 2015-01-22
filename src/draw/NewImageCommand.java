package draw;

import java.awt.image.BufferedImage;

public class NewImageCommand implements UndoableCommand {

	private BufferedImage oldImage;

	public NewImageCommand(BufferedImage image) {
		oldImage = image;
	}

	@Override
	public void undoTo(ImageKeeper image) {
		image.setImage(oldImage);
	}

	@Override
	public void doTo(ImageKeeper image) {
		// TODO Auto-generated method stub

	}

}

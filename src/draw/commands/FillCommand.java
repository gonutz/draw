package draw.commands;

import java.awt.Color;
import java.awt.image.BufferedImage;

import draw.ImageKeeper;
import draw.ToolController;

public class FillCommand implements UndoableCommand {

	public FillCommand(BufferedImage image, int x, int y, Color fillColor) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void undoTo(ImageKeeper keeper, ToolController toolController) {
		// TODO Auto-generated method stub

	}

	@Override
	public void doTo(ImageKeeper keeper, ToolController toolController) {
		// TODO Auto-generated method stub

	}

}

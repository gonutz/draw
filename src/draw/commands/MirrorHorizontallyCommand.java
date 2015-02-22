package draw.commands;

import java.awt.image.WritableRaster;

import draw.ImageKeeper;
import draw.Rectangle;
import draw.SelectionKeeper;
import draw.Tool;
import draw.ToolController;

public class MirrorHorizontallyCommand implements UndoableCommand {

	private Rectangle selection;
	private SelectionKeeper selectionKeeper;

	public MirrorHorizontallyCommand(Rectangle selection,
			SelectionKeeper selectionKeeper) {
		this.selection = selection.copy();
		this.selectionKeeper = selectionKeeper;
	}

	@Override
	public void undoTo(ImageKeeper keeper, ToolController toolController) {
		mirror(keeper, toolController);
	}

	@Override
	public void doTo(ImageKeeper keeper, ToolController toolController) {
		mirror(keeper, toolController);
	}

	private void mirror(ImageKeeper keeper, ToolController toolController) {
		int y = selection.top();
		int height = selection.height();
		int[] leftPixels = new int[4 * height];
		int[] rightPixels = new int[4 * height];
		WritableRaster raster = keeper.getImage().getRaster();
		for (int x = 0; x < selection.width() / 2; x++) {
			int left = selection.left() + x;
			leftPixels = raster.getPixels(left, y, 1, height, leftPixels);
			int right = selection.left() + selection.width() - 1 - x;
			rightPixels = raster.getPixels(right, y, 1, height, rightPixels);
			raster.setPixels(left, y, 1, height, rightPixels);
			raster.setPixels(right, y, 1, height, leftPixels);
		}
		toolController.selectTool(Tool.RectangleSelection);
		selectionKeeper.setSelection(selection.copy());
	}

	@Override
	public boolean hasAnyEffect() {
		return true;
	}

}

package draw.commands;

import java.awt.image.WritableRaster;

import draw.UndoContext;
import draw.Rectangle;
import draw.SelectionKeeper;
import draw.Tool;

public class MirrorVerticallyCommand implements UndoableCommand {

	private Rectangle selection;
	private SelectionKeeper selectionKeeper;

	public MirrorVerticallyCommand(Rectangle selection,
			SelectionKeeper selectionKeeper) {
		this.selection = selection.copy();
		this.selectionKeeper = selectionKeeper;
	}

	@Override
	public void undoTo(UndoContext context) {
		mirror(context);
	}

	@Override
	public void doTo(UndoContext context) {
		mirror(context);
	}

	private void mirror(UndoContext context) {
		int x = selection.left();
		int width = selection.width();
		int[] topPixels = new int[4 * width];
		int[] bottomPixels = new int[4 * width];
		WritableRaster raster = context.getImage().getRaster();
		for (int y = 0; y < selection.height() / 2; y++) {
			int top = selection.top() + y;
			topPixels = raster.getPixels(x, top, width, 1, topPixels);
			int bottom = selection.top() + selection.height() - 1 - y;
			bottomPixels = raster.getPixels(x, bottom, width, 1, bottomPixels);
			raster.setPixels(x, top, width, 1, bottomPixels);
			raster.setPixels(x, bottom, width, 1, topPixels);
		}
		context.selectTool(Tool.RectangleSelection);
		selectionKeeper.setSelection(selection.copy());
	}

	@Override
	public boolean hasAnyEffect() {
		return true;
	}

}

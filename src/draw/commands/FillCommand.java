package draw.commands;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.List;

import draw.ImageKeeper;
import draw.Tool;
import draw.ToolController;

public class FillCommand implements UndoableCommand {

	private int[] fillColor;
	private int[] oldColor;
	private int[] color = new int[4];
	private int x;
	private int y;
	private List<Integer> filledLines = new ArrayList<Integer>();

	public FillCommand(int x, int y, Color fillColor) {
		this.fillColor = new int[] { fillColor.getRed(), fillColor.getGreen(),
				fillColor.getBlue(), fillColor.getAlpha() };
		this.x = x;
		this.y = y;
	}

	@Override
	public void undoTo(ImageKeeper keeper, ToolController toolController) {
		BufferedImage image = keeper.getImage();
		WritableRaster raster = image.getRaster();
		for (int i = 0; i < filledLines.size(); i += 3) {
			int left = filledLines.get(i + 0);
			int right = filledLines.get(i + 1);
			int y = filledLines.get(i + 2);
			for (int x = left; x <= right; x++)
				raster.setPixel(x, y, oldColor);
		}
		toolController.selectTool(Tool.Fill);
	}

	@Override
	public void doTo(ImageKeeper keeper, ToolController toolController) {
		BufferedImage image = keeper.getImage();
		WritableRaster raster = image.getRaster();
		oldColor = raster.getPixel(x, y, new int[4]);
		if (isFillColor(oldColor))
			return;
		fillScanLines(raster, x, y, raster.getBounds());
		toolController.selectTool(Tool.Fill);
	}

	private void fillScanLines(WritableRaster raster, int x, int y,
			Rectangle bounds) {
		int left = findLeftBorder(raster, x, y);
		int right = findRightBorder(raster, x, y, bounds);
		fillScanLine(raster, y, left, right);
		fillAboveLine(raster, y, bounds, left, right);
		fillBelowLine(raster, y, bounds, left, right);
	}

	private int findLeftBorder(WritableRaster raster, int x, int y) {
		while (x >= 0 && isOldColor(raster.getPixel(x, y, color)))
			x--;
		return x + 1;
	}

	private int findRightBorder(WritableRaster raster, int x, int y,
			Rectangle bounds) {
		while (x < bounds.width && isOldColor(raster.getPixel(x, y, color)))
			x++;
		return x - 1;
	}

	private void fillScanLine(WritableRaster raster, int y, int left, int right) {
		for (int i = left; i <= right; i++)
			raster.setPixel(i, y, fillColor);
		filledLines.add(left);
		filledLines.add(right);
		filledLines.add(y);
	}

	private void fillAboveLine(WritableRaster raster, int y, Rectangle bounds,
			int left, int right) {
		if (y > 0)
			for (int i = left; i <= right; i++)
				fillScanLines(raster, i, y - 1, bounds);
	}

	private void fillBelowLine(WritableRaster raster, int y, Rectangle bounds,
			int left, int right) {
		if (y < bounds.height - 1)
			for (int i = left; i <= right; i++)
				fillScanLines(raster, i, y + 1, bounds);
	}

	private boolean isFillColor(int[] pixel) {
		return isSameColor(fillColor, pixel);
	}

	private boolean isOldColor(int[] pixel) {
		return isSameColor(oldColor, pixel);
	}

	private boolean isSameColor(int[] c1, int[] c2) {
		return c1[0] == c2[0] && c1[1] == c2[1] && c1[2] == c2[2]
				&& c1[3] == c2[3];
	}
}

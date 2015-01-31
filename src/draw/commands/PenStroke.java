package draw.commands;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import draw.Bresenham;
import draw.ImageKeeper;
import draw.Tool;
import draw.ToolController;
import draw.UndoableCommand;

public class PenStroke implements UndoableCommand {

	private Color strokeColor;
	private List<Pixel> pixels = new ArrayList<Pixel>();

	private class Pixel {
		private int x, y, oldColor;

		public Pixel(int x, int y, int oldColor) {
			this.x = x;
			this.y = y;
			this.oldColor = oldColor;
		}
	}

	public PenStroke(Color strokeColor) {
		this.strokeColor = strokeColor;
	}

	/**
	 * Adds to the current pen stroke the given line and paints it onto the
	 * given image. You can add any number of lines to a PenStroke and when
	 * undoing and redoing all these lines will be reverted or brought back,
	 * respectively.
	 */
	public void addLine(BufferedImage image, int fromX, int fromY, int toX,
			int toY) {
		Graphics g = image.getGraphics();
		g.setColor(strokeColor);
		int[] points = Bresenham.linePoints(fromX, fromY, toX, toY);
		for (int i = 0; i < points.length; i += 2) {
			int x = points[i];
			int y = points[i + 1];
			if (insideImage(x, y, image)) {
				addPixelChange(x, y, image.getRGB(x, y));
				g.drawLine(x, y, x, y);
			}
		}
	}

	private boolean insideImage(int x, int y, BufferedImage image) {
		return x >= 0 && y >= 0 && x < image.getWidth()
				&& y < image.getHeight();
	}

	public void addPixelChange(int x, int y, int oldColor) {
		pixels.add(0, new Pixel(x, y, oldColor));
	}

	public void undoTo(ImageKeeper image, ToolController toolController) {
		Graphics g = image.getImage().getGraphics();
		for (Pixel p : pixels) {
			g.setColor(new Color(p.oldColor));
			g.drawLine(p.x, p.y, p.x, p.y);
		}
		toolController.selectTool(Tool.Pen);
	}

	public void doTo(ImageKeeper image, ToolController toolController) {
		Graphics g = image.getImage().getGraphics();
		for (Pixel p : pixels) {
			g.setColor(strokeColor);
			g.drawLine(p.x, p.y, p.x, p.y);
		}
		toolController.selectTool(Tool.Pen);
	}
}

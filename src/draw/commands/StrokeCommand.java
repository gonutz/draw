package draw.commands;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import draw.Bresenham;
import draw.UndoContext;
import draw.Tool;

public class StrokeCommand implements UndoableCommand {

	private Color strokeColor;
	private List<PixelChange> pixelChanges = new ArrayList<PixelChange>();
	private Tool tool;

	private class PixelChange {
		private int x, y, oldColor;

		public PixelChange(int x, int y, int oldColor) {
			this.x = x;
			this.y = y;
			this.oldColor = oldColor;
		}
	}

	public StrokeCommand(Tool tool, Color strokeColor) {
		this.tool = tool;
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

	/**
	 * Sets the points of this stroke to be this one line. They can be undone
	 * and redone which will restore the pixels on the line.
	 */
	public void setLine(BufferedImage image, int fromX, int fromY, int toX,
			int toY) {
		pixelChanges.clear();
		addLine(image, fromX, fromY, toX, toY);
	}

	private boolean insideImage(int x, int y, BufferedImage image) {
		return x >= 0 && y >= 0 && x < image.getWidth()
				&& y < image.getHeight();
	}

	public void addPixelChange(int x, int y, int oldColor) {
		pixelChanges.add(0, new PixelChange(x, y, oldColor));
	}

	@Override
	public void undoTo(UndoContext context) {
		Graphics g = context.getImage().getGraphics();
		for (PixelChange p : pixelChanges) {
			g.setColor(new Color(p.oldColor));
			g.drawLine(p.x, p.y, p.x, p.y);
		}
		context.selectTool(tool);
	}

	@Override
	public void doTo(UndoContext context) {
		Graphics g = context.getImage().getGraphics();
		for (PixelChange p : pixelChanges) {
			g.setColor(strokeColor);
			g.drawLine(p.x, p.y, p.x, p.y);
		}
		context.selectTool(tool);
	}

	@Override
	public boolean hasAnyEffect() {
		int strokeRGB = strokeColor.getRGB();
		for (PixelChange p : pixelChanges)
			if (p.oldColor != strokeRGB)
				return true;
		return false;
	}
}

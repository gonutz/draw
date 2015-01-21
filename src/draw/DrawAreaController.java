package draw;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class DrawAreaController {

	private DrawAreaView view;
	private DrawSettings drawSettings;
	private BufferedImage image;
	private int lastX;
	private int lastY;
	private boolean buttonDown;
	private List<List<Pixel>> strokes = new ArrayList<List<Pixel>>();
	private Color drawColor;

	private class Pixel {
		private int x, y, color;

		public Pixel(int x, int y, int color) {
			this.x = x;
			this.y = y;
			this.color = color;
		}
	}

	public DrawAreaController(DrawAreaView view) {
		this.view = view;
	}

	public void setDrawSettings(DrawSettings drawSettings) {
		this.drawSettings = drawSettings;
	}

	public BufferedImage getImage() {
		return image;
	}

	public void newImage(int width, int height) {
		image = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
		clearImageTo(drawSettings.getBackgroundColor());
		view.refresh();
	}

	private void clearImageTo(Color c) {
		Graphics2D g = (Graphics2D) image.getGraphics();
		g.setBackground(c);
		g.clearRect(0, 0, image.getWidth(), image.getHeight());
	}

	public void leftMouseButtonDown(int x, int y) {
		drawColor = drawSettings.getForegroundColor();
		mouseDown(x, y);
	}

	public void rightMouseButtonDown(int x, int y) {
		drawColor = drawSettings.getBackgroundColor();
		mouseDown(x, y);
	}

	private void mouseDown(int x, int y) {
		if (drawSettings.getCurrentTool() == Tool.Pen) {
			startNewPath();
			line(x, y, x, y, drawColor);
			lastX = x;
			lastY = y;
			buttonDown = true;
			view.refresh();
		}
	}

	private void startNewPath() {
		strokes.add(0, new ArrayList<Pixel>());
	}

	private void line(int fromX, int fromY, int toX, int toY, Color color) {
		Graphics g = image.getGraphics();
		g.setColor(color);
		int[] points = Bresenham.linePoints(fromX, fromY, toX, toY);
		for (int i = 0; i < points.length; i += 2) {
			int x = points[i];
			int y = points[i + 1];
			if (insideImage(x, y)) {
				strokes.get(0).add(0, new Pixel(x, y, image.getRGB(x, y)));
				g.drawLine(x, y, x, y);
			}
		}
	}

	private boolean insideImage(int x, int y) {
		return x >= 0 && y >= 0 && x < image.getWidth()
				&& y < image.getHeight();
	}

	public void leftMouseButtonUp() {
		buttonDown = false;
	}

	public void rightMouseButtonUp() {
		buttonDown = false;
	}

	public void mouseMovedTo(int x, int y) {
		if (buttonDown) {
			line(lastX, lastY, x, y, drawColor);
			view.refresh();
		}
		lastX = x;
		lastY = y;
	}

	public void undoLastDrawAction() {
		if (!strokes.isEmpty())
			undoStroke(strokes.remove(0));
	}

	private void undoStroke(List<Pixel> stroke) {
		if (!stroke.isEmpty()) {
			resetPixels(stroke);
			view.refresh();
		}
	}

	private void resetPixels(List<Pixel> stroke) {
		Graphics g = image.getGraphics();
		for (Pixel p : stroke) {
			g.setColor(new Color(p.color, true));
			g.drawLine(p.x, p.y, p.x, p.y);
		}
	}
}

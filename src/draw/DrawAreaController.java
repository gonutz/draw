package draw;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class DrawAreaController {

	private DrawAreaView view;
	private DrawSettings drawSettings;
	private BufferedImage image;
	private int lastX;
	private int lastY;
	private boolean leftButtonDown;
	private List<List<Point>> strokePaths = new ArrayList<List<Point>>();

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
		if (drawSettings.getCurrentTool() == Tool.Pen) {
			line(x, y, x, y, drawSettings.getForegroundColor());
			lastX = x;
			lastY = y;
			strokePaths.add(0, new ArrayList<Point>());
			strokePaths.get(0).add(new Point(x, y));
			leftButtonDown = true;
			view.refresh();
		}
	}

	private void line(int fromX, int fromY, int toX, int toY, Color color) {
		Graphics g = image.getGraphics();
		g.setColor(color);
		g.drawLine(fromX, fromY, toX, toY);
	}

	public void leftMouseButtonUp() {
		leftButtonDown = false;
	}

	public void mouseMovedTo(int x, int y) {
		if (leftButtonDown) {
			line(lastX, lastY, x, y, drawSettings.getForegroundColor());
			strokePaths.get(0).add(new Point(x, y));
			view.refresh();
		}
		lastX = x;
		lastY = y;
	}

	public void undoLastDrawAction() {
		if (strokePaths.isEmpty())
			return;

		List<Point> path = strokePaths.get(0);
		strokePaths.remove(0);

		if (path.isEmpty())
			return;

		Point p0 = path.get(0);
		line(p0.x, p0.y, p0.x, p0.y, drawSettings.getBackgroundColor());

		for (int i = 0; i < path.size() - 1; i++) {
			p0 = path.get(i);
			Point p1 = path.get(i + 1);
			line(p0.x, p0.y, p1.x, p1.y, drawSettings.getBackgroundColor());
		}
		view.refresh();
	}

}

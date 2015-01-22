package draw;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class DrawAreaController implements ImageProvider, ImageKeeper {

	private DrawAreaView view;
	private DrawSettings drawSettings;
	private BufferedImage image;
	private Color drawColor;
	private int lastX;
	private int lastY;
	private boolean buttonDown;
	private UndoHistory history = new UndoHistory();
	private PenStroke currentStroke;

	public DrawAreaController(DrawAreaView view) {
		this.view = view;
	}

	public void setDrawSettings(DrawSettings drawSettings) {
		this.drawSettings = drawSettings;
	}

	public BufferedImage getImage() {
		return image;
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}

	public void newImage(int width, int height) {
		NewImageCommand newImageCommand = new NewImageCommand(image, width,
				height, drawSettings.getBackgroundColor());
		if (image != null) {
			history.addNewCommand(newImageCommand);
		}
		newImageCommand.doTo(this);
		view.refresh();
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
			currentStroke = history.startNewStroke();
			line(x, y, x, y);
			lastX = x;
			lastY = y;
			buttonDown = true;
			view.refresh();
		}
	}

	private void line(int fromX, int fromY, int toX, int toY) {
		Graphics g = image.getGraphics();
		g.setColor(drawColor);
		int[] points = Bresenham.linePoints(fromX, fromY, toX, toY);
		for (int i = 0; i < points.length; i += 2) {
			int x = points[i];
			int y = points[i + 1];
			if (insideImage(x, y)) {
				currentStroke.addPixelChange(x, y, image.getRGB(x, y),
						drawColor.getRGB());
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
			line(lastX, lastY, x, y);
			view.refresh();
		}
		lastX = x;
		lastY = y;
	}

	public void undoLastAction() {
		if (history.undoTo(this))
			view.refresh();
	}

	public void redoPreviousAction() {
		if (history.redoTo(this))
			view.refresh();
	}

}

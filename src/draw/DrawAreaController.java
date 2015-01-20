package draw;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class DrawAreaController {

	private DrawAreaView view;
	private DrawSettings drawSettings;
	private BufferedImage image;
	private int lastX;
	private int lastY;
	private boolean leftButtonDown;

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
		Graphics2D g = (Graphics2D) image.getGraphics();
		g.setBackground(drawSettings.getBackgroundColor());
		g.clearRect(0, 0, width, height);
		view.refresh();
	}

	public void leftMouseButtonDown(int x, int y) {
		if (drawSettings.getCurrentTool() == Tool.Pen) {
			Graphics g = image.getGraphics();
			g.setColor(drawSettings.getForegroundColor());
			g.drawLine(x, y, x, y);
			lastX = x;
			lastY = y;
			leftButtonDown = true;
			view.refresh();
		}
	}

	public void leftMouseButtonUp(int x, int y) {
		leftButtonDown = false;
	}

	public void mouseMovedTo(int x, int y) {
		if (leftButtonDown) {
			Graphics g = image.getGraphics();
			g.setColor(drawSettings.getForegroundColor());
			g.drawLine(lastX, lastY, x, y);
			view.refresh();
		}
		lastX = x;
		lastY = y;
	}

	public void undoLastDrawAction() {
		view.refresh();
	}

}

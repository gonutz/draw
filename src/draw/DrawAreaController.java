package draw;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class DrawAreaController {

	private DrawAreaView view;
	private DrawSettings drawSettings;
	private BufferedImage image;

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
		Graphics g = image.getGraphics();
		g.setColor(drawSettings.getForegroundColor());
		g.drawLine(x, y, x, y);
	}

	public void leftMouseButtonUp(int x, int y) {
		// TODO Auto-generated method stub

	}

}

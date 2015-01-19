package draw;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class DrawAreaController {

	private DrawAreaView view;
	private BufferedImage image;

	public DrawAreaController(DrawAreaView view) {
		this.view = view;
	}

	public BufferedImage getImage() {
		return image;
	}

	public void newImage(int width, int height, Color color) {
		image = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g = (Graphics2D) image.getGraphics();
		g.setBackground(color);
		g.clearRect(0, 0, width, height);
		view.refresh();
	}
}

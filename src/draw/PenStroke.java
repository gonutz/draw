package draw;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class PenStroke {

	private List<Pixel> pixels = new ArrayList<Pixel>();

	public void addPixelChange(int x, int y, int oldColor, int newColor) {
		pixels.add(0, new Pixel(x, y, oldColor, newColor));
	}

	public void undoTo(BufferedImage image) {
		Graphics g = image.getGraphics();
		for (Pixel p : pixels) {
			g.setColor(new Color(p.oldColor));
			g.drawLine(p.x, p.y, p.x, p.y);
		}
	}

	public void doTo(BufferedImage image) {
		Graphics g = image.getGraphics();
		for (Pixel p : pixels) {
			g.setColor(new Color(p.newColor));
			g.drawLine(p.x, p.y, p.x, p.y);
		}
	}

	private class Pixel {
		private int x, y, oldColor, newColor;

		public Pixel(int x, int y, int oldColor, int newColor) {
			this.x = x;
			this.y = y;
			this.oldColor = oldColor;
			this.newColor = newColor;
		}
	}
}

package draw;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

public class PenStroke implements UndoableCommand {

	private List<Pixel> pixels = new ArrayList<Pixel>();

	public void addPixelChange(int x, int y, int oldColor, int newColor) {
		pixels.add(0, new Pixel(x, y, oldColor, newColor));
	}

	public void undoTo(ImageKeeper image) {
		Graphics g = image.getImage().getGraphics();
		for (Pixel p : pixels) {
			g.setColor(new Color(p.oldColor));
			g.drawLine(p.x, p.y, p.x, p.y);
		}
	}

	public void doTo(ImageKeeper image) {
		Graphics g = image.getImage().getGraphics();
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

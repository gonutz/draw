package draw;

import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

import java.awt.Color;

import javax.swing.border.BevelBorder;

public class CurrentColors extends JPanel implements CurrentColorsView {

	private static final long serialVersionUID = 1L;
	private Color backgroundColor;
	private Color foregroundColor;

	public CurrentColors() {
		setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		setPreferredSize(new Dimension(61, 61));
		setLayout(null);
	}

	public void setBackgroundColor(Color color) {
		backgroundColor = color;
		repaint();
	}

	public void setForegroundColor(Color color) {
		foregroundColor = color;
		repaint();
	}

	@Override
	public void paint(Graphics g) {
		fillBackgroundWithAlternatingColoredSquares(g);
		paintBorder(g);
		g.setColor(backgroundColor);
		g.fillRect(23, 23, 30, 30);
		g.setColor(foregroundColor);
		g.fillRect(7, 7, 30, 30);
	}

	private void fillBackgroundWithAlternatingColoredSquares(Graphics g) {
		final int size = 10;
		for (int x = 0; x <= getWidth() / size; x++)
			for (int y = 0; y <= getHeight() / size; y++) {
				g.setColor(lightSquare(x, y) ? Color.white : Color.lightGray);
				g.fillRect(x * size, y * size, size, size);
			}
	}

	private boolean lightSquare(int x, int y) {
		return (x + y) % 2 == 0;
	}
}

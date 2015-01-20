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
		g.setColor(getBackground());
		g.fillRect(0, 0, getWidth(), getHeight());
		paintBorder(g);
		g.setColor(backgroundColor);
		g.fillRect(23, 23, 30, 30);
		g.setColor(foregroundColor);
		g.fillRect(7, 7, 30, 30);
	}
}

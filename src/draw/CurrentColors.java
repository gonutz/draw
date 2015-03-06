package draw;

import java.awt.*;
import java.awt.event.*;

import javax.swing.JColorChooser;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

public class CurrentColors extends JPanel implements CurrentColorsView {

	private static final long serialVersionUID = 1L;
	private ColorPaletteViewController controller;
	private Color backgroundColor;
	private Color foregroundColor;
	private RectangleWH backgroundRect = new RectangleWH(23, 23, 30, 30);
	private RectangleWH foregroundRect = new RectangleWH(7, 7, 30, 30);

	private class RectangleWH {
		public int x, y, width, height;

		public RectangleWH(int x, int y, int width, int height) {
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
		}

		public boolean contains(int x, int y) {
			return x >= this.x && y >= this.y && x < this.x + this.width
					&& y < this.y + this.height;
		}
	}

	public CurrentColors(final JColorChooser colorChooser) {
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2 && isLeftButton(e.getButton())) {
					if (foregroundRect.contains(e.getX(), e.getY())) {
						colorChooser.setColor(foregroundColor);
						JColorChooser.createDialog(null, "Select new color",
								true, colorChooser,
								new ForegroundColorChanger(), null).setVisible(
								true);
					} else if (backgroundRect.contains(e.getX(), e.getY())) {
						colorChooser.setColor(backgroundColor);
						JColorChooser.createDialog(null, "Select new color",
								true, colorChooser,
								new BackgroundColorChanger(), null).setVisible(
								true);
					}
				}
			}

			private boolean isLeftButton(int button) {
				return button == MouseEvent.BUTTON1;
			}

			class ForegroundColorChanger implements ActionListener {
				@Override
				public void actionPerformed(ActionEvent e) {
					setForegroundColor(colorChooser.getColor());
					controller.setForegroundColor(colorChooser.getColor());
				}
			}

			class BackgroundColorChanger implements ActionListener {
				@Override
				public void actionPerformed(ActionEvent e) {
					setBackgroundColor(colorChooser.getColor());
					controller.setBackgroundColor(colorChooser.getColor());
				}
			}
		});
		setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		setPreferredSize(new Dimension(61, 61));
		setLayout(null);
	}

	public void setController(ColorPaletteViewController controller) {
		this.controller = controller;
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
		ImageUtils
				.fillWithAlternatingColoredSquares(g, getWidth(), getHeight());
		paintBorder(g);

		g.setColor(backgroundColor);
		RectangleWH b = backgroundRect;
		g.fillRect(b.x, b.y, b.width, b.height);

		g.setColor(foregroundColor);
		RectangleWH f = foregroundRect;
		g.fillRect(f.x, f.y, f.width, f.height);
	}
}

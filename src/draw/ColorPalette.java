package draw;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ColorPalette extends JPanel implements ColorPaletteView {

	private static final long serialVersionUID = 1L;
	private ColorBox[] colorBoxes;
	private ColorPaletteViewController controller;
	private BufferedImage colorBackground;

	private class ColorBox extends JPanel {
		private static final long serialVersionUID = 1L;

		@Override
		protected void paintComponent(Graphics g) {
			g.drawImage(colorBackground, 0, 0, null);
			g.setColor(getBackground());
			g.fillRect(0, 0, getWidth(), getHeight());
		}
	}

	public ColorPalette(final JColorChooser colorChooser) {
		setBorder(null);
		setLayout(new GridLayout(3, 10, 1, 1));
		this.setPreferredSize(new Dimension(309, 92));
		fillInNumberLabels();
		fillWithColorBoxes();
		setClickListenersOnBoxes(colorChooser);
		createColorBackground();
	}

	private void fillInNumberLabels() {
		for (int i = 0; i < 10; i++)
			this.add(new JLabel("" + ((i + 1) % 10), JLabel.CENTER));
	}

	private void fillWithColorBoxes() {
		colorBoxes = new ColorBox[20];
		for (int i = 0; i < 20; i++) {
			colorBoxes[i] = new ColorBox();
			this.add(colorBoxes[i]);
		}
	}

	private void setClickListenersOnBoxes(final JColorChooser colorChooser) {
		for (int i = 0; i < 20; i++) {
			final int index = i;
			colorBoxes[i].addMouseListener(new MouseListener() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (isLeftClick(e))
						controller.selectForegroundColor(index);
					if (isRightClick(e))
						controller.selectBackgroundColor(index);
					if (isLeftOrRightDoubleClick(e)) {
						colorChooser.setColor(colorBoxes[index].getBackground());
						JColorChooser.createDialog(
								null,
								"Select new color",
								true,
								colorChooser,
								new ColorAcceptListener(colorChooser, index, e
										.getButton()), null).setVisible(true);
					}
				}

				private boolean isLeftClick(MouseEvent e) {
					return e.getClickCount() == 1
							&& e.getButton() == MouseEvent.BUTTON1;
				}

				private boolean isRightClick(MouseEvent e) {
					return e.getClickCount() == 1
							&& e.getButton() == MouseEvent.BUTTON3;
				}

				private boolean isLeftOrRightDoubleClick(MouseEvent e) {
					return e.getClickCount() == 2
							&& (e.getButton() == MouseEvent.BUTTON1 || e
									.getButton() == MouseEvent.BUTTON3);
				}

				@Override
				public void mouseEntered(MouseEvent e) {
				}

				@Override
				public void mouseExited(MouseEvent e) {
				}

				@Override
				public void mousePressed(MouseEvent e) {
					if (e.getButton() == MouseEvent.BUTTON1)
						controller.selectForegroundColor(index);
					else if (e.getButton() == MouseEvent.BUTTON3)
						controller.selectBackgroundColor(index);
				}

				@Override
				public void mouseReleased(MouseEvent e) {
				}
			});
		}
	}

	private class ColorAcceptListener implements ActionListener {
		int index;
		int button;
		JColorChooser colorChooser;

		public ColorAcceptListener(JColorChooser colorChooser, int index,
				int button) {
			this.colorChooser = colorChooser;
			this.index = index;
			this.button = button;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			Color color = colorChooser.getColor();
			controller.setPaletteEntry(index, color);
			if (isLeftButton(button))
				controller.setForegroundColor(color);
			else
				controller.setBackgroundColor(color);
		}

		private boolean isLeftButton(int button) {
			return button == MouseEvent.BUTTON1;
		}
	}

	private void createColorBackground() {
		final int size = 30;
		colorBackground = new BufferedImage(size, size,
				BufferedImage.TYPE_4BYTE_ABGR);
		Graphics g = colorBackground.getGraphics();
		ImageUtils.fillWithAlternatingColoredSquares(g, size, size);
	}

	@Override
	public void setColor(int index, Color color) {
		colorBoxes[index].setBackground(color);
	}

	public void setAndActivateController(ColorPaletteViewController controller) {
		this.controller = controller;
		controller.activate();
	}
}

package draw;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JColorChooser;
import javax.swing.JPanel;

public class ColorPalette extends JPanel implements ColorPaletteView {

	private JPanel[] colorBoxes;
	private ColorPaletteViewController controller;

	public ColorPalette() {
		setBorder(null);
		setLayout(new GridLayout(2, 10, 1, 1));
		this.setPreferredSize(new Dimension(309, 61));
		fillWithColorBoxes();
		setClickListenersOnBoxes();
	}

	private void fillWithColorBoxes() {
		colorBoxes = new JPanel[20];
		for (int i = 0; i < 20; i++) {
			colorBoxes[i] = new JPanel();
			this.add(colorBoxes[i]);
		}
	}

	private void setClickListenersOnBoxes() {
		for (int i = 0; i < 20; i++) {
			final int index = i;
			colorBoxes[i].addMouseListener(new MouseListener() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount() == 1) {
						if (e.getButton() == MouseEvent.BUTTON1)
							controller.selectForegroundColor(index);
						else if (e.getButton() == MouseEvent.BUTTON3)
							controller.selectBackgroundColor(index);
					} else if (e.getClickCount() == 2
							&& e.getButton() == MouseEvent.BUTTON1) {
						Color newColor = JColorChooser.showDialog(
								ColorPalette.this, "Select new color",
								colorBoxes[index].getBackground());
						if (newColor != null)
							controller.setPaletteEntry(index, newColor);
					}
				}

				@Override
				public void mouseEntered(MouseEvent arg0) {
				}

				@Override
				public void mouseExited(MouseEvent arg0) {
				}

				@Override
				public void mousePressed(MouseEvent arg0) {
				}

				@Override
				public void mouseReleased(MouseEvent arg0) {
				}
			});
		}
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

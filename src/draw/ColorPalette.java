package draw;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ColorPalette extends JPanel implements ColorPaletteView {

	private static final long serialVersionUID = 1L;
	private JPanel[] colorBoxes;
	private ColorPaletteViewController controller;
	private JColorChooser colorChooser = new JColorChooser();

	public ColorPalette() {
		setBorder(null);
		setLayout(new GridLayout(3, 10, 1, 1));
		this.setPreferredSize(new Dimension(309, 92));
		fillInNumberLabels();
		fillWithColorBoxes();
		setClickListenersOnBoxes();
	}

	private void fillInNumberLabels() {
		for (int i = 0; i < 10; i++)
			this.add(new JLabel("" + ((i + 1) % 10), JLabel.CENTER));
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
					if (isLeftClick(e))
						controller.selectForegroundColor(index);
					if (isRightClick(e))
						controller.selectBackgroundColor(index);
					if (isLeftOrRightDoubleClick(e)) {
						colorChooser.setColor(colorBoxes[index].getBackground());
						JColorChooser.createDialog(null, "Select new color",
								true, colorChooser,
								new ColorAcceptListener(index, e.getButton()),
								null).setVisible(true);
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

		public ColorAcceptListener(int index, int button) {
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

	@Override
	public void setColor(int index, Color color) {
		colorBoxes[index].setBackground(color);
	}

	public void setAndActivateController(ColorPaletteViewController controller) {
		this.controller = controller;
		controller.activate();
	}
}

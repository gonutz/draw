package draw;

import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

public class DrawArea extends JPanel implements DrawAreaView {

	private DrawAreaController controller;

	public void setController(DrawAreaController controller) {
		this.controller = controller;
	}

	@Override
	public void paint(Graphics graphics) {
		Graphics2D g = (Graphics2D) graphics;
		g.setBackground(getBackground());
		g.clearRect(0, 0, getWidth(), getHeight());
		g.drawImage(controller.getImage(), null, 0, 0);
	}

	@Override
	public void refresh() {
		repaint();
	}
}

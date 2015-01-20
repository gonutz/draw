package draw;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class DrawArea extends JPanel implements DrawAreaView {

	private static final long serialVersionUID = 1L;
	private DrawAreaController controller;

	public DrawArea() {
		initialize();
	}

	public void setController(DrawAreaController controller) {
		this.controller = controller;
	}

	@Override
	public void refresh() {
		setPreferredSize(new Dimension(controller.getImage().getWidth(),
				controller.getImage().getHeight()));
		repaint();
	}

	@Override
	public void paint(Graphics graphics) {
		Graphics2D g = (Graphics2D) graphics;
		g.setBackground(getBackground());
		g.clearRect(0, 0, getWidth(), getHeight());
		if (controller != null)
			g.drawImage(controller.getImage(), null, 0, 0);
	}

	private void initialize() {
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					controller.leftMouseButtonDown(e.getX(), e.getY());
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					controller.leftMouseButtonUp(e.getX(), e.getY());
				}
			}
		});
		addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				controller.mouseMovedTo(e.getX(), e.getY());
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				controller.mouseMovedTo(e.getX(), e.getY());
			}
		});
	}
}

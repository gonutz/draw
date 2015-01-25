package draw;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JPanel;

public class DrawArea extends JPanel implements DrawAreaView {

	private static final long serialVersionUID = 1L;
	private DrawAreaController controller;
	private Rectangle selection;

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
	public void setSelection(Rectangle selection) {
		this.selection = selection;
	}

	@Override
	public void paint(Graphics graphics) {
		Graphics2D g = (Graphics2D) graphics;
		clearBackground(g);
		drawImage(g);
		paintSelection(g);
	}

	private void clearBackground(Graphics2D g) {
		g.setBackground(getBackground());
		g.clearRect(0, 0, getWidth(), getHeight());
	}

	private void drawImage(Graphics2D g) {
		if (controller != null)
			g.drawImage(controller.getImage(), null, 0, 0);
	}

	private void paintSelection(Graphics2D g) {
		if (selection != null) {
			g.setColor(Color.black);
			g.setStroke(dashed);
			g.drawRect(selection.left(), selection.top(),
					selection.width() - 1, selection.height() - 1);
			g.setColor(new Color(0x100000FF, true));
			g.fillRect(selection.left() + 1, selection.top() + 1,
					selection.width() - 2, selection.height() - 2);
		}
	}

	private final float[] dash = { 2.0f };
	private final BasicStroke dashed = new BasicStroke(1.0f,
			BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 2.0f, dash, 0.0f);

	private void initialize() {
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					controller.leftMouseButtonDown(e.getX(), e.getY());
				}
				if (e.getButton() == MouseEvent.BUTTON3) {
					controller.rightMouseButtonDown(e.getX(), e.getY());
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					controller.leftMouseButtonUp();
				}
				if (e.getButton() == MouseEvent.BUTTON3) {
					controller.rightMouseButtonUp();
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

package draw;

import java.awt.AWTException;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Robot;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;

public class DrawArea extends JPanel implements DrawAreaView, Scrollable,
		MouseWheelListener {

	private static final int MAX_ZOOM_FACTOR = 32;
	private static final long serialVersionUID = 1L;
	private static final int BORDER_WIDTH = 1;
	private DrawAreaController controller;
	private PositionView positionView;
	private ZoomView zoomView;
	private Rectangle selection;
	private int zoomFactor = 1;

	public DrawArea() {
		initialize();
	}

	public void setController(DrawAreaController controller) {
		this.controller = controller;
	}

	public void setPositionView(PositionView positionView) {
		this.positionView = positionView;
	}

	public void setZoomView(ZoomView zoomView) {
		this.zoomView = zoomView;
	}

	/**
	 * Adjusts the zoom so it is the biggest possible zoom (and multiple of two,
	 * like in the other zoom functions) that fits inside the parent container.
	 */
	public void zoomToFit() {
		BufferedImage image = controller.getImage();
		int width = getParent().getWidth();
		int height = getParent().getHeight();
		int zoom = 2;
		while (image.getWidth() * zoom < width
				&& image.getHeight() * zoom < height && zoom <= MAX_ZOOM_FACTOR)
			zoom *= 2;
		zoom /= 2;
		while (zoomFactor < zoom)
			zoomIn();
		while (zoomFactor > zoom)
			zoomOut();
	}

	public void zoomIn() {
		java.awt.Rectangle visible = (java.awt.Rectangle) getVisibleRect()
				.clone();
		int centerX = visible.x + visible.width / 2;
		int centerY = visible.y + visible.height / 2;
		zoomInAt(visible, centerX, centerY);
	}

	public void zoomInAt(java.awt.Rectangle visible, int panelX, int panelY) {
		if (zoomFactor < MAX_ZOOM_FACTOR) {
			int newX = 2 * panelX;
			int newY = 2 * panelY;
			visible.x = newX - (panelX - visible.x);
			visible.y = newY - (panelY - visible.y);
			setZoomFactor(zoomFactor * 2);
			refresh();
			scrollRectToVisible(visible);
			moveMouseTo(newX, newY);
		}
	}

	private void moveMouseTo(int panelOffsetX, int panelOffsetY) {
		// TODO have robot created once at program start
		Robot robot = null;
		try {
			robot = new Robot();
		} catch (AWTException e1) {
		}

		if (robot != null) {
			java.awt.Point p = this.getLocationOnScreen();
			robot.mouseMove(p.x + panelOffsetX, p.y + panelOffsetY);
		}
	}

	private void setZoomFactor(int zoom) {
		this.zoomFactor = zoom;
		zoomView.setZoomFactor(zoom);
	}

	public void zoomOut() {
		java.awt.Rectangle visible = (java.awt.Rectangle) getVisibleRect()
				.clone();
		int centerX = visible.x + visible.width / 2;
		int centerY = visible.y + visible.height / 2;
		zoomOutAt(visible, centerX, centerY);
	}

	public void zoomOutAt(java.awt.Rectangle visible, int panelX, int panelY) {
		if (zoomFactor > 1) {
			int newX = panelX / 2;
			int newY = panelY / 2;
			visible.x = newX - (panelX - visible.x);
			visible.y = newY - (panelY - visible.y);
			scrollRectToVisible(visible);
			setZoomFactor(zoomFactor / 2);
			refresh();
			moveMouseTo(newX, newY);
		}
	}

	@Override
	public void refresh() {
		BufferedImage img = controller.getImage();
		setPreferredSize(new Dimension(img.getWidth() * zoomFactor
				+ BORDER_WIDTH, img.getHeight() * zoomFactor + BORDER_WIDTH));
		revalidate();
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
		drawImageBorder(g);
		paintSelection(g);
	}

	private void clearBackground(Graphics2D g) {
		g.setBackground(getBackground());
		g.clearRect(0, 0, getWidth(), getHeight());
	}

	private void drawImage(Graphics2D g) {
		if (controller != null) {
			BufferedImage img = controller.getImage();
			g.drawImage(img, 0, 0, img.getWidth() * zoomFactor, img.getHeight()
					* zoomFactor, null);
		}
	}

	private void drawImageBorder(Graphics2D g) {
		if (controller != null) {
			BufferedImage img = controller.getImage();
			int x = img.getWidth() * zoomFactor;
			int y = img.getHeight() * zoomFactor;
			g.setColor(Color.black);
			g.drawLine(x, 0, x, y);
			g.drawLine(0, y, x, y);
		}
	}

	private void paintSelection(Graphics2D g) {
		if (selection != null) {
			g.setColor(Color.black);
			g.setStroke(dashed);
			g.drawRect(selection.left() * zoomFactor, selection.top()
					* zoomFactor, selection.width() * zoomFactor - 1,
					selection.height() * zoomFactor - 1);
			g.setColor(new Color(0x200000FF, true));
			g.fillRect(selection.left() * zoomFactor + 1, selection.top()
					* zoomFactor + 1, selection.width() * zoomFactor - 2,
					selection.height() * zoomFactor - 2);
		}
	}

	private final float[] dash = { 2.0f };
	private final BasicStroke dashed = new BasicStroke(1.0f,
			BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 2.0f, dash, 0.0f);

	private void initialize() {
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				int x = e.getX() / zoomFactor;
				int y = e.getY() / zoomFactor;
				if (e.getButton() == MouseEvent.BUTTON1) {
					controller.leftMouseButtonDown(x, y);
				}
				if (e.getButton() == MouseEvent.BUTTON3) {
					controller.rightMouseButtonDown(x, y);
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

			@Override
			public void mouseExited(MouseEvent e) {
				positionView.setNoPosition();
			}
		});
		addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				moveMouseTo(e);
			}

			private void moveMouseTo(MouseEvent e) {
				int x = e.getX() / zoomFactor;
				int y = e.getY() / zoomFactor;
				controller.mouseMovedTo(x, y);
				positionView.setPosition(x, y);
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				moveMouseTo(e);
			}
		});
		addMouseWheelListener(this);
	}

	@Override
	public Dimension getPreferredScrollableViewportSize() {
		if (controller == null)
			return null;
		return new Dimension(controller.getImage().getWidth() * zoomFactor,
				controller.getImage().getHeight() * zoomFactor);
	}

	@Override
	public int getScrollableUnitIncrement(java.awt.Rectangle visibleRect,
			int orientation, int direction) {
		if (orientation == SwingConstants.HORIZONTAL)
			return visibleRect.width / 5;
		else
			return visibleRect.height / 5;
	}

	@Override
	public int getScrollableBlockIncrement(java.awt.Rectangle visibleRect,
			int orientation, int direction) {
		if (orientation == SwingConstants.HORIZONTAL)
			return visibleRect.width - 2 * zoomFactor;
		else
			return visibleRect.height - 2 * zoomFactor;
	}

	@Override
	public boolean getScrollableTracksViewportHeight() {
		return false;
	}

	@Override
	public boolean getScrollableTracksViewportWidth() {
		return false;
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		java.awt.Rectangle visible = (java.awt.Rectangle) getVisibleRect()
				.clone();
		int dx = visible.width / 5;
		int dy = visible.height / 5;
		if (e.isControlDown()) {
			if (e.getWheelRotation() < 0)
				zoomInAt(visible, e.getX(), e.getY());
			if (e.getWheelRotation() > 0)
				zoomOutAt(visible, e.getX(), e.getY());
			positionView.setNoPosition();
		} else if (e.isShiftDown() || e.isAltDown()) {
			if (e.getWheelRotation() < 0)
				visible.x -= dx;
			if (e.getWheelRotation() > 0)
				visible.x += dx;
			scrollRectToVisible(visible);
		} else {
			if (e.getWheelRotation() < 0)
				visible.y -= dy;
			if (e.getWheelRotation() > 0)
				visible.y += dy;
			scrollRectToVisible(visible);
		}
	}

	@Override
	public Point getVisibleTopLeftCorner() {
		java.awt.Rectangle v = getVisibleRect();
		Point p = new Point();
		p.x = (v.x + zoomFactor - 1) / zoomFactor;
		p.y = (v.y + zoomFactor - 1) / zoomFactor;
		return p;
	}

}

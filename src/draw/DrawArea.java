package draw;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
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

	private static final long serialVersionUID = 1L;
	private static final int BORDER_WIDTH = 1;
	private DrawAreaController controller;
	private PositionView positionView;
	private ZoomView zoomView;
	private Rectangle selection;
	private int zoomFactor = 1;
	private int mouseZoomX, mouseZoomY;

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

	public void zoomIn() {
		java.awt.Rectangle visible = (java.awt.Rectangle) getVisibleRect()
				.clone();
		int centerX = visible.x + visible.width / 2;
		int centerY = visible.y + visible.height / 2;
		zoomIn(visible, centerX, centerY);
	}

	public void zoomIn(java.awt.Rectangle visible, int x, int y) {
		if (zoomFactor < 32) {
			visible.x = 2 * x - (x - visible.x);
			visible.y = 2 * y - (y - visible.y);
			setZoomFactor(zoomFactor * 2);
			refresh();
			scrollRectToVisible(visible);
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
		zoomOut(visible, centerX, centerY);
	}

	public void zoomOut(java.awt.Rectangle visible, int x, int y) {
		if (zoomFactor > 1) {
			visible.x = x / 2 - (x - visible.x);
			visible.y = y / 2 - (y - visible.y);
			scrollRectToVisible(visible);
			setZoomFactor(zoomFactor / 2);
			refresh();
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
				mouseZoomX = e.getX() / zoomFactor;
				mouseZoomY = e.getY() / zoomFactor;
				if (e.getButton() == MouseEvent.BUTTON1) {
					controller.leftMouseButtonDown(mouseZoomX, mouseZoomY);
				}
				if (e.getButton() == MouseEvent.BUTTON3) {
					controller.rightMouseButtonDown(mouseZoomX, mouseZoomY);
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
				mouseZoomX = e.getX() / zoomFactor;
				mouseZoomY = e.getY() / zoomFactor;
				controller.mouseMovedTo(mouseZoomX, mouseZoomY);
				positionView.setPosition(mouseZoomX, mouseZoomY);
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				mouseZoomX = e.getX() / zoomFactor;
				mouseZoomY = e.getY() / zoomFactor;
				controller.mouseMovedTo(mouseZoomX, mouseZoomY);
				positionView.setPosition(mouseZoomX, mouseZoomY);
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
				zoomIn(visible, mouseZoomX * zoomFactor, mouseZoomY
						* zoomFactor);
			if (e.getWheelRotation() > 0)
				zoomOut(visible, mouseZoomX * zoomFactor, mouseZoomY
						* zoomFactor);
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

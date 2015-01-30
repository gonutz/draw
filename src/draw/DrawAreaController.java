package draw;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class DrawAreaController implements ImageProvider, ImageKeeper,
		SelectionKeeper, ToolChangeObserver {

	private DrawAreaView view;
	private DrawSettings drawSettings;
	private BufferedImage image;
	private UndoHistory history = new UndoHistory();
	private Mouse lastMouse = new Mouse();
	private Selection selection = new Selection();
	private Pen pen = new Pen();

	private class Mouse {
		int x, y;
	}

	private class Selection {
		boolean selecting;
		boolean moving;
		Rectangle rect;
		SelectionMovement movement;
	}

	private class Pen {
		boolean down;
		PenStroke stroke;
		Color color;
	}

	public DrawAreaController(DrawAreaView view) {
		this.view = view;
	}

	public void setDrawSettings(DrawSettings drawSettings) {
		this.drawSettings = drawSettings;
	}

	public BufferedImage getImage() {
		return image;
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}

	public void undoLastAction() {
		if (history.undoTo(this))
			view.refresh();
	}

	public void redoPreviousAction() {
		if (history.redoTo(this))
			view.refresh();
	}

	public void newImage(int width, int height) {
		NewImageCommand newImageCommand = new NewImageCommand(image, width,
				height, drawSettings.getBackgroundColor());
		makeUndoableIfThisIsNotTheVeryFirstImage(newImageCommand);
		newImageCommand.doTo(this);
		view.refresh();
	}

	private void makeUndoableIfThisIsNotTheVeryFirstImage(NewImageCommand c) {
		if (image != null) {
			history.addCommand(c);
		}
	}

	private static final int LEFT_BUTTON = 1;
	private static final int RIGHT_BUTTON = 2;

	public void leftMouseButtonDown(int x, int y) {
		mouseDown(x, y, LEFT_BUTTON);
	}

	public void rightMouseButtonDown(int x, int y) {
		mouseDown(x, y, RIGHT_BUTTON);
	}

	private void mouseDown(int x, int y, int button) {
		Tool tool = drawSettings.getCurrentTool();
		if (tool == Tool.Pen)
			mouseDownWithPenSelected(x, y, button);
		if (tool == Tool.RectangleSelection && button == LEFT_BUTTON) {
			mouseDownWithRectangleSelectionTool(x, y);
		}
		lastMouse.x = x;
		lastMouse.y = y;
	}

	private void mouseDownWithPenSelected(int x, int y, int button) {
		selectDrawColorForButton(button);
		if (pen.down)
			undoCurrentStroke();
		else
			startNewPenStroke(x, y);
		view.refresh();
	}

	private void selectDrawColorForButton(int button) {
		switch (button) {
		case LEFT_BUTTON:
			pen.color = drawSettings.getForegroundColor();
			break;
		case RIGHT_BUTTON:
			pen.color = drawSettings.getBackgroundColor();
			break;
		}
	}

	private void undoCurrentStroke() {
		pen.stroke.undoTo(this);
		pen.down = false;
	}

	private void startNewPenStroke(int x, int y) {
		pen.stroke = new PenStroke(pen.color);
		pen.stroke.addLine(image, x, y, x, y);
		pen.down = true;
	}

	private void mouseDownWithRectangleSelectionTool(int x, int y) {
		if (!isInSelection(x, y)) {
			if (isInsideImage(x, y)) {
				selection.selecting = true;
				selection.rect = new Rectangle(x, y, x, y);
			}
			selection.movement = null;
		} else
			startMovingSelection();
	}

	private boolean isInSelection(int x, int y) {
		if (selection.rect == null)
			return false;
		return selection.rect.contains(x, y);
	}

	private boolean isInsideImage(int x, int y) {
		return x >= 0 && x < image.getWidth() && y >= 0
				&& y < image.getHeight();
	}

	private void startMovingSelection() {
		selection.moving = true;
		if (selection.movement == null) {
			selection.movement = new SelectionMovement(image, selection.rect,
					drawSettings.getBackgroundColor(), this);
			history.addCommand(selection.movement);
		}
	}

	public void leftMouseButtonUp() {
		mouseUp();
	}

	public void rightMouseButtonUp() {
		mouseUp();
	}

	private void mouseUp() {
		if (pen.down)
			history.addCommand(pen.stroke);
		if (selection.selecting && selection.rect.x == lastMouse.x
				&& selection.rect.y == lastMouse.y) {
			selection.movement = null;
			updateSelection(null);
		}
		selection.selecting = false;
		selection.moving = false;
		pen.down = false;
	}

	public void setSelection(Rectangle rect) {
		selection.rect = rect;
		view.setSelection(rect);
	}

	private void updateSelection(Rectangle rect) {
		setSelection(rect);
		view.refresh();
	}

	public void mouseMovedTo(int x, int y) {
		if (pen.down) {
			pen.stroke.addLine(image, lastMouse.x, lastMouse.y, x, y);
			view.refresh();
		}
		if (selection.selecting) {
			selection.rect.x2 = clampToImageX(x);
			selection.rect.y2 = clampToImageY(y);
			updateSelection(selection.rect);
		}
		if (selection.moving) {
			int dx = x - lastMouse.x;
			int dy = y - lastMouse.y;
			selection.movement.moveBy(dx, dy);
			selection.movement.drawCompositeTo(image.getGraphics());
			updateSelection(selection.rect);
		}
		lastMouse.x = x;
		lastMouse.y = y;
	}

	private int clampToImageY(int y) {
		return Math.min(Math.max(y, 0), image.getHeight() - 1);
	}

	private int clampToImageX(int x) {
		return Math.min(Math.max(x, 0), image.getWidth() - 1);
	}

	@Override
	public void toolChangedTo(Tool tool) {
		updateSelection(null);
	}
}
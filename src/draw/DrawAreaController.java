package draw;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class DrawAreaController implements ImageProvider, ImageKeeper,
		SelectionKeeper {

	private DrawAreaView view;
	private DrawSettings drawSettings;
	private BufferedImage image;
	private Color drawColor;
	private int lastX;
	private int lastY;
	private boolean penDown;
	private UndoHistory history = new UndoHistory();
	private PenStroke currentStroke;
	private boolean makingSelection;
	private Rectangle selection;
	private boolean movingSelection;
	private SelectionMovement movement;

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
		lastX = x;
		lastY = y;
	}

	private void mouseDownWithPenSelected(int x, int y, int button) {
		selectDrawColorForButton(button);
		if (penDown)
			undoCurrentStroke();
		else
			startNewPenStroke(x, y);
		view.refresh();
	}

	private void selectDrawColorForButton(int button) {
		switch (button) {
		case LEFT_BUTTON:
			drawColor = drawSettings.getForegroundColor();
			break;
		case RIGHT_BUTTON:
			drawColor = drawSettings.getBackgroundColor();
			break;
		}
	}

	private void undoCurrentStroke() {
		currentStroke.undoTo(this);
		penDown = false;
	}

	private void startNewPenStroke(int x, int y) {
		currentStroke = new PenStroke(drawColor);
		currentStroke.addLine(image, x, y, x, y);
		penDown = true;
	}

	private void mouseDownWithRectangleSelectionTool(int x, int y) {
		if (!isInSelection(x, y)) {
			if (isInsideImage(x, y)) {
				makingSelection = true;
				selection = new Rectangle(x, y, x, y);
			}
			movement = null;
		} else
			startMovingSelection();
	}

	private boolean isInSelection(int x, int y) {
		if (selection == null)
			return false;
		return selection.contains(x, y);
	}

	private boolean isInsideImage(int x, int y) {
		return x >= 0 && x < image.getWidth() && y >= 0
				&& y < image.getHeight();
	}

	private void startMovingSelection() {
		movingSelection = true;
		if (movement == null) {
			movement = new SelectionMovement(image, selection,
					drawSettings.getBackgroundColor(), this);
			history.addCommand(movement);
		}
	}

	public void leftMouseButtonUp() {
		mouseUp();
	}

	public void rightMouseButtonUp() {
		mouseUp();
	}

	private void mouseUp() {
		if (penDown)
			history.addCommand(currentStroke);
		if (makingSelection && selection.x == lastX && selection.y == lastY) {
			movement = null;
			updateSelection(null);
		}
		makingSelection = false;
		movingSelection = false;
		penDown = false;
	}

	public void setSelection(Rectangle selection) {
		this.selection = selection;
		view.setSelection(selection);
	}

	private void updateSelection(Rectangle selection) {
		this.selection = selection;
		view.setSelection(selection);
		view.refresh();
	}

	public void mouseMovedTo(int x, int y) {
		if (penDown) {
			currentStroke.addLine(image, lastX, lastY, x, y);
			view.refresh();
		}
		if (makingSelection) {
			selection.x2 = clampToImageX(x);
			selection.y2 = clampToImageY(y);
			updateSelection(selection);
		}
		if (movingSelection) {
			int dx = x - lastX;
			int dy = y - lastY;
			movement.moveBy(dx, dy);
			movement.drawCompositeTo(image.getGraphics());
			updateSelection(selection);
		}
		lastX = x;
		lastY = y;
	}

	private int clampToImageY(int y) {
		return Math.min(Math.max(y, 0), image.getHeight() - 1);
	}

	private int clampToImageX(int x) {
		return Math.min(Math.max(x, 0), image.getWidth() - 1);
	}

}
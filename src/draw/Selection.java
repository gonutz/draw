package draw;

import java.awt.Color;
import java.awt.image.BufferedImage;

import draw.commands.SelectionMoveCommand;
import draw.commands.UndoHistory;

public class Selection {

	private Rectangle rect;
	private SelectionMoveCommand movement;
	private boolean movementAddedToHistory;
	private UndoHistory history;

	public Selection(UndoHistory history) {
		this.history = history;
	}

	public boolean contains(int x, int y) {
		if (rect == null)
			return false;
		return rect.contains(x, y);
	}

	public void startMovingWithMouse(BufferedImage image,
			Color backgroundColor, DrawAreaController controller) {
		if (movement == null) {
			movement = new SelectionMoveCommand(image, rect, backgroundColor,
					controller);
			movementAddedToHistory = false;
		}
	}

	public void stopMovement() {
		movement = null;
	}

	public void startSelectionAt(int x, int y) {
		rect = new Rectangle(x, y, x, y);
	}

	public boolean isActive() {
		return rect != null;
	}

	public void moveBy(int dx, int dy) {
		movement.moveBy(dx, dy);
		if (!movementAddedToHistory) {
			history.addCommand(movement);
			movementAddedToHistory = true;
		}
	}

	public Rectangle getRect() {
		return rect;
	}

	public void setRect(Rectangle rect) {
		this.rect = rect;
	}

	public SelectionMoveCommand getMovement() {
		return movement;
	}

	public void setMovement(SelectionMoveCommand movement) {
		this.movement = movement;
	}
}

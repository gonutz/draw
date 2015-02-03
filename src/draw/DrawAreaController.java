package draw;

import java.awt.Color;
import java.awt.image.BufferedImage;

import draw.commands.NewImageCommand;
import draw.commands.Stroke;
import draw.commands.SelectionMovement;

public class DrawAreaController implements ImageProvider, ImageKeeper,
		SelectionKeeper, ToolChangeObserver, ImageDisplay {

	private DrawAreaView view;
	private DrawSettings drawSettings;
	private ToolController toolController;
	private BufferedImage image;
	private UndoHistory history = new UndoHistory();
	private Mouse lastMouse = new Mouse();
	private Selection selection = new Selection();
	private Pen pen = new Pen();
	private State state = State.Idle;
	private boolean refreshed;
	private Line line = new Line();

	private class Line {
		int startX, startY;
		Stroke stroke;
	}

	private enum State {
		Idle, PenDown, Selecting, MovingSelection, DrawingLine
	}

	private class Mouse {
		static final int LeftButton = 1;
		static final int RightButton = 2;
		int x, y;

		public void setCursor(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}

	private class Selection {
		Rectangle rect;
		SelectionMovement movement;

		public boolean contains(int x, int y) {
			if (rect == null)
				return false;
			return rect.contains(x, y);
		}

		public void startMoving() {
			state = State.MovingSelection;
			if (movement == null) {
				movement = new SelectionMovement(image, rect,
						drawSettings.getBackgroundColor(),
						DrawAreaController.this);
				history.addCommand(selection.movement);
			}
		}

		public void stopMovement() {
			movement = null;
		}

		public void startSelectionAt(int x, int y) {
			state = State.Selecting;
			rect = new Rectangle(x, y, x, y);
		}
	}

	private class Pen {
		Stroke stroke;
		Color color;
	}

	public DrawAreaController(DrawAreaView view) {
		this.view = view;
	}

	public void setDrawSettings(DrawSettings drawSettings) {
		this.drawSettings = drawSettings;
	}

	public void setToolController(ToolController toolController) {
		this.toolController = toolController;
	}

	public BufferedImage getImage() {
		return image;
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}

	public void undoLastAction() {
		refreshed = false;
		if (history.undoTo(this, toolController) && !refreshed)
			view.refresh();
		selection.movement = null;
	}

	public void redoPreviousAction() {
		refreshed = false;
		if (history.redoTo(this, toolController) && !refreshed)
			view.refresh();
	}

	public void newImage(int width, int height) {
		NewImageCommand newImageCommand = new NewImageCommand(image, width,
				height, drawSettings.getBackgroundColor());
		makeUndoableIfThisIsNotTheVeryFirstImage(newImageCommand);
		newImageCommand.doTo(this, toolController);
		setSelection(null);
		view.refresh();
	}

	private void makeUndoableIfThisIsNotTheVeryFirstImage(NewImageCommand c) {
		if (image != null) {
			history.addCommand(c);
		}
	}

	public void leftMouseButtonDown(int x, int y) {
		mouseDown(x, y, Mouse.LeftButton);
	}

	public void rightMouseButtonDown(int x, int y) {
		mouseDown(x, y, Mouse.RightButton);
	}

	private void mouseDown(int x, int y, int button) {
		Tool tool = toolController.getSelectedTool();
		if (tool == Tool.Pen)
			mouseDownWithPenSelected(x, y, button);
		if (tool == Tool.Line)
			mouseDownWithLineSelected(x, y, button);
		if (tool == Tool.RectangleSelection && button == Mouse.LeftButton)
			mouseDownWithRectangleSelectionTool(x, y);
		lastMouse.setCursor(x, y);
	}

	private void mouseDownWithPenSelected(int x, int y, int button) {
		pen.color = getDrawColorForMouseButton(button);
		if (state == State.PenDown) // pressing other mouse key undoes stroke
			undoCurrentPenStroke();
		else
			startNewPenStroke(x, y);
		view.refresh();
	}

	private Color getDrawColorForMouseButton(int button) {
		if (button == Mouse.LeftButton)
			return drawSettings.getForegroundColor();
		return drawSettings.getBackgroundColor();
	}

	private void undoCurrentPenStroke() {
		pen.stroke.undoTo(this, toolController);
		state = State.Idle;
	}

	private void startNewPenStroke(int x, int y) {
		pen.stroke = new Stroke(Tool.Pen, pen.color);
		pen.stroke.addLine(image, x, y, x, y);
		state = State.PenDown;
	}

	private void mouseDownWithLineSelected(int x, int y, int button) {
		if (state == State.DrawingLine)
			undoCurrentLineStroke();
		else
			startLineStroke(x, y, button);
		view.refresh();
	}

	private void undoCurrentLineStroke() {
		line.stroke.undoTo(this, toolController);
		state = State.Idle;
	}

	private void startLineStroke(int x, int y, int button) {
		line.startX = x;
		line.startY = y;
		line.stroke = new Stroke(Tool.Line, getDrawColorForMouseButton(button));
		line.stroke.addLine(image, x, y, x, y);
		state = State.DrawingLine;
	}

	private void mouseDownWithRectangleSelectionTool(int x, int y) {
		if (selection.contains(x, y))
			selection.startMoving();
		else {
			selection.stopMovement();
			if (isInsideImage(x, y))
				selection.startSelectionAt(x, y);
		}
	}

	private boolean isInsideImage(int x, int y) {
		return x >= 0 && x < image.getWidth() && y >= 0
				&& y < image.getHeight();
	}

	public void leftMouseButtonUp() {
		mouseUp();
	}

	public void rightMouseButtonUp() {
		mouseUp();
	}

	private void mouseUp() {
		if (state == State.PenDown)
			history.addCommand(pen.stroke);
		if (state == State.Selecting && selection.rect.x == lastMouse.x
				&& selection.rect.y == lastMouse.y) {
			selection.stopMovement();
			updateSelection(null);
		}
		if (state == State.DrawingLine)
			history.addCommand(line.stroke);
		state = State.Idle;
	}

	public void setSelection(Rectangle rect) {
		selection.rect = rect;
		view.setSelection(rect);
	}

	private void updateSelection(Rectangle rect) {
		setSelection(rect);
		view.refresh();
		refreshed = true;
	}

	public void mouseMovedTo(int x, int y) {
		switch (state) {
		case Idle:
			break;
		case PenDown:
			pen.stroke.addLine(image, lastMouse.x, lastMouse.y, x, y);
			view.refresh();
			break;
		case Selecting:
			selection.rect.x2 = clampToImageX(x);
			selection.rect.y2 = clampToImageY(y);
			updateSelection(selection.rect);
			break;
		case MovingSelection:
			int dx = x - lastMouse.x;
			int dy = y - lastMouse.y;
			selection.movement.moveBy(dx, dy);
			selection.movement.drawCompositeTo(image.getGraphics());
			updateSelection(selection.rect);
			break;
		case DrawingLine:
			line.stroke.undoTo(this, toolController);
			line.stroke.setLine(image, line.startX, line.startY, x, y);
			view.refresh();
			break;
		}
		lastMouse.setCursor(x, y);
	}

	private int clampToImageY(int y) {
		return Math.min(Math.max(y, 0), image.getHeight() - 1);
	}

	private int clampToImageX(int x) {
		return Math.min(Math.max(x, 0), image.getWidth() - 1);
	}

	@Override
	public void toolChangedTo(Tool tool) {
		if (selection.rect != null)
			updateSelection(null);
	}

	@Override
	public void showLoadedImage(BufferedImage loaded) {
		image = ImageUtils.copyImage(loaded);
		view.refresh();
	}
}
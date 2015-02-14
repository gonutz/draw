package draw;

import java.awt.Color;
import java.awt.image.BufferedImage;

import draw.DrawAreaView.Point;
import draw.commands.ImageDisplayCommand;
import draw.commands.NewImageCommand;
import draw.commands.SelectionMovement;
import draw.commands.Stroke;
import draw.commands.UndoHistory;
import draw.commands.UndoableCommand;

public class DrawAreaController implements ImageProvider, ImageKeeper,
		SelectionKeeper, ToolChangeObserver, ImageDisplay {

	private DrawAreaView view;
	private DrawSettings drawSettings;
	private ToolController toolController;
	private Clipboard clipboard;
	private BufferedImage image;
	private UndoHistory history = new UndoHistory();
	private Mouse lastMouse = new Mouse();
	private Selection selection = new Selection();
	private Pen pen = new Pen();
	private State state = State.Idle;
	private Line line = new Line();
	private boolean viewDirty;
	private boolean updatingTool;

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
		private Rectangle rect;
		private SelectionMovement movement;
		private boolean movementAddedToHistory;

		public boolean contains(int x, int y) {
			if (rect == null)
				return false;
			return rect.contains(x, y);
		}

		public void startMovingWithMouse() {
			state = State.MovingSelection;
			createNewMovementIfNecessary();
		}

		private void createNewMovementIfNecessary() {
			if (movement == null) {
				movement = new SelectionMovement(image, rect,
						drawSettings.getBackgroundColor(),
						DrawAreaController.this);
				movementAddedToHistory = false;
			}
		}

		public void stopMovement() {
			movement = null;
		}

		public void startSelectionAt(int x, int y) {
			state = State.Selecting;
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

	public void setClipboard(Clipboard clipboard) {
		this.clipboard = clipboard;
	}

	public BufferedImage getImage() {
		return image;
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}

	public void undoLastAction() {
		setSelection(null);
		if (history.undoTo(this, toolController))
			view.refresh();
		selection.stopMovement();
	}

	public void redoPreviousAction() {
		updatingTool = true;
		if (history.redoTo(this, toolController))
			view.refresh();
		updatingTool = false;
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
		refreshViewIfNecessary();
	}

	private void mouseDownWithPenSelected(int x, int y, int button) {
		pen.color = getDrawColorForMouseButton(button);
		if (state == State.PenDown) // pressing other mouse key undoes stroke
			undoCurrentPenStroke();
		else
			startNewPenStroke(x, y);
		setViewDirty();
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
		setViewDirty();
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
			selection.startMovingWithMouse();
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

	private void setViewDirty() {
		viewDirty = true;
	}

	private void refreshViewIfNecessary() {
		if (viewDirty)
			view.refresh();
		viewDirty = false;
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
		refreshViewIfNecessary();
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
		switch (state) {
		case Idle:
			break;
		case PenDown:
			pen.stroke.addLine(image, lastMouse.x, lastMouse.y, x, y);
			setViewDirty();
			break;
		case Selecting:
			selection.rect.x2 = clampToImageX(x);
			selection.rect.y2 = clampToImageY(y);
			updateSelection(selection.rect);
			break;
		case MovingSelection:
			int dx = x - lastMouse.x;
			int dy = y - lastMouse.y;
			selection.moveBy(dx, dy);
			selection.movement.drawCompositeTo(image.getGraphics());
			updateSelection(selection.rect);
			break;
		case DrawingLine:
			line.stroke.undoTo(this, toolController);
			line.stroke.setLine(image, line.startX, line.startY, x, y);
			setViewDirty();
			break;
		}
		lastMouse.setCursor(x, y);
		refreshViewIfNecessary();
	}

	private int clampToImageY(int y) {
		return Math.min(Math.max(y, 0), image.getHeight() - 1);
	}

	private int clampToImageX(int x) {
		return Math.min(Math.max(x, 0), image.getWidth() - 1);
	}

	@Override
	public void toolChangedTo(Tool tool) {
		if (selection.isActive()) {
			if (updatingTool)
				setSelection(null);
			else
				updateSelection(null);
		}
	}

	@Override
	public void showLoadedImage(BufferedImage loaded) {
		history.addCommand(new ImageDisplayCommand(image, loaded));
		image = loaded;
		setSelection(null);
		view.refresh();
	}

	public void copy() {
		if (selection.isActive())
			clipboard.storeImage(image.getSubimage(selection.rect.left(),
					selection.rect.top(), selection.rect.width(),
					selection.rect.height()));
	}

	public void paste() {
		BufferedImage toPaste = clipboard.getImage();
		if (toPaste != null) {
			Point corner = view.getVisibleTopLeftCorner();
			int x = corner.x;
			int y = corner.y;
			int w = toPaste.getWidth();
			int h = toPaste.getHeight();
			Rectangle newSelection = new Rectangle(x, y, x + w - 1, y + h - 1);
			PasteCommand paste = new PasteCommand(toPaste, newSelection);
			paste.doTo(this, toolController);
			history.addCommand(paste);
			view.refresh();
		}
	}

	private class PasteCommand implements UndoableCommand {
		BufferedImage background;
		private BufferedImage toPaste;
		private Rectangle newSelection;

		public PasteCommand(BufferedImage toPaste, Rectangle selection) {
			background = ImageUtils.copyImage(image);
			this.toPaste = toPaste;
			this.newSelection = selection;
		}

		@Override
		public void undoTo(ImageKeeper keeper, ToolController toolController) {
			keeper.setImage(ImageUtils.copyImage(background));
		}

		@Override
		public void doTo(ImageKeeper keeper, ToolController toolController) {
			selection.movement = new SelectionMovement(image, newSelection,
					toPaste, DrawAreaController.this);
			updatingTool = true;
			selection.movement.doTo(DrawAreaController.this, toolController);
			updatingTool = false;
			setSelection(newSelection);
		}
	}

	public void escape() {
		if (selection.isActive())
			updateSelection(null);
	}

	public void delete() {
		// TODO Auto-generated method stub

	}

	public void move(int dx, int dy) {
		if (selection.isActive()) {
			selection.createNewMovementIfNecessary();
			selection.moveBy(dx, dy);
			selection.movement.drawCompositeTo(image.getGraphics());
			updateSelection(selection.rect);
		}
	}

	public void selectAll() {
		toolController.selectTool(Tool.RectangleSelection);
		updateSelection(new Rectangle(0, 0, image.getWidth() - 1,
				image.getHeight() - 1));
	}

}
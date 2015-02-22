package draw;

import java.awt.Color;
import java.awt.image.BufferedImage;

import draw.DrawAreaView.Point;
import draw.commands.DeleteSelectionCommand;
import draw.commands.FillCommand;
import draw.commands.ImageDisplayCommand;
import draw.commands.MirrorHorizontallyCommand;
import draw.commands.MirrorVerticallyCommand;
import draw.commands.NewImageCommand;
import draw.commands.ResizeCommand;
import draw.commands.SelectionMoveCommand;
import draw.commands.StrokeCommand;
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
	private Mouse lastMousePosition = new Mouse();
	private Selection selection = new Selection();
	private Pen pen = new Pen();
	private State state = State.Idle;
	private Line line = new Line();
	private boolean viewDirty;
	private boolean updatingTool;
	private Tool lastNonColorPickerTool;

	private class Line {
		int startX, startY;
		StrokeCommand stroke;
	}

	private enum State {
		Idle, PenDown, Selecting, MovingSelection, DrawingLine
	}

	private class Mouse {
		static final int LeftButton = 1;
		static final int RightButton = 2;
		int x, y;

		public void set(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}

	private class Selection {
		private Rectangle rect;
		private SelectionMoveCommand movement;
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
				movement = new SelectionMoveCommand(image, rect,
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
		StrokeCommand stroke;
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
		if (tool == Tool.Fill)
			fillWith(x, y, getDrawColorForMouseButton(button));
		if (tool == Tool.ColorPicker)
			pickColor(x, y, button);
		lastMousePosition.set(x, y);
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
		pen.stroke = new StrokeCommand(Tool.Pen, pen.color);
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
		line.stroke = new StrokeCommand(Tool.Line,
				getDrawColorForMouseButton(button));
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

	private void fillWith(int x, int y, Color color) {
		FillCommand fill = new FillCommand(x, y, color);
		fill.doTo(this, toolController);
		history.addCommand(fill);
		view.refresh();
	}

	private void pickColor(int x, int y, int mouseButton) {
		Color color = new Color(image.getRGB(x, y), true);
		if (mouseButton == Mouse.LeftButton)
			drawSettings.setForegroundColor(color);
		else
			drawSettings.setBackgroundColor(color);
		toolController.selectTool(lastNonColorPickerTool);
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
		if (state == State.Selecting && selection.rect.x == lastMousePosition.x
				&& selection.rect.y == lastMousePosition.y) {
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
			pen.stroke.addLine(image, lastMousePosition.x, lastMousePosition.y,
					x, y);
			setViewDirty();
			break;
		case Selecting:
			selection.rect.x2 = clampToImageX(x);
			selection.rect.y2 = clampToImageY(y);
			updateSelection(selection.rect);
			break;
		case MovingSelection:
			int dx = x - lastMousePosition.x;
			int dy = y - lastMousePosition.y;
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
		lastMousePosition.set(x, y);
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
		if (tool != Tool.ColorPicker)
			lastNonColorPickerTool = tool;
	}

	@Override
	public void showLoadedImage(BufferedImage loaded) {
		history.addCommand(new ImageDisplayCommand(image, loaded));
		image = loaded;
		setSelection(null);
		view.refresh();
	}

	public void delete() {
		if (selection.isActive()) {
			DeleteSelectionCommand delete = new DeleteSelectionCommand(image,
					selection.rect, drawSettings.getBackgroundColor(), this);
			delete.doTo(this, toolController);
			history.addCommand(delete);
			view.refresh();
		}
	}

	public void cut() {
		copy();
		delete();
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
			selection.movement = new SelectionMoveCommand(image,
					newSelection.copy(), toPaste, DrawAreaController.this);
			updatingTool = true;
			selection.movement.doTo(DrawAreaController.this, toolController);
			updatingTool = false;
			setSelection(newSelection);
		}

		@Override
		public boolean hasAnyEffect() {
			return true;
		}
	}

	public void escape() {
		if (selection.isActive())
			updateSelection(null);
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

	public void resizeImageTo(int width, int height) {
		if (width != image.getWidth() || height != image.getHeight()) {
			ResizeCommand resize = new ResizeCommand(width, height,
					drawSettings.getBackgroundColor());
			resize.doTo(this, toolController);
			history.addCommand(resize);
			view.refresh();
		}
	}

	public void mirrorHorizontally() {
		if (selection.isActive()) {
			MirrorHorizontallyCommand mirror = new MirrorHorizontallyCommand(
					selection.rect, this);
			mirror.doTo(this, toolController);
			history.addCommand(mirror);
		}
	}

	public void mirrorVertically() {
		if (selection.isActive()) {
			MirrorVerticallyCommand mirror = new MirrorVerticallyCommand(
					selection.rect, this);
			mirror.doTo(this, toolController);
			history.addCommand(mirror);
		}
	}
}
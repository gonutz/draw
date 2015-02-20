package draw;

import static org.junit.Assert.*;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;

public class TestDrawAreaController {

	private static final int BLACK = 0xFF000000;
	private static final int WHITE = 0xFFFFFFFF;

	private class SpyView implements DrawAreaView {
		private int refreshCount;
		private Rectangle selecetion;
		private int top;
		private int left;

		public void refresh() {
			refreshCount++;
		}

		public void setSelection(Rectangle selection) {
			this.selecetion = selection;
		}

		public Point getVisibleTopLeftCorner() {
			Point p = new Point();
			p.x = left;
			p.y = top;
			return p;
		}

	}

	private class MockDrawSettings implements DrawSettings {
		private Color foregroundColor;
		private Color backgroundColor;

		@Override
		public Color getForegroundColor() {
			return foregroundColor;
		}

		@Override
		public Color getBackgroundColor() {
			return backgroundColor;
		}

		@Override
		public void setForegroundColor(Color color) {
			foregroundColor = color;
		}

		@Override
		public void setBackgroundColor(Color color) {
			backgroundColor = color;
		}
	}

	private class DummyToolView implements ToolView {
		public void setSelection(Tool selected) {
		}
	}

	private class MockClipboard implements Clipboard {
		private BufferedImage image;

		@Override
		public void storeImage(BufferedImage image) {
			this.image = ImageUtils.copyImage(image);
		}

		@Override
		public BufferedImage getImage() {
			return image;
		}
	}

	@Before
	public void setup() {
		view = new SpyView();
		controller = new DrawAreaController(view);
		drawSettings = new MockDrawSettings();
		controller.setDrawSettings(drawSettings);
		toolController = new ToolViewController(new DummyToolView());
		controller.setToolController(toolController);
		toolController.setObserver(controller);
		clipboard = new MockClipboard();
		controller.setClipboard(clipboard);
	}

	private SpyView view;
	private MockDrawSettings drawSettings;
	private ToolViewController toolController;
	private DrawAreaController controller;
	private MockClipboard clipboard;
	private int previousRefreshs;

	@Test
	public void newImage_CreatesBufferedImageWithBackgroundColor() {
		final int width = 20;
		final int height = 10;
		final int color = 0x0A0B0C0D;
		drawSettings.backgroundColor = new Color(color, true);

		controller.newImage(width, height);

		assertImageSize(width, height);
		assertPixelsAreSet(0, color);
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				assertEquals(x + " " + y, color,
						controller.getImage().getRGB(x, y));
			}
		}
	}

	private void assertImageSize(BufferedImage image, int width, int height) {
		assertEquals("width", width, image.getWidth());
		assertEquals("height", height, image.getHeight());
	}

	private void assertImageSize(int width, int height) {
		assertImageSize(controller.getImage(), width, height);
	}

	private void assertPixelsAreSet(BufferedImage image, int foreground,
			int background, Point... set) {
		HashSet<Point> setPixels = new HashSet<Point>();
		for (Point p : set)
			setPixels.add(p);
		for (int x = 0; x < image.getWidth(); x++)
			for (int y = 0; y < image.getHeight(); y++) {
				int expected = background;
				if (setPixels.contains(p(x, y)))
					expected = foreground;
				assertEquals(x + " " + y, expected, image.getRGB(x, y));
			}
	}

	private void assertPixelsAreSet(int foreground, int background,
			Point... set) {
		assertPixelsAreSet(controller.getImage(), foreground, background, set);
	}

	private Point p(int x, int y) {
		return new Point(x, y);
	}

	private Point from(int x, int y) {
		return new Point(x, y);
	}

	private Point to(int x, int y) {
		return new Point(x, y);
	}

	@Test
	public void afterNewImageCreation_ViewIsRefreshed() {
		assertEquals(0, view.refreshCount);
		controller.newImage(10, 5);
		assertEquals(1, view.refreshCount);
	}

	@Test
	public void leftClickWithPen_PaintsDotInForegroundColor() {
		final int color = 0xFFFFAACC;
		new20x10imageWithPenColor(color);
		final int x = 3, y = 4;
		captureCurrentRefreshCount();

		controller.leftMouseButtonDown(x, y);
		controller.leftMouseButtonUp();

		assertPixelsAreSet(color, WHITE, p(x, y));
		assertRefreshesSinceLastCapture(1);
	}

	private void captureCurrentRefreshCount() {
		previousRefreshs = view.refreshCount;
	}

	private void assertRefreshesSinceLastCapture(int i) {
		assertEquals(i, view.refreshCount - previousRefreshs);
	}

	private void new20x10imageWithPenColor(int color) {
		drawSettings.foregroundColor = new Color(color);
		drawSettings.backgroundColor = Color.white;
		toolController.selectTool(Tool.Pen);
		controller.newImage(20, 10);
	}

	private void new10x8imageWithLineColor(int color) {
		drawSettings.foregroundColor = new Color(color);
		drawSettings.backgroundColor = Color.white;
		toolController.selectTool(Tool.Line);
		controller.newImage(10, 8);
	}

	@Test
	public void draggingPen_DrawsLine() {
		final int color = 0xFF010203;
		new20x10imageWithPenColor(color);
		captureCurrentRefreshCount();

		controller.leftMouseButtonDown(0, 0);
		controller.mouseMovedTo(1, 3);
		controller.mouseMovedTo(3, 3);
		controller.leftMouseButtonUp();

		assertRefreshesSinceLastCapture(3);
		assertPixelsAreSet(color, WHITE, //
				p(0, 0), p(0, 1), p(1, 2), p(1, 3),//
				p(2, 3), p(3, 3));
	}

	@Test
	public void movingMouseWithLeftButtonUp_DrawsNothing() {
		final int color = 0xFF123456;
		new20x10imageWithPenColor(color);
		captureCurrentRefreshCount();

		controller.mouseMovedTo(0, 0);
		controller.mouseMovedTo(1, 1);

		assertRefreshesSinceLastCapture(0);
		assertPixelsAreSet(color, WHITE);
	}

	@Test
	public void liftingAndDroppingPenAgain_DrawsTwoPoints() {
		final int color = 0xFF006660;
		new20x10imageWithPenColor(color);
		captureCurrentRefreshCount();

		controller.leftMouseButtonDown(0, 0);
		controller.leftMouseButtonUp();
		controller.mouseMovedTo(3, 3);
		controller.leftMouseButtonDown(3, 3);
		controller.leftMouseButtonUp();

		assertRefreshesSinceLastCapture(2);
		assertPixelsAreSet(color, WHITE, p(0, 0), p(3, 3));
	}

	@Test
	public void draggingLeftMouse_WithRectangleSelectionTool_DoesNotDraw() {
		new20x10imageWithPenColor(BLACK);
		captureCurrentRefreshCount();

		toolController.selectTool(Tool.RectangleSelection);
		controller.leftMouseButtonDown(0, 0);

		assertRefreshesSinceLastCapture(0);
		assertPixelsAreSet(BLACK, WHITE);
	}

	@Test
	public void undoingPenDot_ErasesDot() {
		new20x10imageWithPenColor(0xFF123456);
		captureCurrentRefreshCount();

		controller.leftMouseButtonDown(0, 0);
		controller.leftMouseButtonUp();
		controller.undoLastAction();

		assertRefreshesSinceLastCapture(2);
		assertPixelsAreSet(0xFF123456, WHITE);
	}

	@Test
	public void undoingPenStroke_ErasesLine() {
		final int color = 0xFF123456;
		new20x10imageWithPenColor(color);
		captureCurrentRefreshCount();

		controller.leftMouseButtonDown(1, 2);
		controller.mouseMovedTo(4, 5);
		controller.leftMouseButtonUp();
		controller.undoLastAction();

		assertRefreshesSinceLastCapture(3);
		assertPixelsAreSet(color, WHITE);
	}

	@Test
	public void undoPen_OnlyErasesLastStrokeSinceMouseWasUp() {
		final int color = 0xFF321321;
		new20x10imageWithPenColor(color);
		captureCurrentRefreshCount();

		controller.leftMouseButtonDown(2, 2);
		controller.leftMouseButtonUp();
		controller.leftMouseButtonDown(3, 3);
		controller.leftMouseButtonUp();
		controller.undoLastAction();

		assertRefreshesSinceLastCapture(3);
		assertPixelsAreSet(color, WHITE, p(2, 2));
	}

	@Test
	public void undoPen_ErasesAllStrokesSinceMouseWasUp() {
		final int color = 0xFF321355;
		new20x10imageWithPenColor(color);
		captureCurrentRefreshCount();

		controller.leftMouseButtonDown(2, 2);
		controller.mouseMovedTo(3, 3);
		controller.leftMouseButtonUp();
		controller.leftMouseButtonDown(6, 5);
		controller.mouseMovedTo(8, 5);
		controller.mouseMovedTo(8, 6);
		controller.leftMouseButtonUp();
		controller.undoLastAction();

		assertRefreshesSinceLastCapture(6);
		assertPixelsAreSet(color, WHITE, p(2, 2), p(3, 3));
	}

	@Test
	public void undoPen_WithoutAnyStroke_DoesNothing() {
		new20x10imageWithPenColor(0xFF112233);
		captureCurrentRefreshCount();

		controller.undoLastAction();

		assertRefreshesSinceLastCapture(0);
		assertPixelsAreSet(0xFF112233, WHITE);
	}

	@Test
	public void undoingPenTwice_ErasesLastTwoStrokes() {
		final int color = 0xFF000001;
		new20x10imageWithPenColor(color);
		captureCurrentRefreshCount();

		controller.leftMouseButtonDown(2, 2);
		controller.mouseMovedTo(3, 3);
		controller.leftMouseButtonUp();
		controller.leftMouseButtonDown(6, 5);
		controller.leftMouseButtonUp();
		controller.leftMouseButtonDown(8, 4);
		controller.leftMouseButtonUp();
		controller.undoLastAction();
		controller.undoLastAction();

		assertRefreshesSinceLastCapture(6);
		assertPixelsAreSet(color, WHITE, p(2, 2), p(3, 3));
	}

	@Test
	public void undoPen_OverwritesPixelsWithPreviousColor() {
		final int firstColor = 0xFFFF0000;
		final int secondColor = 0xFF0000FF;
		new20x10imageWithPenColor(firstColor);

		controller.leftMouseButtonDown(0, 0);
		controller.leftMouseButtonUp();
		drawSettings.foregroundColor = new Color(secondColor);
		controller.leftMouseButtonDown(0, 0);
		controller.leftMouseButtonUp();
		controller.undoLastAction();

		assertPixelsAreSet(firstColor, WHITE, p(0, 0));
	}

	@Test
	public void strokePenOutsideImage_SkipsInvalidPoints() {
		new20x10imageWithPenColor(BLACK);

		controller.leftMouseButtonDown(1, 1);
		controller.mouseMovedTo(-1, -1);
		controller.leftMouseButtonUp();
		controller.leftMouseButtonDown(18, 8);
		controller.mouseMovedTo(20, 10);
		controller.leftMouseButtonUp();

		assertPixelsAreSet(BLACK, WHITE, p(1, 1), p(0, 0), p(18, 8), p(19, 9));
	}

	@Test
	public void rightDraggingPen_DrawsWithBackgroundColor() {
		drawSettings.backgroundColor = Color.white;
		controller.newImage(20, 10);
		final int color = 0xFF123456;
		drawSettings.backgroundColor = new Color(color, true);
		toolController.selectTool(Tool.Pen);
		captureCurrentRefreshCount();

		controller.rightMouseButtonDown(1, 1);
		controller.mouseMovedTo(3, 1);
		controller.rightMouseButtonUp();

		assertRefreshesSinceLastCapture(2);
		assertPixelsAreSet(color, WHITE, p(1, 1), p(2, 1), p(3, 1));
	}

	@Test
	public void redoingPenStroke_BringsItBack() {
		final int color = 0xFF332211;
		new20x10imageWithPenColor(color);
		captureCurrentRefreshCount();

		controller.leftMouseButtonDown(0, 0);
		controller.leftMouseButtonUp();
		controller.undoLastAction();
		controller.redoPreviousAction();

		assertRefreshesSinceLastCapture(3);
		assertPixelsAreSet(color, WHITE, p(0, 0));
	}

	@Test
	public void redoingWhenNothingWasUndone_DoesNothing() {
		new20x10imageWithPenColor(BLACK);
		captureCurrentRefreshCount();

		controller.redoPreviousAction();

		assertRefreshesSinceLastCapture(0);
		assertPixelsAreSet(BLACK, WHITE);
	}

	@Test
	public void redoingTwoStrokesBringsThemBack() {
		new20x10imageWithPenColor(BLACK);
		captureCurrentRefreshCount();

		controller.leftMouseButtonDown(1, 2);
		controller.leftMouseButtonUp();
		controller.leftMouseButtonDown(5, 4);
		controller.leftMouseButtonUp();
		controller.undoLastAction();
		controller.undoLastAction();
		controller.redoPreviousAction();
		controller.redoPreviousAction();

		assertRefreshesSinceLastCapture(6);
		assertPixelsAreSet(BLACK, WHITE, p(1, 2), p(5, 4));
	}

	@Test
	public void afterRedoingEverything_NothingIsRedoneAnymore() {
		new20x10imageWithPenColor(BLACK);
		captureCurrentRefreshCount();

		controller.leftMouseButtonDown(0, 0);
		controller.leftMouseButtonUp();
		controller.undoLastAction();
		for (int i = 0; i < 10; i++)
			controller.redoPreviousAction();

		assertRefreshesSinceLastCapture(3);
	}

	@Test
	public void newPenStrokeAfterUndoing_ErasesRedoHistory() {
		new20x10imageWithPenColor(BLACK);

		controller.leftMouseButtonDown(0, 0);
		controller.leftMouseButtonUp();
		controller.leftMouseButtonDown(1, 1);
		controller.leftMouseButtonUp();
		controller.undoLastAction();
		controller.leftMouseButtonDown(2, 2);
		controller.leftMouseButtonUp();
		controller.undoLastAction();
		controller.undoLastAction();

		assertPixelsAreSet(BLACK, WHITE);
	}

	@Test
	public void undoRedoCombinationsWork() {
		new20x10imageWithPenColor(BLACK);

		controller.leftMouseButtonDown(1, 0);
		controller.leftMouseButtonUp();
		controller.leftMouseButtonDown(2, 0);
		controller.leftMouseButtonUp();
		controller.leftMouseButtonDown(3, 0);
		controller.leftMouseButtonUp();
		controller.undoLastAction();
		controller.undoLastAction();
		controller.leftMouseButtonDown(4, 0);
		controller.leftMouseButtonUp();
		controller.undoLastAction();
		controller.undoLastAction();
		// now there are four strokes and 4 undos => image should be empty

		assertPixelsAreSet(BLACK, WHITE);
	}

	@Test
	public void undoingFirstNewImage_DoesNothing() {
		captureCurrentRefreshCount();

		controller.newImage(10, 5);
		controller.undoLastAction();

		assertRefreshesSinceLastCapture(1);
		assertImageSize(10, 5);
	}

	@Test
	public void undoingNewImage_RestoresOldImageSize() {
		captureCurrentRefreshCount();

		controller.newImage(5, 10);
		controller.newImage(20, 5);
		controller.undoLastAction();

		assertRefreshesSinceLastCapture(3);
		assertImageSize(5, 10);
	}

	@Test
	public void undoingNewImage_RestoresOldImage() {
		new20x10imageWithPenColor(BLACK);
		controller.leftMouseButtonDown(0, 0);
		controller.mouseMovedTo(2, 2);
		controller.leftMouseButtonUp();
		captureCurrentRefreshCount();

		drawSettings.backgroundColor = Color.cyan;
		controller.newImage(5, 5);
		controller.undoLastAction();

		assertRefreshesSinceLastCapture(2);
		assertImageSize(20, 10);
		assertPixelsAreSet(BLACK, WHITE, p(0, 0), p(1, 1), p(2, 2));
	}

	@Test
	public void redoingNewImage_BringsBackEmptyImage() {
		new20x10imageWithPenColor(BLACK);
		captureCurrentRefreshCount();

		final int back = 0xFF123456;
		drawSettings.backgroundColor = new Color(back);
		controller.newImage(5, 10);
		controller.undoLastAction();
		controller.redoPreviousAction();

		assertRefreshesSinceLastCapture(3);
		assertImageSize(5, 10);
		assertPixelsAreSet(BLACK, back);
	}

	@Test
	public void chainedUndoAndRedos_ReproducePreviousImages() {
		new20x10imageWithPenColor(BLACK);
		controller.newImage(20, 10);
		controller.leftMouseButtonDown(0, 0);
		controller.leftMouseButtonUp();
		controller.newImage(20, 10);
		controller.leftMouseButtonDown(0, 0);
		controller.leftMouseButtonUp();
		// now we did: new, new, pen, new, pen
		for (int i = 0; i < 20; i++)
			controller.undoLastAction();
		for (int i = 0; i < 20; i++)
			controller.redoPreviousAction();
		// everything should be back to where we ended
		controller.undoLastAction(); // last pen
		controller.undoLastAction(); // last new image

		assertPixelsAreSet(BLACK, WHITE, p(0, 0));
	}

	@Test
	public void leftDragPenStroke_IsAbortedOnRightMouseDown() {
		new20x10imageWithPenColor(BLACK);
		captureCurrentRefreshCount();

		controller.leftMouseButtonDown(0, 0);
		controller.mouseMovedTo(2, 0);
		controller.rightMouseButtonDown(2, 0);
		controller.rightMouseButtonUp();
		controller.leftMouseButtonUp();

		assertRefreshesSinceLastCapture(3);
		assertPixelsAreSet(BLACK, WHITE);
	}

	@Test
	public void abortedLeftPenStroke_IsNotStoredInUndoHistory() {
		new20x10imageWithPenColor(BLACK);
		captureCurrentRefreshCount();

		controller.leftMouseButtonDown(0, 0);
		controller.mouseMovedTo(2, 0);
		controller.rightMouseButtonDown(2, 0);
		controller.rightMouseButtonUp();
		controller.leftMouseButtonUp();
		controller.undoLastAction();

		assertRefreshesSinceLastCapture(3);
		assertPixelsAreSet(BLACK, WHITE);
	}

	@Test
	public void abortingLeftPenStrokeWithRightClick_EndsStroke() {
		new20x10imageWithPenColor(BLACK);
		captureCurrentRefreshCount();

		controller.leftMouseButtonDown(0, 0);
		controller.rightMouseButtonDown(0, 0);
		controller.mouseMovedTo(2, 0);
		controller.rightMouseButtonUp();
		controller.leftMouseButtonUp();

		assertRefreshesSinceLastCapture(2);
		assertPixelsAreSet(BLACK, WHITE);
	}

	@Test
	public void rightPenStrokesAreUndoable() {
		new20x10imageWithPenColor(BLACK);
		final int color = 0xFF445566;
		drawSettings.backgroundColor = new Color(color, true);
		captureCurrentRefreshCount();

		controller.rightMouseButtonDown(0, 0);
		controller.rightMouseButtonUp();
		controller.rightMouseButtonDown(1, 1);
		controller.rightMouseButtonUp();
		controller.undoLastAction();

		assertRefreshesSinceLastCapture(3);
		assertPixelsAreSet(color, WHITE, p(0, 0));
	}

	private void new20x10imageWithSelectionTool() {
		drawSettings.foregroundColor = Color.black;
		drawSettings.backgroundColor = Color.white;
		toolController.selectTool(Tool.RectangleSelection);
		controller.newImage(20, 10);
	}

	private void assertSelection(int x, int y, int x2, int y2) {
		assertEquals("x", x, view.selecetion.x);
		assertEquals("y", y, view.selecetion.y);
		assertEquals("x2", x2, view.selecetion.x2);
		assertEquals("y2", y2, view.selecetion.y2);
	}

	private void assertNoSelectionIsMade() {
		assertNull(view.selecetion);
	}

	@Test
	public void selectionRectangleIsSetInView() {
		new20x10imageWithSelectionTool();
		captureCurrentRefreshCount();

		controller.leftMouseButtonDown(0, 0);
		controller.mouseMovedTo(2, 2);

		assertRefreshesSinceLastCapture(1);
		assertSelection(0, 0, 2, 2);
	}

	@Test
	public void selectionRectangleCanGoUpLeft() {
		new20x10imageWithSelectionTool();
		captureCurrentRefreshCount();

		controller.leftMouseButtonDown(5, 6);
		controller.mouseMovedTo(3, 1);

		assertRefreshesSinceLastCapture(1);
		assertSelection(5, 6, 3, 1);
	}

	@Test
	public void selectionRectangleIsResizedWhileLeftMouseDown() {
		new20x10imageWithSelectionTool();
		captureCurrentRefreshCount();

		controller.leftMouseButtonDown(2, 3);
		controller.mouseMovedTo(0, 0);
		controller.mouseMovedTo(5, 6);

		assertRefreshesSinceLastCapture(2);
		assertSelection(2, 3, 5, 6);
	}

	@Test
	public void selectionIsDoneWhenLeftMouseIsLieftedUp() {
		new20x10imageWithSelectionTool();
		captureCurrentRefreshCount();

		controller.leftMouseButtonDown(0, 0);
		controller.mouseMovedTo(2, 2);
		controller.leftMouseButtonUp();
		controller.mouseMovedTo(4, 4);

		assertRefreshesSinceLastCapture(1);
		assertSelection(0, 0, 2, 2);
	}

	@Test
	public void selectionStaysInUpperLeftImageBounds() {
		new20x10imageWithSelectionTool();

		controller.leftMouseButtonDown(1, 1);
		controller.mouseMovedTo(-2, -4);

		assertSelection(1, 1, 0, 0);
	}

	@Test
	public void selectionStaysInLowerRightImageBounds() {
		new20x10imageWithSelectionTool();

		controller.leftMouseButtonDown(1, 1);
		controller.mouseMovedTo(100, 100);

		assertSelection(1, 1, 19, 9);
	}

	@Test
	public void leftClick_RemovesSelectionRectangle() {
		new20x10imageWithSelectionTool();
		captureCurrentRefreshCount();

		controller.leftMouseButtonDown(1, 1);
		controller.mouseMovedTo(3, 3);
		controller.leftMouseButtonUp();
		controller.leftMouseButtonDown(5, 5);
		controller.leftMouseButtonUp();

		assertRefreshesSinceLastCapture(2);
		assertNoSelectionIsMade();
	}

	@Test
	public void rightMouseDoesNotSelectRectangle() {
		new20x10imageWithSelectionTool();
		captureCurrentRefreshCount();

		controller.rightMouseButtonDown(0, 0);
		controller.mouseMovedTo(2, 2);

		assertRefreshesSinceLastCapture(0);
		assertNoSelectionIsMade();
	}

	@Test
	public void selectionCanBeDragged() {
		new20x10imageWithSelectionTool();
		captureCurrentRefreshCount();

		selectRect(2, 2, 6, 6);
		controller.leftMouseButtonDown(4, 4);
		controller.mouseMovedTo(2, 4);

		assertRefreshesSinceLastCapture(2);
		assertSelection(0, 2, 4, 6);
	}

	private void selectRect(int x, int y, int x2, int y2) {
		toolController.selectTool(Tool.RectangleSelection);
		controller.leftMouseButtonDown(x, y);
		controller.mouseMovedTo(x2, y2);
		controller.leftMouseButtonUp();
	}

	@Test
	public void draggingSelectionStops_WhenLeftMouseIsLiftedUp() {
		new20x10imageWithSelectionTool();
		captureCurrentRefreshCount();

		selectRect(2, 2, 6, 6);
		controller.leftMouseButtonDown(4, 4);
		controller.mouseMovedTo(2, 4);
		controller.leftMouseButtonUp();
		controller.mouseMovedTo(-5, -5);

		assertRefreshesSinceLastCapture(2);
		assertSelection(0, 2, 4, 6);
	}

	@Test
	public void draggingSelectionMovesImagePart() {
		new20x10imageWithPenColor(BLACK);
		drawPenDot(10, 0);
		drawPenDot(10, 1);
		drawPenDot(12, 0);
		selectRect(10, 0, 12, 2);
		captureCurrentRefreshCount();

		controller.leftMouseButtonDown(10, 0);
		controller.mouseMovedTo(12, 1);

		assertRefreshesSinceLastCapture(1);
		assertPixelsAreSet(BLACK, WHITE, p(12, 1), p(12, 2), p(14, 1));
	}

	private void drawPenDot(int x, int y) {
		toolController.selectTool(Tool.Pen);
		controller.leftMouseButtonDown(x, y);
		controller.leftMouseButtonUp();
	}

	@Test
	public void movingSelection_ReplacesOriginalWithBackgroundColor() {
		new20x10imageWithSelectionTool();

		// move background from = to x
		// ...... => ......
		// .OOO.. => .OOO..
		// .OOO.. => .Oxxx.
		// ...... => ..xxx.
		// ...... => ......
		selectRect(1, 1, 3, 2);
		drawSettings.backgroundColor = Color.black;
		controller.leftMouseButtonDown(2, 1);
		controller.mouseMovedTo(3, 2);

		assertPixelsAreSet(BLACK, WHITE, p(1, 1), p(2, 1), p(3, 1), p(1, 2));
	}

	@Test
	public void selectionIsMovedUntilDeselected() {
		new20x10imageWithPenColor(BLACK);
		drawPenDot(0, 0);

		// place a background pixel on top of the black pixel
		selectRect(1, 1, 2, 2);
		controller.leftMouseButtonDown(1, 1);
		controller.mouseMovedTo(0, 0);
		controller.leftMouseButtonUp();
		// move the selection back off the black pixel
		controller.leftMouseButtonDown(0, 0);
		controller.mouseMovedTo(1, 1);
		controller.leftMouseButtonUp();

		assertPixelsAreSet(BLACK, WHITE, p(0, 0));
	}

	@Test
	public void selectionOutsideImageIsNotPossible() {
		new20x10imageWithSelectionTool();
		selectRect(100, 100, 102, 102);
		assertNoSelectionIsMade();
	}

	@Test
	public void movingSelectionCanBeUndone() {
		new20x10imageWithPenColor(BLACK);
		drawPenDot(1, 1);
		selectRect(0, 0, 2, 2);
		controller.leftMouseButtonDown(1, 1);
		controller.mouseMovedTo(5, 5);
		controller.leftMouseButtonUp();
		captureCurrentRefreshCount();

		controller.undoLastAction();

		assertRefreshesSinceLastCapture(1);
		assertPixelsAreSet(BLACK, WHITE, p(1, 1));
		assertSelection(0, 0, 2, 2);
	}

	@Test
	public void undoneSelectionCanBeMovedAgain() {
		new20x10imageWithPenColor(BLACK);
		drawPenDot(1, 1);
		captureCurrentRefreshCount();

		selectRect(0, 0, 2, 2);
		dragLeftMouse(from(1, 1), to(6, 6));
		controller.undoLastAction();
		dragLeftMouse(from(1, 1), to(6, 1));

		assertRefreshesSinceLastCapture(4);
		assertPixelsAreSet(BLACK, WHITE, p(6, 1));
		assertSelection(5, 0, 7, 2);
	}

	private void dragLeftMouse(Point from, Point to) {
		controller.leftMouseButtonDown(from.x, from.y);
		controller.mouseMovedTo(to.x, to.y);
		controller.leftMouseButtonUp();
	}

	@Test
	public void twoDifferentAreasCanBeMoved() {
		new20x10imageWithSelectionTool();
		drawSettings.backgroundColor = Color.black;

		selectRect(0, 0, 1, 0);
		dragLeftMouse(from(0, 0), to(1, 1));
		selectRect(3, 3, 4, 3);
		dragLeftMouse(from(3, 3), to(4, 4));

		assertPixelsAreSet(BLACK, WHITE, p(0, 0), p(1, 0), p(3, 3), p(4, 3));
	}

	@Test
	public void movingSelectionCanBeRedone() {
		new20x10imageWithSelectionTool();
		drawSettings.backgroundColor = Color.black;
		selectRect(0, 0, 1, 0);
		dragLeftMouse(from(0, 0), to(2, 2));
		captureCurrentRefreshCount();

		controller.undoLastAction();
		controller.redoPreviousAction();

		assertRefreshesSinceLastCapture(2);
		assertPixelsAreSet(BLACK, WHITE, p(0, 0), p(1, 0));
	}

	@Test
	public void activatingPen_DisablesCurrentSelection() {
		new20x10imageWithSelectionTool();
		selectRect(1, 2, 3, 4);
		captureCurrentRefreshCount();

		controller.toolChangedTo(Tool.Pen);

		assertRefreshesSinceLastCapture(1);
		assertNoSelectionIsMade();
	}

	@Test
	public void reactivatingSelectionTool_DisablesCurrentSelection() {
		new20x10imageWithSelectionTool();
		selectRect(1, 2, 3, 4);
		captureCurrentRefreshCount();

		controller.toolChangedTo(Tool.RectangleSelection);

		assertRefreshesSinceLastCapture(1);
		assertNoSelectionIsMade();
	}

	@Test
	public void undoingPenStrokeSetsToolToPen() {
		new20x10imageWithPenColor(BLACK);
		drawPenDot(1, 1);
		toolController.selectTool(null);

		controller.undoLastAction();

		assertEquals(Tool.Pen, toolController.getSelectedTool());
	}

	@Test
	public void undoingSelectionMovement_EnablesSelectionTool() {
		new20x10imageWithSelectionTool();
		selectRect(0, 0, 5, 5);
		dragLeftMouse(from(1, 1), to(4, 4));
		toolController.selectTool(null);

		controller.undoLastAction();

		assertEquals(Tool.RectangleSelection, toolController.getSelectedTool());
	}

	@Test
	public void redoingPen_EnablesPenTool() {
		new20x10imageWithPenColor(BLACK);
		drawPenDot(1, 1);
		controller.undoLastAction();
		toolController.selectTool(null);
		captureCurrentRefreshCount();

		controller.redoPreviousAction();

		assertRefreshesSinceLastCapture(1);
		assertEquals(Tool.Pen, toolController.getSelectedTool());
	}

	@Test
	public void redoingSelectionMovement_EnablesSelectionTool() {
		new20x10imageWithSelectionTool();
		selectRect(0, 0, 5, 5);
		dragLeftMouse(from(1, 1), to(4, 4));
		controller.undoLastAction();
		toolController.selectTool(null);

		controller.redoPreviousAction();

		assertEquals(Tool.RectangleSelection, toolController.getSelectedTool());
	}

	@Test
	public void newImageDisablesCurrentSelection() {
		new20x10imageWithSelectionTool();
		selectRect(0, 0, 5, 5);
		captureCurrentRefreshCount();

		controller.newImage(10, 5);

		assertRefreshesSinceLastCapture(1);
		assertNoSelectionIsMade();
	}

	@Test
	public void leftMouseDownWithLineTool_DrawsOnePixel() {
		final int color = 0xFF123654;
		new10x8imageWithLineColor(color);
		captureCurrentRefreshCount();

		controller.leftMouseButtonDown(0, 0);

		assertRefreshesSinceLastCapture(1);
		assertPixelsAreSet(color, WHITE, p(0, 0));
	}

	@Test
	public void lineCanBeDrawn() {
		new10x8imageWithLineColor(BLACK);
		captureCurrentRefreshCount();

		dragLeftMouse(from(2, 0), to(4, 0));

		assertRefreshesSinceLastCapture(2);
		assertPixelsAreSet(BLACK, WHITE, p(2, 0), p(3, 0), p(4, 0));
	}

	@Test
	public void movingLineRedrawsItAndRemovesPreviousLine() {
		new10x8imageWithLineColor(BLACK);
		captureCurrentRefreshCount();

		controller.leftMouseButtonDown(1, 1);
		controller.mouseMovedTo(5, 0);
		controller.mouseMovedTo(1, 3);

		assertRefreshesSinceLastCapture(3);
		assertPixelsAreSet(BLACK, WHITE, p(1, 1), p(1, 2), p(1, 3));
	}

	@Test
	public void lineToolIsStillSelectedAfterLine() {
		new10x8imageWithLineColor(BLACK);
		dragLeftMouse(from(0, 0), to(1, 1));
		assertEquals(Tool.Line, toolController.getSelectedTool());
	}

	@Test
	public void lineWithRightMouse_HasBakcgroundColor() {
		new10x8imageWithLineColor(BLACK);
		final int color = 0xFF554433;
		drawSettings.backgroundColor = new Color(color, true);

		controller.rightMouseButtonDown(0, 0);
		controller.mouseMovedTo(0, 1);

		assertPixelsAreSet(color, WHITE, p(0, 0), p(0, 1));
	}

	@Test
	public void lineCanBeUndone() {
		new10x8imageWithLineColor(BLACK);
		dragLeftMouse(from(0, 0), to(3, 6));
		captureCurrentRefreshCount();

		controller.undoLastAction();

		assertRefreshesSinceLastCapture(1);
		assertPixelsAreSet(BLACK, WHITE);
	}

	@Test
	public void undoneLineCanBeRedone() {
		new10x8imageWithLineColor(BLACK);
		controller.leftMouseButtonDown(2, 1);
		controller.mouseMovedTo(5, 1);
		controller.mouseMovedTo(0, 1);
		controller.leftMouseButtonUp();
		controller.undoLastAction();
		captureCurrentRefreshCount();

		controller.redoPreviousAction();

		assertRefreshesSinceLastCapture(1);
		assertPixelsAreSet(BLACK, WHITE, p(2, 1), p(1, 1), p(0, 1));
	}

	@Test
	public void pressingOtherButtonWhileDrawingLine_ErasesLine() {
		new10x8imageWithLineColor(BLACK);
		captureCurrentRefreshCount();

		controller.leftMouseButtonDown(0, 0);
		controller.mouseMovedTo(2, 2);
		controller.rightMouseButtonDown(2, 2);
		controller.leftMouseButtonUp();
		controller.rightMouseButtonUp();

		assertRefreshesSinceLastCapture(3);
		assertPixelsAreSet(BLACK, WHITE);
	}

	@Test
	public void lineAbortedWithOtherButton_IsNotInHistory() {
		new10x8imageWithLineColor(BLACK);
		captureCurrentRefreshCount();

		controller.leftMouseButtonDown(0, 0);
		controller.mouseMovedTo(2, 2);
		controller.rightMouseButtonDown(2, 2);
		controller.leftMouseButtonUp();
		controller.rightMouseButtonUp();
		controller.undoLastAction();
		controller.redoPreviousAction();
		controller.redoPreviousAction();

		assertRefreshesSinceLastCapture(3);
		assertPixelsAreSet(BLACK, WHITE);
	}

	@Test
	public void pressingEscape_DisablesCurrentSelection() {
		new20x10imageWithSelectionTool();
		selectRect(2, 2, 4, 4);
		captureCurrentRefreshCount();

		controller.escape();

		assertRefreshesSinceLastCapture(1);
		assertNoSelectionIsMade();
	}

	@Test
	public void pressingEscape_WithNoSelection_DoesNothing() {
		new20x10imageWithSelectionTool();
		captureCurrentRefreshCount();

		controller.escape();

		assertRefreshesSinceLastCapture(0);
	}

	@Test
	public void selectionCanBeMovedByGivenDistance() {
		new20x10imageWithPenColor(BLACK);
		drawPenDot(1, 1);
		selectRect(0, 0, 2, 2);
		captureCurrentRefreshCount();

		controller.move(1, 2);

		assertRefreshesSinceLastCapture(1);
		assertPixelsAreSet(BLACK, WHITE, p(2, 3));
	}

	@Test
	public void withNoSelection_MovingDoesNothing() {
		new20x10imageWithSelectionTool();
		captureCurrentRefreshCount();

		controller.move(-1, -2);

		assertRefreshesSinceLastCapture(0);
	}

	@Test
	public void selectingAllSelectsWholeImage() {
		new20x10imageWithPenColor(BLACK);
		captureCurrentRefreshCount();

		controller.selectAll();

		assertSelection(0, 0, 19, 9);
		assertEquals(Tool.RectangleSelection, toolController.getSelectedTool());
	}

	@Test
	public void loadedImageIsDisplayed() {
		BufferedImage image = newImageOfSize(3, 4);
		captureCurrentRefreshCount();

		controller.showLoadedImage(image);

		assertRefreshesSinceLastCapture(1);
		assertImageSize(3, 4);
	}

	private BufferedImage newImageOfSize(int w, int h) {
		return new BufferedImage(w, h, BufferedImage.TYPE_4BYTE_ABGR);
	}

	@Test
	public void loadingImageCanBeUndone() {
		new20x10imageWithPenColor(BLACK);
		drawPenDot(1, 2);
		controller.showLoadedImage(newImageOfSize(3, 4));
		captureCurrentRefreshCount();

		controller.undoLastAction();

		assertRefreshesSinceLastCapture(1);
		assertPixelsAreSet(BLACK, WHITE, p(1, 2));
	}

	@Test
	public void loadingImageCanBeRedone() {
		new20x10imageWithSelectionTool();
		final int width = 5;
		final int height = 3;
		controller.showLoadedImage(newImageOfSize(width, height));
		controller.undoLastAction();
		captureCurrentRefreshCount();

		controller.redoPreviousAction();

		assertRefreshesSinceLastCapture(1);
		assertImageSize(width, height);
	}

	@Test
	public void loadingImageDeselectsCurrentSelection() {
		new20x10imageWithSelectionTool();
		selectRect(0, 0, 5, 5);

		controller.showLoadedImage(newImageOfSize(4, 5));

		assertNoSelectionIsMade();
	}

	@Test
	public void undoingLoadingImage_DisablesCurrentSelection() {
		new20x10imageWithSelectionTool();
		controller.showLoadedImage(newImageOfSize(3, 4));
		selectRect(0, 0, 2, 2);

		controller.undoLastAction();

		assertNoSelectionIsMade();
	}

	@Test
	public void copyingSelectionSetsItInTheClipboard() {
		new20x10imageWithPenColor(BLACK);
		drawPenDot(1, 2);
		selectRect(2, 3, 0, 0);

		controller.copy();

		assertImageSize(clipboard.image, 3, 4);
		assertPixelsAreSet(clipboard.image, BLACK, WHITE, p(1, 2));
	}

	@Test
	public void copyDoesNothingIfNoSelectionIsActive() {
		new20x10imageWithSelectionTool();
		controller.copy();
		assertNull(clipboard.image);
	}

	@Test
	public void imageGetsPaintedToLeftCornerInView() {
		clipboard.image = newBlackWhiteImageWithPixels(3, 4, p(0, 2));
		new20x10imageWithSelectionTool();
		view.top = 1;
		view.left = 2;
		captureCurrentRefreshCount();

		controller.paste();

		assertRefreshesSinceLastCapture(1);
		assertSelection(2, 1, 4, 4);
		assertPixelsAreSet(BLACK, WHITE, p(2, 3));
	}

	private BufferedImage newBlackWhiteImageWithPixels(int width, int height,
			Point... pixels) {
		BufferedImage image = newImageOfSize(width, height);
		Graphics2D g = (Graphics2D) image.getGraphics();
		g.setBackground(Color.white);
		g.clearRect(0, 0, width, height);
		g.setColor(Color.black);
		for (Point p : pixels)
			g.drawLine(p.x, p.y, p.x, p.y);
		return image;
	}

	@Test
	public void ifNoImageIsInClipboard_NothingIsPasted() {
		new20x10imageWithSelectionTool();
		captureCurrentRefreshCount();

		controller.paste();

		assertRefreshesSinceLastCapture(0);
	}

	@Test
	public void deselectingPastedImageDropsItWhereItWasMoved() {
		clipboard.image = newBlackWhiteImageWithPixels(2, 3, p(1, 2));
		new20x10imageWithSelectionTool();
		captureCurrentRefreshCount();

		controller.paste();
		dragLeftMouse(from(1, 2), to(3, 4));
		controller.leftMouseButtonDown(9, 9); // disable selection
		controller.leftMouseButtonUp();

		assertRefreshesSinceLastCapture(3);
		assertPixelsAreSet(BLACK, WHITE, p(3, 4));
		assertNoSelectionIsMade();
	}

	@Test
	public void pastingImageActivatesSelectionTool() {
		clipboard.image = newImageOfSize(1, 1);
		new20x10imageWithPenColor(BLACK);

		controller.paste();

		assertEquals(Tool.RectangleSelection, toolController.getSelectedTool());
	}

	@Test
	public void movingPastedImage_LeavesTheRestAsIs() {
		clipboard.image = newBlackWhiteImageWithPixels(3, 3, p(1, 1));
		new20x10imageWithPenColor(BLACK);
		drawPenDot(0, 1);
		captureCurrentRefreshCount();

		controller.paste();
		dragLeftMouse(from(1, 1), to(4, 1));

		assertRefreshesSinceLastCapture(2);
		assertPixelsAreSet(BLACK, WHITE, p(0, 1), p(4, 1));
	}

	@Test
	public void pastingTwice_CopiesFirstPastingToImage() {
		clipboard.image = newBlackWhiteImageWithPixels(3, 3, p(1, 1));
		new20x10imageWithSelectionTool();
		captureCurrentRefreshCount();

		controller.paste();
		dragLeftMouse(from(1, 1), to(3, 4));
		controller.paste();

		assertRefreshesSinceLastCapture(3);
		assertPixelsAreSet(BLACK, WHITE, p(1, 1), p(3, 4));
	}

	@Test
	public void pastingCanBeUndone() {
		clipboard.image = newBlackWhiteImageWithPixels(3, 3, p(1, 1));
		new20x10imageWithSelectionTool();
		captureCurrentRefreshCount();

		controller.paste();
		controller.undoLastAction();

		assertRefreshesSinceLastCapture(2);
		assertNoSelectionIsMade();
		assertPixelsAreSet(BLACK, WHITE);
	}

	@Test
	public void pastingCanBeRedone() {
		clipboard.image = newBlackWhiteImageWithPixels(3, 3, p(1, 1));
		new20x10imageWithSelectionTool();
		captureCurrentRefreshCount();

		controller.paste();
		controller.undoLastAction();
		controller.redoPreviousAction();

		assertRefreshesSinceLastCapture(3);
		assertSelection(0, 0, 2, 2);
		assertPixelsAreSet(BLACK, WHITE, p(1, 1));
	}

	private void new8x3imageWithFillTool() {
		drawSettings.foregroundColor = Color.black;
		drawSettings.backgroundColor = Color.white;
		toolController.selectTool(Tool.Fill);
		controller.newImage(8, 3);
	}

	@Test
	public void fillingWhiteImageWithBlack_FillsAll() {
		new8x3imageWithFillTool();
		captureCurrentRefreshCount();

		controller.leftMouseButtonDown(0, 0);

		assertRefreshesSinceLastCapture(1);
		assertPixelsAreSet(WHITE, BLACK);
	}

	@Test
	public void fillingWithRightClick_UsesBackgroudColor() {
		new8x3imageWithFillTool();
		final int color = 0xFF556677;
		drawSettings.backgroundColor = new Color(color, true);

		controller.rightMouseButtonDown(5, 2);

		assertPixelsAreSet(BLACK, color);
	}

	@Test
	public void fillingCanBeUndone() {
		new8x3imageWithFillTool();
		controller.leftMouseButtonDown(0, 0);
		captureCurrentRefreshCount();

		controller.undoLastAction();

		assertRefreshesSinceLastCapture(1);
		assertPixelsAreSet(BLACK, WHITE);
	}

	@Test
	public void fillCanBeRedone() {
		new8x3imageWithFillTool();
		controller.leftMouseButtonDown(0, 0);
		controller.undoLastAction();
		captureCurrentRefreshCount();

		controller.redoPreviousAction();

		assertRefreshesSinceLastCapture(1);
		assertPixelsAreSet(WHITE, BLACK);
	}

	@Test
	public void undoingFill_SelectsFillTool() {
		new8x3imageWithFillTool();
		controller.leftMouseButtonDown(0, 0);
		toolController.selectTool(Tool.Pen);
		captureCurrentRefreshCount();

		controller.undoLastAction();

		assertRefreshesSinceLastCapture(1);
		assertEquals(Tool.Fill, toolController.getSelectedTool());
	}

	@Test
	public void redoingFill_SelectsFillTool() {
		new8x3imageWithFillTool();
		controller.leftMouseButtonDown(0, 0);
		controller.undoLastAction();
		toolController.selectTool(Tool.Pen);
		captureCurrentRefreshCount();

		controller.redoPreviousAction();

		assertRefreshesSinceLastCapture(1);
		assertEquals(Tool.Fill, toolController.getSelectedTool());
	}

	@Test
	public void pickingWithLeftMouse_ChangesForegroundColor() {
		int color = 0xFF112233;
		new20x10imageWithPenColor(color);
		drawPenDot(5, 5);
		toolController.selectTool(Tool.ColorPicker);
		drawSettings.foregroundColor = Color.black;

		controller.leftMouseButtonDown(5, 5);

		assertEquals(new Color(color, true), drawSettings.foregroundColor);
	}

	@Test
	public void pickingWithRightMouse_ChangesBackgroundColor() {
		int color = 0xFF112233;
		new20x10imageWithPenColor(color);
		drawPenDot(2, 3);
		toolController.selectTool(Tool.ColorPicker);
		drawSettings.backgroundColor = Color.black;

		controller.rightMouseButtonDown(2, 3);

		assertEquals(new Color(color, true), drawSettings.backgroundColor);
	}

	@Test
	public void afterPickingColor_PreviousToolIsSelected() {
		new8x3imageWithFillTool();
		toolController.selectTool(Tool.ColorPicker);

		controller.leftMouseButtonDown(0, 0);

		assertEquals(Tool.Fill, toolController.getSelectedTool());
	}

	@Test
	public void afterPickingColor_PreviousNonColorPickerToolIsSelected() {
		new8x3imageWithFillTool();
		toolController.selectTool(Tool.Pen);
		toolController.selectTool(Tool.ColorPicker);
		toolController.selectTool(Tool.ColorPicker);

		controller.leftMouseButtonDown(0, 0);

		assertEquals(Tool.Pen, toolController.getSelectedTool());
	}

	@Test
	public void deletingSelection_SetsItToBackgroundColor() {
		new20x10imageWithSelectionTool();
		final int color = 0xF4112233;
		drawSettings.backgroundColor = new Color(color, true);
		selectRect(2, 3, 4, 4);
		captureCurrentRefreshCount();

		controller.delete();

		assertRefreshesSinceLastCapture(1);
		assertPixelsAreSet(color, WHITE, p(2, 3), p(3, 3), p(4, 3), p(2, 4),
				p(3, 4), p(4, 4));
		assertNoSelectionIsMade();
	}

	@Test
	public void undoingDelete_BringsBackImage_AndRestoresSelection() {
		new20x10imageWithSelectionTool();
		drawSettings.backgroundColor = Color.black;
		selectRect(2, 3, 4, 4);
		controller.delete();
		captureCurrentRefreshCount();

		controller.undoLastAction();

		assertRefreshesSinceLastCapture(1);
		assertPixelsAreSet(BLACK, WHITE);
		assertSelection(2, 3, 4, 4);
	}

	@Test
	public void deletingWithNoSelection_DoesNothing() {
		controller.delete();
	}

	@Test
	public void redoneMovedPastedArea_CanBeMovedAgain() {
		// this test is a bug fix
		new20x10imageWithSelectionTool();
		selectRect(0, 0, 2, 2);
		controller.copy();
		controller.paste();
		dragLeftMouse(from(0, 0), to(5, 5)); // move the pasted area
		assertSelection(5, 5, 7, 7);
		controller.undoLastAction(); // move it back
		controller.undoLastAction(); // delete pasted area
		controller.redoPreviousAction(); // bring area back
		controller.redoPreviousAction(); // move it again
		assertSelection(5, 5, 7, 7);

		dragLeftMouse(from(5, 5), to(6, 6));

		// TODO fix this bug
		assertSelection(6, 6, 8, 8);
	}

	@Test
	public void cut_deletesSelectedArea() {
		new20x10imageWithPenColor(BLACK);
		drawPenDot(1, 1);
		selectRect(0, 0, 2, 2);
		captureCurrentRefreshCount();

		controller.cut();

		assertRefreshesSinceLastCapture(1);
		assertPixelsAreSet(BLACK, WHITE);
	}

	@Test
	public void cutArea_CanBePasted() {
		new20x10imageWithPenColor(BLACK);
		drawPenDot(1, 1);
		selectRect(0, 0, 2, 2);
		captureCurrentRefreshCount();

		controller.cut();
		controller.paste();

		assertRefreshesSinceLastCapture(2);
		assertPixelsAreSet(BLACK, WHITE, p(1, 1));
	}

	@Test
	public void resizingToSmallerImage_CutsOffEdges() {
		new20x10imageWithPenColor(BLACK);
		drawPenDot(2, 2);
		captureCurrentRefreshCount();

		controller.resizeImageTo(15, 5);

		assertRefreshesSinceLastCapture(1);
		assertImageSize(15, 5);
		assertPixelsAreSet(BLACK, WHITE, p(2, 2));
	}

	@Test
	public void resizingToSameSize_DoesNothing() {
		new20x10imageWithPenColor(BLACK);
		drawPenDot(3, 3);
		captureCurrentRefreshCount();

		controller.resizeImageTo(20, 10);

		assertRefreshesSinceLastCapture(0);
		assertImageSize(20, 10);
		assertPixelsAreSet(BLACK, WHITE, p(3, 3));
	}

	@Test
	public void resizingToBiggerSize_FillsVoidWithBackgroundColor() {
		new8x3imageWithFillTool();
		drawPenDot(4, 4);
		drawSettings.backgroundColor = Color.black;
		captureCurrentRefreshCount();

		controller.resizeImageTo(9, 3);

		assertRefreshesSinceLastCapture(1);
		assertImageSize(9, 3);
		assertPixelsAreSet(BLACK, WHITE, p(4, 4), p(8, 0), p(8, 1), p(8, 2));
	}

	@Test
	public void resizingImageCanBeUndone() {
		new20x10imageWithPenColor(BLACK);
		drawPenDot(19, 9);
		controller.resizeImageTo(1, 1);
		captureCurrentRefreshCount();

		controller.undoLastAction();

		assertRefreshesSinceLastCapture(1);
		assertImageSize(20, 10);
		assertPixelsAreSet(BLACK, WHITE, p(19, 9));
	}
}

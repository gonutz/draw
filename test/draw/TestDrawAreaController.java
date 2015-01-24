package draw;

import static org.junit.Assert.*;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;

public class TestDrawAreaController {

	private static final int BLACK = 0xFF000000;
	private static final int WHITE = 0xFFFFFFFF;

	private SpyView view;
	private DrawAreaController controller;
	private StubDrawSettings drawSettings;
	private int previousRefreshs;

	private class SpyView implements DrawAreaView {
		private int refreshCount;

		public void refresh() {
			refreshCount++;
		}
	}

	private class StubDrawSettings implements DrawSettings {
		private Color foregroundColor;
		private Color backgroundColor;
		private Tool tool;

		@Override
		public Color getForegroundColor() {
			return foregroundColor;
		}

		@Override
		public Color getBackgroundColor() {
			return backgroundColor;
		}

		@Override
		public Tool getCurrentTool() {
			return tool;
		}
	}

	@Before
	public void setup() {
		view = new SpyView();
		controller = new DrawAreaController(view);
		drawSettings = new StubDrawSettings();
		controller.setDrawSettings(drawSettings);
	}

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

	private void assertImageSize(int width, int height) {
		assertEquals(width, controller.getImage().getWidth());
		assertEquals(height, controller.getImage().getHeight());
	}

	private void assertPixelsAreSet(int foreground, int background,
			Point... set) {
		HashSet<Point> setPixels = new HashSet<Point>();
		for (Point p : set)
			setPixels.add(p);
		BufferedImage img = controller.getImage();
		for (int x = 0; x < img.getWidth(); x++)
			for (int y = 0; y < img.getHeight(); y++) {
				int expected = background;
				if (setPixels.contains(p(x, y)))
					expected = foreground;
				assertEquals(x + " " + y, expected, img.getRGB(x, y));
			}
	}

	private Point p(int x, int y) {
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
		drawSettings.tool = Tool.Pen;
		controller.newImage(20, 10);
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

		drawSettings.tool = Tool.RectangleSelection;
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
		drawSettings.tool = Tool.Pen;
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
}

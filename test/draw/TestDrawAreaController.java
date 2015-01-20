package draw;

import static org.junit.Assert.*;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;

public class TestDrawAreaController {

	private SpyView view;
	private DrawAreaController controller;
	private StubDrawSettings drawSettings;

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

		assertEquals(width, controller.getImage().getWidth());
		assertEquals(height, controller.getImage().getHeight());
		assertForegroundPixelsAreSet(0, color);
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				assertEquals(x + " " + y, color,
						controller.getImage().getRGB(x, y));
			}
		}
	}

	private void assertForegroundPixelsAreSet(int foreground, int background,
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
		new20x10imageWithPenForegroundColor(color);
		final int x = 3, y = 4;
		int previousRefreshs = view.refreshCount;

		controller.leftMouseButtonDown(x, y);
		controller.leftMouseButtonUp(x, y);

		assertForegroundPixelsAreSet(color, 0xFFFFFFFF, p(x, y));
		assertEquals(previousRefreshs + 1, view.refreshCount);
	}

	private void new20x10imageWithPenForegroundColor(int color) {
		drawSettings.foregroundColor = new Color(color);
		drawSettings.backgroundColor = Color.white;
		drawSettings.tool = Tool.Pen;
		controller.newImage(20, 10);
	}

	@Test
	public void draggingPen_DrawsLine() {
		final int color = 0xFF010203;
		new20x10imageWithPenForegroundColor(color);
		int previousRefreshs = view.refreshCount;

		controller.leftMouseButtonDown(0, 0);
		controller.mouseMovedTo(1, 3);
		controller.mouseMovedTo(3, 3);
		controller.leftMouseButtonUp(2, 3);

		assertEquals(previousRefreshs + 3, view.refreshCount);
		assertForegroundPixelsAreSet(color, 0xFFFFFFFF, //
				p(0, 0), p(0, 1), p(1, 2), p(1, 3),//
				p(2, 3), p(3, 3));
	}

	@Test
	public void movingMouseWithLeftButtonUp_DrawsNothing() {
		final int color = 0xFF123456;
		new20x10imageWithPenForegroundColor(color);
		int previousRefreshs = view.refreshCount;

		controller.mouseMovedTo(0, 0);
		controller.mouseMovedTo(1, 1);

		assertEquals(previousRefreshs, view.refreshCount);
		assertForegroundPixelsAreSet(color, 0xFFFFFFFF);
	}

	@Test
	public void liftingAndDroppingPenAgain_DrawsTwoPoints() {
		final int color = 0xFF006660;
		new20x10imageWithPenForegroundColor(color);
		int previousRefreshs = view.refreshCount;

		controller.leftMouseButtonDown(0, 0);
		controller.leftMouseButtonUp(0, 0);
		controller.mouseMovedTo(3, 3);
		controller.leftMouseButtonDown(3, 3);
		controller.leftMouseButtonUp(3, 3);

		assertEquals(previousRefreshs + 2, view.refreshCount);
		assertForegroundPixelsAreSet(color, 0xFFFFFFFF, p(0, 0), p(3, 3));
	}

	@Test
	public void draggingLeftMouse_WithRectangleSelectionTool_DoesNotDraw() {
		new20x10imageWithPenForegroundColor(0xFF000000);
		int previousRefreshs = view.refreshCount;

		drawSettings.tool = Tool.RectangleSelection;
		controller.leftMouseButtonDown(0, 0);

		assertEquals(0xFFFFFFFF, controller.getImage().getRGB(0, 0));
		assertEquals(previousRefreshs, view.refreshCount);
		assertForegroundPixelsAreSet(0xFF000000, 0xFFFFFFFF);
	}

}

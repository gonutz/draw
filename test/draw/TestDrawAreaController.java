package draw;

import static org.junit.Assert.*;

import java.awt.Color;
import java.awt.Point;
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
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				assertEquals(x + " " + y, color,
						controller.getImage().getRGB(x, y));
			}
		}
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
		drawSettings.foregroundColor = new Color(color);
		drawSettings.backgroundColor = Color.white;
		drawSettings.tool = Tool.Pen;
		controller.newImage(20, 10);
		final int x = 3, y = 4;
		int previousRefreshs = view.refreshCount;

		controller.leftMouseButtonDown(x, y);
		controller.leftMouseButtonUp(x, y);

		assertEquals(color, controller.getImage().getRGB(x, y));
		assertEquals(previousRefreshs + 1, view.refreshCount);
	}

	@Test
	public void draggingPen_DrawsLine() {
		final int color = 0xFF010203;
		drawSettings.foregroundColor = new Color(color);
		drawSettings.backgroundColor = Color.white;
		drawSettings.tool = Tool.Pen;
		controller.newImage(20, 10);
		int previousRefreshs = view.refreshCount;

		controller.leftMouseButtonDown(0, 0);
		controller.mouseMovedTo(1, 3);
		controller.mouseMovedTo(3, 3);
		controller.leftMouseButtonUp(2, 3);

		HashSet<Point> setPixels = new HashSet<Point>();
		setPixels.add(new Point(0, 0));
		setPixels.add(new Point(0, 1));
		setPixels.add(new Point(1, 2));
		setPixels.add(new Point(1, 3));

		setPixels.add(new Point(2, 3));
		setPixels.add(new Point(3, 3));

		for (int x = 0; x < 20; x++)
			for (int y = 0; y < 10; y++) {
				int expectedColor = 0xFFFFFFFF;
				if (setPixels.contains(new Point(x, y)))
					expectedColor = color;
				assertEquals(x + " " + y, expectedColor, controller.getImage()
						.getRGB(x, y));
			}
		assertEquals(previousRefreshs + 3, view.refreshCount);
	}

	@Test
	public void movingMouseWithLeftButtonUp_DrawsNothing() {
		final int color = 0xFF123456;
		drawSettings.foregroundColor = new Color(color);
		drawSettings.backgroundColor = Color.white;
		drawSettings.tool = Tool.Pen;
		controller.newImage(20, 10);
		int previousRefreshs = view.refreshCount;

		controller.mouseMovedTo(0, 0);
		controller.mouseMovedTo(1, 1);

		assertEquals(0xFFFFFFFF, controller.getImage().getRGB(0, 0));
		assertEquals(0xFFFFFFFF, controller.getImage().getRGB(1, 1));
		assertEquals(previousRefreshs, view.refreshCount);
	}

	@Test
	public void liftingAndDroppingPenAgain_DrawsTwoPoints() {
		final int color = 0xFF006660;
		drawSettings.foregroundColor = new Color(color);
		drawSettings.backgroundColor = Color.white;
		drawSettings.tool = Tool.Pen;
		controller.newImage(20, 10);
		int previousRefreshs = view.refreshCount;

		controller.leftMouseButtonDown(0, 0);
		controller.leftMouseButtonUp(0, 0);
		controller.mouseMovedTo(3, 3);
		controller.leftMouseButtonDown(3, 3);
		controller.leftMouseButtonUp(3, 3);

		assertEquals(color, controller.getImage().getRGB(0, 0));
		assertEquals(color, controller.getImage().getRGB(3, 3));
		assertEquals(previousRefreshs + 2, view.refreshCount);
	}

	@Test
	public void draggingLeftMouse_WithRectangleSelectionTool_DoesNotDraw() {
		drawSettings.foregroundColor = Color.black;
		drawSettings.backgroundColor = Color.white;
		controller.newImage(20, 10);
		int previousRefreshs = view.refreshCount;

		drawSettings.tool = Tool.RectangleSelection;
		controller.leftMouseButtonDown(0, 0);

		assertEquals(0xFFFFFFFF, controller.getImage().getRGB(0, 0));
		assertEquals(previousRefreshs, view.refreshCount);
	}

}

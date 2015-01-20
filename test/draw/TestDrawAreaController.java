package draw;

import static org.junit.Assert.*;

import java.awt.Color;

import org.junit.Before;
import org.junit.Test;

public class TestDrawAreaController {

	private SpyView view;
	private DrawAreaController controller;
	private StubDrawSettings drawSettings;

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
	public void afterNewImageCreation_ViewIsRefreshed() throws Exception {
		assertEquals(0, view.refreshCount);
		controller.newImage(10, 5);
		assertEquals(1, view.refreshCount);
	}

	@Test
	public void leftClickWithPen_PaintsDotInForegroundColor() throws Exception {
		drawSettings.backgroundColor = Color.white;
		controller.newImage(20, 10);
		drawSettings.tool = Tool.Pen;
		final int color = 0xFFFFAACC;
		drawSettings.foregroundColor = new Color(color);

		final int x = 3, y = 4;

		controller.leftMouseButtonDown(x, y);
		controller.leftMouseButtonUp(x, y);
		assertEquals(color, controller.getImage().getRGB(x, y));
	}

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
}

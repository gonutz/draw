package draw;

import static org.junit.Assert.*;

import java.awt.Color;

import org.junit.Test;

public class TestDrawAreaController {

	@Test
	public void newImage_CreatesUniformColoredBufferedImage() {
		DrawAreaController c = new DrawAreaController(new SpyView());
		final int width = 20;
		final int height = 10;
		final int color = 0x0A0B0C0D;

		c.newImage(width, height, new Color(color, true));

		assertEquals(width, c.getImage().getWidth());
		assertEquals(height, c.getImage().getHeight());
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				assertEquals(x + " " + y, color, c.getImage().getRGB(x, y));
			}
		}
	}

	@Test
	public void afterNewImageCreation_ViewIsRefreshed() throws Exception {
		SpyView view = new SpyView();
		DrawAreaController c = new DrawAreaController(view);
		assertEquals(0, view.refreshCount);
		c.newImage(10, 5, Color.white);
		assertEquals(1, view.refreshCount);
	}

	private class SpyView implements DrawAreaView {
		private int refreshCount;

		public void refresh() {
			refreshCount++;
		}
	}
}

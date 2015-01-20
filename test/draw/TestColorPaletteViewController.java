package draw;

import static org.junit.Assert.*;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class TestColorPaletteViewController {

	@Test
	public void onActivate_DefaultColorPaletteIsShown() throws Exception {
		SpyView view = new SpyView();
		ColorPaletteViewController c = new ColorPaletteViewController(view,
				null);
		c.activate();
		assertEquals(c.defaultColors.length, view.setColors.size());
		for (int i = 0; i < c.defaultColors.length; i++) {
			assertEquals(i, view.setIndices.get(i).intValue());
			assertEquals(c.defaultColors[i], view.setColors.get(i));
		}
	}

	@Test
	public void settingNewColor_ShowsItInView() throws Exception {
		SpyView view = new SpyView();
		ColorPaletteViewController c = new ColorPaletteViewController(view,
				null);

		c.setPaletteEntry(5, Color.white);

		assertEquals(1, view.setColors.size());
		assertEquals(5, view.setIndices.get(0).intValue());
		assertEquals(Color.white, view.setColors.get(0));
	}

	@Test
	public void selectingColor_ShowsItInCurrentColorView() throws Exception {
		SpyCurrentColorView currentColors = new SpyCurrentColorView();
		ColorPaletteViewController c = new ColorPaletteViewController(
				new SpyView(), currentColors);
		c.setPaletteEntry(1, Color.white);
		c.setPaletteEntry(3, Color.black);

		c.selectBackgroundColor(1);
		c.selectForegroundColor(3);

		assertEquals(Color.white, currentColors.background);
		assertEquals(Color.black, currentColors.foreground);
	}

	@Test
	public void controllerKnowsCurrentForeAndBackgroundColors() {
		ColorPaletteViewController c = new ColorPaletteViewController(
				new SpyView(), new SpyCurrentColorView());
		c.setPaletteEntry(2, Color.cyan);
		c.setPaletteEntry(4, Color.yellow);

		c.selectBackgroundColor(2);
		c.selectForegroundColor(4);

		assertEquals(Color.cyan, c.getBackgroundColor());
		assertEquals(Color.yellow, c.getForegroundColor());
	}

	private class SpyView implements ColorPaletteView {
		private List<Integer> setIndices = new ArrayList<Integer>();
		private List<Color> setColors = new ArrayList<Color>();

		@Override
		public void setColor(int index, Color color) {
			setIndices.add(index);
			setColors.add(color);
		}
	}

	private class SpyCurrentColorView implements CurrentColorsView {
		private Color background;
		private Color foreground;

		@Override
		public void setBackgroundColor(Color color) {
			background = color;
		}

		@Override
		public void setForegroundColor(Color color) {
			foreground = color;
		}
	}

}

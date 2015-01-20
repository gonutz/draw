package draw;

import static org.junit.Assert.*;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class TestColorPaletteViewController {

	private SpyView view;
	private SpyCurrentColorView currentColorView;
	private ColorPaletteViewController controller;

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

	@Before
	public void setup() {
		view = new SpyView();
		currentColorView = new SpyCurrentColorView();
		controller = new ColorPaletteViewController(view, currentColorView);
	}

	@Test
	public void onActivate_DefaultColorPaletteIsShown() throws Exception {
		controller.activate();

		assertEquals(controller.defaultColors.length, view.setColors.size());
		for (int i = 0; i < controller.defaultColors.length; i++) {
			assertEquals(i, view.setIndices.get(i).intValue());
			assertEquals(controller.defaultColors[i], view.setColors.get(i));
		}
	}

	@Test
	public void settingNewColor_ShowsItInView() throws Exception {
		controller.setPaletteEntry(5, Color.white);

		assertEquals(1, view.setColors.size());
		assertEquals(5, view.setIndices.get(0).intValue());
		assertEquals(Color.white, view.setColors.get(0));
	}

	@Test
	public void selectingColor_ShowsItInCurrentColorView() throws Exception {
		controller.setPaletteEntry(1, Color.white);
		controller.setPaletteEntry(3, Color.black);

		controller.selectBackgroundColor(1);
		controller.selectForegroundColor(3);

		assertEquals(Color.white, currentColorView.background);
		assertEquals(Color.black, currentColorView.foreground);
	}

	@Test
	public void controllerKnowsCurrentForeAndBackgroundColors() {
		controller.setPaletteEntry(2, Color.cyan);
		controller.setPaletteEntry(4, Color.yellow);

		controller.selectBackgroundColor(2);
		controller.selectForegroundColor(4);

		assertEquals(Color.cyan, controller.getBackgroundColor());
		assertEquals(Color.yellow, controller.getForegroundColor());
	}

	@Test
	public void defaultForeAndBackgroundColorsAreBlackAndWhite()
			throws Exception {
		assertEquals(Color.white, controller.getBackgroundColor());
		assertEquals(Color.black, controller.getForegroundColor());
	}

}

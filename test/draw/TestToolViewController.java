package draw;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestToolViewController {

	@Test
	public void changingSelectedTool_UpdatesView() throws Exception {
		SpyView view = new SpyView();
		ToolViewController c = new ToolViewController(view);
		for (Tool tool : Tool.values()) {
			c.selectTool(tool);
			assertEquals(tool, view.selection);
		}
	}

	@Test
	public void onShow_RectangleSelectToolIsActive() throws Exception {
		SpyView view = new SpyView();
		ToolViewController c = new ToolViewController(view);
		c.viewActivated();
		assertEquals(Tool.RectangleSelection, view.selection);
	}

	private class SpyView implements ToolView {
		private Tool selection;

		@Override
		public void setSelection(Tool selected) {
			selection = selected;
		}
	}
}

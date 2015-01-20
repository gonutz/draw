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

	@Test
	public void controllerKnowsTheCurrentlySelectedTool() throws Exception {
		ToolViewController c = new ToolViewController(new SpyView());
		c.selectTool(Tool.Pen);
		assertEquals(Tool.Pen, c.getSelectedTool());
	}

	private class SpyView implements ToolView {
		private Tool selection;

		@Override
		public void setSelection(Tool selected) {
			selection = selected;
		}
	}
}

package draw;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestToolViewController {

	private class SpyView implements ToolView {
		private Tool selection;

		@Override
		public void setSelection(Tool selected) {
			selection = selected;
		}
	}

	@Test
	public void changingSelectedTool_UpdatesView() {
		SpyView view = new SpyView();
		ToolViewController c = new ToolViewController(view);
		for (Tool tool : Tool.values()) {
			c.selectTool(tool);
			assertEquals(tool, view.selection);
		}
	}

	@Test
	public void onShow_RectangleSelectToolIsActive() {
		SpyView view = new SpyView();
		ToolViewController c = new ToolViewController(view);
		c.viewActivated();
		assertEquals(Tool.RectangleSelection, view.selection);
	}

	@Test
	public void controllerKnowsTheCurrentlySelectedTool() {
		ToolViewController c = new ToolViewController(new SpyView());
		c.selectTool(Tool.Pen);
		assertEquals(Tool.Pen, c.getSelectedTool());
	}

	@Test
	public void defaultToolIsRectangleSelection() {
		assertEquals(Tool.RectangleSelection,
				new ToolViewController(null).getSelectedTool());
	}
}

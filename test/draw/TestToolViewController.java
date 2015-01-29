package draw;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class TestToolViewController {

	private class SpyView implements ToolView {
		private Tool selection;

		@Override
		public void setSelection(Tool selected) {
			selection = selected;
		}
	}

	@Before
	public void setup() {
		view = new SpyView();
		controller = new ToolViewController(view);
	}

	private SpyView view;
	private ToolViewController controller;

	@Test
	public void changingSelectedTool_UpdatesView() {
		for (Tool tool : Tool.values()) {
			controller.selectTool(tool);
			assertEquals(tool, view.selection);
		}
	}

	@Test
	public void onShow_RectangleSelectToolIsActive() {
		controller.viewActivated();
		assertEquals(Tool.RectangleSelection, view.selection);
	}

	@Test
	public void controllerKnowsTheCurrentlySelectedTool() {
		controller.selectTool(Tool.Pen);
		assertEquals(Tool.Pen, controller.getSelectedTool());
	}

	@Test
	public void defaultToolIsRectangleSelection() {
		assertEquals(Tool.RectangleSelection,
				new ToolViewController(null).getSelectedTool());
	}

	@Test
	public void selectingToolNotifiesObserverIfAny() {
		SpyObserver o = new SpyObserver();
		controller.setObserver(o);

		controller.selectTool(Tool.Pen);
		assertEquals(Tool.Pen, o.tool);
		assertEquals(1, o.changes);

		controller.selectTool(Tool.Pen);
		assertEquals(Tool.Pen, o.tool);
		assertEquals(2, o.changes);

		controller.selectTool(Tool.Fill);
		assertEquals(Tool.Fill, o.tool);
		assertEquals(3, o.changes);
	}

	private class SpyObserver implements ToolChangeObserver {
		Tool tool;
		int changes;

		@Override
		public void toolChangedTo(Tool tool) {
			this.tool = tool;
			changes++;
		}
	}
}

package draw;

public class ToolViewController {

	private ToolView view;
	private Tool selected = Tool.RectangleSelection;

	public ToolViewController(ToolView view) {
		this.view = view;
	}

	public void selectTool(Tool tool) {
		selected = tool;
		view.setSelection(tool);
	}

	public void viewActivated() {
		view.setSelection(Tool.RectangleSelection);
	}

	public Tool getSelectedTool() {
		return selected;
	}

}

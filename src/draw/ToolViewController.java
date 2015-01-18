package draw;

public class ToolViewController {

	private ToolView view;

	public ToolViewController(ToolView view) {
		this.view = view;
	}

	public void selectTool(Tool tool) {
		view.setSelection(tool);
	}

	public void viewActivated() {
		view.setSelection(Tool.RectangleSelection);
	}

}

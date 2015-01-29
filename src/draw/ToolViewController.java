package draw;

public class ToolViewController {

	private ToolView view;
	private Tool selected = Tool.RectangleSelection;
	private ToolChangeObserver observer;

	public ToolViewController(ToolView view) {
		this.view = view;
	}

	public void selectTool(Tool tool) {
		selected = tool;
		view.setSelection(tool);
		if (observer != null)
			observer.toolChangedTo(tool);
	}

	public void viewActivated() {
		view.setSelection(Tool.RectangleSelection);
	}

	public Tool getSelectedTool() {
		return selected;
	}

	public void setObserver(ToolChangeObserver o) {
		this.observer = o;
	}

}

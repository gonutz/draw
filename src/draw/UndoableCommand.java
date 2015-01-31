package draw;

public interface UndoableCommand {

	void undoTo(ImageKeeper keeper, ToolController toolController);

	public void doTo(ImageKeeper keeper);

}

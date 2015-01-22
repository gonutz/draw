package draw;

public interface UndoableCommand {

	void undoTo(ImageKeeper image);

	public void doTo(ImageKeeper image);

}

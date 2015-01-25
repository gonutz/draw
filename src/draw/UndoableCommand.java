package draw;

public interface UndoableCommand {

	void undoTo(ImageKeeper keeper);

	public void doTo(ImageKeeper keeper);

}

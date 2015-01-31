package draw;

import java.util.ArrayList;
import java.util.List;

public class UndoHistory {

	private List<UndoableCommand> undoList = new ArrayList<UndoableCommand>();
	private int undoIndex = -1;

	/**
	 * Remembers the given command at the current point in the history. If you
	 * add several commands, then undo some of them, then add a new command, the
	 * previously undone commands are forgotten.
	 */
	public void addCommand(UndoableCommand command) {
		for (int i = undoList.size() - 1; i > undoIndex; i--)
			undoList.remove(i);
		undoList.add(command);
		undoIndex = undoList.size() - 1;
	}

	/**
	 * Undoes the next command in the history if there is any.
	 * 
	 * @return true if there was a command undone and false if not (if the
	 *         history is empty).
	 */
	public boolean undoTo(ImageKeeper image, ToolController c) {
		if (nothingToUndo())
			return false;
		undoList.get(undoIndex).undoTo(image, c);
		undoIndex--;
		return true;
	}

	private boolean nothingToUndo() {
		return undoIndex < 0;
	}

	/**
	 * Re-does the last thing undone if there is one.
	 * 
	 * @return true if something was re-done and false if not (if nothing was
	 *         undone).
	 */
	public boolean redoTo(ImageKeeper image) {
		if (nothingToRedo())
			return false;
		undoIndex++;
		undoList.get(undoIndex).doTo(image);
		return true;
	}

	private boolean nothingToRedo() {
		return undoIndex + 1 >= undoList.size();
	}

}

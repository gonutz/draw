package draw;

import java.util.ArrayList;
import java.util.List;

public class UndoHistory {

	private List<UndoableCommand> undoList = new ArrayList<UndoableCommand>();
	private int undoIndex = -1;

	public void addCommand(UndoableCommand command) {
		for (int i = undoList.size() - 1; i > undoIndex; i--)
			undoList.remove(i);
		undoList.add(command);
		undoIndex = undoList.size() - 1;
	}

	public boolean undoTo(ImageKeeper image) {
		if (nothingToUndo())
			return false;
		undoList.get(undoIndex).undoTo(image);
		undoIndex--;
		return true;
	}

	private boolean nothingToUndo() {
		return undoIndex < 0;
	}

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

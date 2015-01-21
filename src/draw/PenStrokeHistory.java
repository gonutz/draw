package draw;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class PenStrokeHistory {

	private List<PenStroke> undoList = new ArrayList<PenStroke>();
	private int undoIndex = -1;

	public PenStroke startNewStroke() {
		for (int i = undoList.size() - 1; i > undoIndex; i--)
			undoList.remove(i);
		undoList.add(new PenStroke());
		undoIndex = undoList.size() - 1;
		return undoList.get(undoIndex);
	}

	public boolean undoTo(BufferedImage image) {
		if (nothingToUndo())
			return false;
		undoList.get(undoIndex).undoTo(image);
		undoIndex--;
		return true;
	}

	private boolean nothingToUndo() {
		return undoIndex < 0;
	}

	public boolean redoTo(BufferedImage image) {
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

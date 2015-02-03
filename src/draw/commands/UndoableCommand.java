package draw.commands;

import draw.ImageKeeper;
import draw.ToolController;

public interface UndoableCommand {

	void undoTo(ImageKeeper keeper, ToolController toolController);

	public void doTo(ImageKeeper keeper, ToolController toolController);

}

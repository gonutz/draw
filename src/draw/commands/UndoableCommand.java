package draw.commands;

import draw.ImageKeeper;
import draw.ToolController;

public interface UndoableCommand {

	void undoTo(ImageKeeper keeper, ToolController toolController);

	public void doTo(ImageKeeper keeper, ToolController toolController);

	/**
	 * @return true if the command changed the image in any way. Returns false
	 *         if for example a pixel was set from white to white with a pen or
	 *         filling an area with the same color. These actions have no real
	 *         effect since they do not change any colors.
	 */
	boolean hasAnyEffect();

}

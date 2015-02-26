package draw.commands;

import draw.UndoContext;

public interface UndoableCommand {

	void undoTo(UndoContext keeper);

	public void doTo(UndoContext keeper);

	/**
	 * @return true if the command changed the image in any way. Returns false
	 *         if for example a pixel was set from white to white with a pen or
	 *         filling an area with the same color. These actions have no real
	 *         effect since they do not change any colors.
	 */
	boolean hasAnyEffect();

}

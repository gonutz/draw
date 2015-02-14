package draw;

public interface DrawAreaView {

	/**
	 * Tells the view that something has changed and it must be repainted to
	 * reflect the changes.
	 */
	void refresh();

	/**
	 * Sets the area currently selected by the user, see the rectangle selection
	 * tool. Set to null if no selection is active or to remove the current
	 * selection.
	 */
	void setSelection(Rectangle selection);

	/**
	 * @return the coordinates of the top-left point currently visible in the
	 *         view.
	 */
	Point getVisibleTopLeftCorner();

	static class Point {
		int x, y;
	}

}

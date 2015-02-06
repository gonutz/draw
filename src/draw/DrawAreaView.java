package draw;

import java.awt.image.BufferedImage;

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
	 * Sets an image that is to be drawn on top of the current image, it floats
	 * above the image. The area to draw it in is the currently set selection
	 * area so the image must match the size of that.
	 */
	void setFloatingImage(BufferedImage image);

}

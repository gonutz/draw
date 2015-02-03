package draw;

public interface SaveFileDialog {
	/**
	 * Presents the user with a dialog to choose a new file name.
	 * 
	 * @return true if the user accepts the new file name, false if the user
	 *         aborts the operation. Only if the user confirms, getFileName
	 *         returns a valid file name.
	 */
	boolean askUserForFileName();

	/**
	 * @return If the user accepted the last file action, this contains the
	 *         chosen file name.
	 */
	String getFileName();
}

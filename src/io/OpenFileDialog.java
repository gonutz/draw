package io;

public interface OpenFileDialog {
	/**
	 * Presents the user with a dialog to choose an existing file name.
	 * 
	 * @return true if the user accepts the file name, false if the user aborts
	 *         the operation. Only if the user confirms, getFileName returns a
	 *         valid file name.
	 */
	boolean askUserForOpenFileName();

	/**
	 * @return If the user accepted the last file action, this contains the
	 *         chosen file name.
	 */
	String getOpenFileName();
}

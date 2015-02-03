package draw;

public interface CurrentFileNameObserver {

	/**
	 * Called when the file name changes, e.g. when the user saves the image
	 * under a new path or when an image was loaded.
	 * 
	 * The file name is null if a new image was created, that means that there
	 * is no file name currently associated to the image.
	 */
	void currentFileNameChangedTo(String fileName);

}

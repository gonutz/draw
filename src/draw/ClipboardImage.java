package draw;

import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;

/**
 * read from clipboard:
 * 
 * try
 * 
 * if (t != null && t.isDataFlavorSupported(DataFlavor.imageFlavor))
 * 
 * Image image = (Image)t.getTransferData(DataFlavor.imageFlavor);
 * 
 * catch (Exception e) { image = null; }
 */
public class ClipboardImage implements Transferable {
	private Image image;

	public ClipboardImage(Image image) {
		this.image = image;
	}

	public Object getTransferData(DataFlavor flavor)
			throws UnsupportedFlavorException {
		if (isDataFlavorSupported(flavor)) {
			return image;
		} else {
			throw new UnsupportedFlavorException(flavor);
		}
	}

	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return flavor == DataFlavor.imageFlavor;
	}

	public DataFlavor[] getTransferDataFlavors() {
		return new DataFlavor[] { DataFlavor.imageFlavor };
	}
}
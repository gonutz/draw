package draw;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.image.BufferedImage;

public class SystemClipboard implements Clipboard {

	@Override
	public void storeImage(BufferedImage image) {
		Toolkit.getDefaultToolkit().getSystemClipboard()
				.setContents(new ClipboardImage(image), null);
	}

	@Override
	public Image getImage() {
		Transferable t = Toolkit.getDefaultToolkit().getSystemClipboard()
				.getContents(null);
		try {
			if (t != null && t.isDataFlavorSupported(DataFlavor.imageFlavor))
				return (Image) t.getTransferData(DataFlavor.imageFlavor);
		} catch (Exception e) {
		}
		return null;
	}
}

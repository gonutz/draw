package draw;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.image.BufferedImage;

public class SystemClipboard implements Clipboard {

	@Override
	public void storeImage(BufferedImage image) {
		ClipboardImage clipboardImage = new ClipboardImage(
				ImageUtils.copyImage(image));
		Toolkit.getDefaultToolkit().getSystemClipboard()
				.setContents(clipboardImage, null);
	}

	@Override
	public BufferedImage getImage() {
		Transferable t = Toolkit.getDefaultToolkit().getSystemClipboard()
				.getContents(null);
		try {
			if (t != null && t.isDataFlavorSupported(DataFlavor.imageFlavor))
				return makeImage(t.getTransferData(DataFlavor.imageFlavor));
		} catch (Exception e) {
		}
		return null;
	}

	private BufferedImage makeImage(Object data) {
		if (data instanceof BufferedImage)
			return (BufferedImage) data;
		if (data instanceof Image)
			return copyToBufferedImage((Image) data);
		return null;
	}

	private BufferedImage copyToBufferedImage(Image image) {
		BufferedImage copy = new BufferedImage(image.getWidth(null),
				image.getHeight(null), BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g = copy.createGraphics();
		g.drawImage(image, 0, 0, null);
		g.dispose();
		return copy;
	}
}

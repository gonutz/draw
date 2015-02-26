package draw.commands;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import draw.UndoContext;
import draw.Rectangle;
import draw.SelectionKeeper;

public class DeleteSelectionCommand implements UndoableCommand {

	private Rectangle selection;
	private Color clearColor;
	private SelectionKeeper selector;
	private BufferedImage deleted;

	public DeleteSelectionCommand(BufferedImage image, Rectangle selection,
			Color clearColor, SelectionKeeper selector) {
		copyDeletedImage(image, selection);
		this.selection = selection.copy();
		this.clearColor = clearColor;
		this.selector = selector;
	}

	private void copyDeletedImage(BufferedImage image, Rectangle selection) {
		int x = selection.left();
		int y = selection.top();
		int w = selection.width();
		int h = selection.height();
		this.deleted = new BufferedImage(w, h, image.getType());
		Graphics g = deleted.getGraphics();
		g.drawImage(image, 0, 0, w, h, x, y, x + w, y + h, null);
	}

	@Override
	public void undoTo(UndoContext context) {
		Graphics g = context.getImage().getGraphics();
		g.drawImage(deleted, selection.left(), selection.top(),
				selection.width(), selection.height(), null);
		selector.setSelection(selection.copy());
	}

	@Override
	public void doTo(UndoContext context) {
		Graphics2D g = (Graphics2D) context.getImage().getGraphics();
		g.setBackground(clearColor);
		Rectangle r = selection;
		g.clearRect(r.left(), r.top(), r.width(), r.height());
		selector.setSelection(null);
	}

	@Override
	public boolean hasAnyEffect() {
		return true;
	}

}

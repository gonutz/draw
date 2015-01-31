package draw.commands;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import draw.ImageKeeper;
import draw.Rectangle;
import draw.SelectionKeeper;
import draw.Tool;
import draw.ToolController;
import draw.UndoableCommand;

public class SelectionMovement implements UndoableCommand {

	private Color backgroundColor;
	private Rectangle original;
	private Rectangle selection;
	private BufferedImage foreground;
	private BufferedImage background;
	private SelectionKeeper selectionKeeper;

	public SelectionMovement(BufferedImage image, Rectangle selection,
			Color background, SelectionKeeper selectionKeeper) {
		this.selectionKeeper = selectionKeeper;
		backgroundColor = background;
		this.original = selection.copy();
		this.selection = selection.copy();
		copyImageWithSelectionSetToBackgroundColor(image);
		copySelectedImageArea(image);
	}

	private void copyImageWithSelectionSetToBackgroundColor(BufferedImage image) {
		background = new BufferedImage(image.getWidth(), image.getHeight(),
				image.getType());
		Graphics2D g = (Graphics2D) background.getGraphics();
		g.drawImage(image, 0, 0, null);
		g.setBackground(backgroundColor);
		g.clearRect(selection.left(), selection.top(), selection.width(),
				selection.height());
	}

	private void copySelectedImageArea(BufferedImage image) {
		int x = selection.left();
		int y = selection.top();
		int w = selection.width();
		int h = selection.height();
		foreground = new BufferedImage(w, h, image.getType());
		foreground.getGraphics().drawImage(image, 0, 0, w, h, x, y, x + w,
				y + h, null);
	}

	public void moveBy(int dx, int dy) {
		selection.moveBy(dx, dy);
		selectionKeeper.setSelection(selection);
	}

	public void drawCompositeTo(Graphics g) {
		drawAreaTo(g, selection);
	}

	private void drawAreaTo(Graphics g, Rectangle area) {
		g.drawImage(background, 0, 0, null);
		g.drawImage(foreground, area.left(), area.top(), null);
	}

	@Override
	public void undoTo(ImageKeeper keeper, ToolController toolController) {
		toolController.selectTool(Tool.RectangleSelection);
		setImageAndSelectionTo(keeper, original);
	}

	@Override
	public void doTo(ImageKeeper keeper, ToolController toolController) {
		toolController.selectTool(Tool.RectangleSelection);
		setImageAndSelectionTo(keeper, selection);
	}

	private void setImageAndSelectionTo(ImageKeeper keeper, Rectangle area) {
		Graphics g = keeper.getImage().getGraphics();
		drawAreaTo(g, area);
		selectionKeeper.setSelection(area.copy());
	}
}

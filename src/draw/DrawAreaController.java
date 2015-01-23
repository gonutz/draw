package draw;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class DrawAreaController implements ImageProvider, ImageKeeper {

	private DrawAreaView view;
	private DrawSettings drawSettings;
	private BufferedImage image;
	private Color drawColor;
	private int lastX;
	private int lastY;
	private boolean penDown;
	private UndoHistory history = new UndoHistory();
	private PenStroke currentStroke;

	public DrawAreaController(DrawAreaView view) {
		this.view = view;
	}

	public void setDrawSettings(DrawSettings drawSettings) {
		this.drawSettings = drawSettings;
	}

	public BufferedImage getImage() {
		return image;
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}

	public void newImage(int width, int height) {
		NewImageCommand newImageCommand = new NewImageCommand(image, width,
				height, drawSettings.getBackgroundColor());
		makeUndoableIfThisIsNotTheVeryFirstImage(newImageCommand);
		newImageCommand.doTo(this);
		view.refresh();
	}

	private void makeUndoableIfThisIsNotTheVeryFirstImage(NewImageCommand c) {
		if (image != null) {
			history.addCommand(c);
		}
	}

	public void leftMouseButtonDown(int x, int y) {
		drawColor = drawSettings.getForegroundColor();
		mouseDown(x, y);
	}

	public void rightMouseButtonDown(int x, int y) {
		drawColor = drawSettings.getBackgroundColor();
		mouseDown(x, y);
	}

	private void mouseDown(int x, int y) {
		if (drawSettings.getCurrentTool() == Tool.Pen) {
			currentStroke = new PenStroke(drawColor);
			history.addCommand(currentStroke);
			currentStroke.addLine(image, x, y, x, y);
			lastX = x;
			lastY = y;
			penDown = true;
			view.refresh();
		}
	}

	public void leftMouseButtonUp() {
		penDown = false;
	}

	public void rightMouseButtonUp() {
		penDown = false;
	}

	public void mouseMovedTo(int x, int y) {
		if (penDown) {
			currentStroke.addLine(image, lastX, lastY, x, y);
			view.refresh();
		}
		lastX = x;
		lastY = y;
	}

	public void undoLastAction() {
		if (history.undoTo(this))
			view.refresh();
	}

	public void redoPreviousAction() {
		if (history.redoTo(this))
			view.refresh();
	}

}

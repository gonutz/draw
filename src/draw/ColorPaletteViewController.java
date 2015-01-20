package draw;

import java.awt.Color;

public class ColorPaletteViewController {

	private ColorPaletteView view;
	private CurrentColorsView current;
	public Color[] defaultColors = { Color.black, Color.gray, Color.red,
			Color.orange, Color.yellow, Color.green, Color.cyan, Color.blue,
			Color.magenta, Color.pink, Color.white, Color.lightGray,
			new Color(128, 0, 0), new Color(128, 64, 0),
			new Color(128, 128, 0), new Color(0, 128, 0),
			new Color(0, 128, 128), new Color(0, 0, 128),
			new Color(128, 0, 128), new Color(128, 128, 255) };
	private Color backgroundColor = Color.white;
	private Color foregroundColor = Color.black;

	public ColorPaletteViewController(ColorPaletteView view,
			CurrentColorsView current) {
		this.view = view;
		this.current = current;
	}

	public void activate() {
		for (int i = 0; i < defaultColors.length; i++) {
			view.setColor(i, defaultColors[i]);
		}
	}

	public void selectForegroundColor(int index) {
		foregroundColor = defaultColors[index];
		current.setForegroundColor(foregroundColor);
	}

	public void selectBackgroundColor(int index) {
		backgroundColor = defaultColors[index];
		current.setBackgroundColor(backgroundColor);
	}

	public void setPaletteEntry(int index, Color color) {
		view.setColor(index, color);
		defaultColors[index] = color;
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public Color getForegroundColor() {
		return foregroundColor;
	}
}

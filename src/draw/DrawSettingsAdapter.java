package draw;

import java.awt.Color;

public class DrawSettingsAdapter implements DrawSettings {

	private ColorPaletteViewController colors;
	private ToolViewController tools;

	public DrawSettingsAdapter(ColorPaletteViewController colors,
			ToolViewController tools) {
		this.colors = colors;
		this.tools = tools;
	}

	@Override
	public Color getForegroundColor() {
		return colors.getForegroundColor();
	}

	@Override
	public Color getBackgroundColor() {
		return colors.getBackgroundColor();
	}

	@Override
	public Tool getCurrentTool() {
		return tools.getSelectedTool();
	}
}

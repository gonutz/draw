package commands;

import java.awt.Color;
import java.awt.image.BufferedImage;

import org.junit.Test;

import draw.commands.FillCommand;

public class TestFillCommand {

	private BufferedImage image;
	private Color color;

	@Test
	public void UniformColoredImage_IsCompletelyChanged() {
		new FillCommand(image, 0, 0, color);
	}
}

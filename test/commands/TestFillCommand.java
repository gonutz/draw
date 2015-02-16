package commands;

import static org.junit.Assert.*;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import org.junit.Test;

import draw.ImageKeeper;
import draw.Tool;
import draw.ToolController;
import draw.commands.FillCommand;

public class TestFillCommand implements ImageKeeper {

	private BufferedImage image;

	@Override
	public BufferedImage getImage() {
		return image;
	}

	@Override
	public void setImage(BufferedImage image) {
		this.image = image;
	}

	private void newImage(String... pixels) {
		int width = pixels[0].length();
		int height = pixels.length;
		image = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics g = image.getGraphics();
		for (int y = 0; y < height; y++)
			for (int x = 0; x < width; x++) {
				char p = pixels[y].charAt(x);
				if (p == '.')
					g.setColor(Color.white);
				else if (p == 'x')
					g.setColor(Color.black);
				else
					g.setColor(Color.red);
				g.drawLine(x, y, x, y);
			}
	}

	private void checkImage(String... pixels) {
		for (int y = 0; y < image.getHeight(); y++)
			for (int x = 0; x < image.getWidth(); x++) {
				char p = pixels[y].charAt(x);
				Color expected = Color.red;
				if (p == '.')
					expected = Color.white;
				else if (p == 'x')
					expected = Color.black;
				if (image.getRGB(x, y) != expected.getRGB())
					fail("wrong pixel at " + x + "|" + y);
			}
	}

	private class DummyToolController implements ToolController {
		public Tool getSelectedTool() {
			return null;
		}

		public void selectTool(Tool tool) {
		}
	}

	@Test
	public void UniformColoredImage_IsCompletelyChanged() {
		newImage(//
				"...",//
				"...");
		checkImageFilledAt(0, 0,//
				"xxx",//
				"xxx");

	}

	private void checkImageFilledAt(int x, int y, String... pixels) {
		new FillCommand(x, y, Color.black)
				.doTo(this, new DummyToolController());
		checkImage(pixels);
	}

	@Test
	public void onlyFourNeighborhoodIsFilled() {
		newImage(//
				".x.",//
				"x..");
		checkImageFilledAt(0, 0,//
				"xx.",//
				"x..");
	}

	@Test
	public void sameColorFill_DoesNothing() {
		newImage("..x..");
		checkImageFilledAt(2, 0, "..x..");
	}

	@Test
	public void complexFill() {
		newImage(// v
				".o...",//
				"o..o.",//
				"oo..o",// <
				".o.o.",//
				"...o.");
		checkImageFilledAt(3, 2,//
				".oxxx",//
				"oxxox",//
				"ooxxo",//
				"xoxo.",//
				"xxxo.");
	}

	@Test
	public void undoingFill_ResetsPixels() {
		newImage(// v
				".o...",//
				"o..o.",//
				"oo..o",// <
				".o.o.",//
				"...o.");

		FillCommand fill = new FillCommand(3, 2, Color.black);
		fill.doTo(this, new DummyToolController());
		fill.undoTo(this, new DummyToolController());

		checkImage(//
				".o...",//
				"o..o.",//
				"oo..o",//
				".o.o.",//
				"...o.");
	}
}

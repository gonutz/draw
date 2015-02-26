package draw;

import java.awt.image.BufferedImage;

public interface UndoContext {

	BufferedImage getImage();

	void setImage(BufferedImage image);

	void selectTool(Tool tool);

}

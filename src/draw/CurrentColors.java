package draw;

import java.awt.Dimension;

import javax.swing.JPanel;
import java.awt.Color;
import javax.swing.border.BevelBorder;

public class CurrentColors extends JPanel implements CurrentColorsView {

	private static final long serialVersionUID = 1L;
	private JPanel foreground;
	private JPanel background;

	public CurrentColors() {
		setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		setPreferredSize(new Dimension(61, 61));
		setLayout(null);

		foreground = new JPanel();
		foreground.setBackground(Color.BLACK);
		foreground.setBounds(7, 7, 30, 30);
		add(foreground);

		background = new JPanel();
		background.setBackground(Color.WHITE);
		background.setPreferredSize(new Dimension(30, 30));
		background.setBounds(23, 23, 30, 30);
		add(background);
	}

	public void setBackgroundColor(Color color) {
		background.setBackground(color);
		foreground.repaint();
	}

	public void setForegroundColor(Color color) {
		foreground.setBackground(color);
	}
}

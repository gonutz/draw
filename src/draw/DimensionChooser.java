package draw;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import java.awt.Component;
import javax.swing.Box;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class DimensionChooser extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private boolean accepted = false;
	private JSpinner width;
	private JSpinner height;

	public void askUserForDimensions() {
		setModal(true);
		setVisible(true);
	}

	public boolean wasAccepted() {
		return accepted;
	}

	public int getWidth() {
		return (int) width.getValue();
	}

	public int getHeight() {
		return (int) height.getValue();
	}

	public DimensionChooser() {
		setTitle("Canvas Size");
		setBounds(100, 100, 330, 110);
		setResizable(false);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setLayout(new FlowLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);

		JLabel widthLabel = new JLabel("Width");
		contentPanel.add(widthLabel);

		width = new JSpinner();
		width.setModel(new SpinnerNumberModel(1, 1, 100000, 1));
		contentPanel.add(width);

		Component horizontalStrut = Box.createHorizontalStrut(20);
		contentPanel.add(horizontalStrut);

		JLabel heightLabel = new JLabel("Height");
		contentPanel.add(heightLabel);

		height = new JSpinner();
		height.setModel(new SpinnerNumberModel(1, 1, 100000, 1));
		contentPanel.add(height);

		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);

		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				accepted = true;
				DimensionChooser.this.setVisible(false);
			}
		});
		okButton.setActionCommand("OK");
		buttonPane.add(okButton);
		getRootPane().setDefaultButton(okButton);

		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				accepted = false;
				DimensionChooser.this.setVisible(false);
			}
		});
		cancelButton.setActionCommand("Cancel");
		buttonPane.add(cancelButton);
	}
}

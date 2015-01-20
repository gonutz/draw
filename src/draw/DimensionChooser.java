package draw;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.Component;

import javax.swing.Box;

public class DimensionChooser extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private boolean accepted = false;
	private JSpinner width;
	private JSpinner height;

	public boolean wasAccepted() {
		return accepted;
	}

	public int getCanvasWidth() {
		return (int) width.getValue();
	}

	public int getCanvasHeight() {
		return (int) height.getValue();
	}

	public DimensionChooser() {
		setModal(true);
		setTitle("Canvas Size");
		setBounds(100, 100, 450, 130);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setLayout(new FlowLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		{
			JLabel lblWidth = new JLabel("Width");
			contentPanel.add(lblWidth);
		}
		{
			width = new JSpinner();
			width.setModel(new SpinnerNumberModel(1, 1, 99999, 1));
			contentPanel.add(width);
		}
		{
			Component horizontalStrut = Box.createHorizontalStrut(10);
			contentPanel.add(horizontalStrut);
		}
		{
			JLabel lblHeight = new JLabel("Height");
			contentPanel.add(lblHeight);
		}
		{
			height = new JSpinner();
			height.setModel(new SpinnerNumberModel(1, 1, 99999, 1));
			contentPanel.add(height);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
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
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						DimensionChooser.this.setVisible(false);
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
		makeDialogCloseOnEscape();
		makeDialogAcceptOnReturn();
	}

	private void makeDialogCloseOnEscape() {
		getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
				KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "close");
		getRootPane().getActionMap().put("close", new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				DimensionChooser.this.setVisible(false);
			}
		});
	}

	private void makeDialogAcceptOnReturn() {
		getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
				KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "accept");
		getRootPane().getActionMap().put("accept", new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				accepted = true;
				DimensionChooser.this.setVisible(false);
			}
		});
	}
}

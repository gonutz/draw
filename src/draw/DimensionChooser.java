package draw;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;

public class DimensionChooser extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private boolean accepted = false;
	private JTextField width;
	private JTextField height;

	public boolean wasAccepted() {
		return accepted;
	}

	public int getCanvasWidth() {
		return Integer.parseInt(width.getText());
	}

	public int getCanvasHeight() {
		return Integer.parseInt(height.getText());
	}

	public DimensionChooser(int initialWidth, int initialHeight) {
		setModal(true);
		setTitle("Canvas Size");
		setBounds(100, 100, 453, 121);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setLayout(new FlowLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		{
			JLabel lblWidth = new JLabel("Width");
			contentPanel.add(lblWidth);
		}
		{
			width = new JTextField();
			width.setText("" + initialWidth);
			width.addFocusListener(new FocusAdapter() {
				@Override
				public void focusGained(FocusEvent e) {
					width.selectAll();
				}
			});
			contentPanel.add(width);
			width.setColumns(10);
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
			height = new JTextField();
			height.setText("" + initialHeight);
			height.addFocusListener(new FocusAdapter() {
				@Override
				public void focusGained(FocusEvent e) {
					height.selectAll();
				}
			});
			contentPanel.add(height);
			height.setColumns(10);
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
				accepted = false;
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

	@Override
	public void setVisible(boolean b) {
		boolean widthOk = ensureTextIsValidNumber(width);
		boolean heightOK = ensureTextIsValidNumber(height);
		if (widthOk && heightOK) {
			super.setVisible(b);
			width.requestFocus();
		}
	}

	private boolean ensureTextIsValidNumber(JTextField text) {
		try {
			int n = Integer.parseInt(text.getText());
			final int maxSize = 5000;
			if (n < 1) {
				text.setText("1");
				return false;
			}
			if (n > maxSize) {
				text.setText("" + maxSize);
				return false;
			}
			return true;
		} catch (NumberFormatException e) {
			text.setText("100");
			return false;
		}
	}
}

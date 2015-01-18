package draw;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.border.BevelBorder;

public class MainWindow implements ToolView {

	private JFrame frmDraw;
	private JButton rectangleSelection;
	private JButton penSelection;
	private JButton fillSelection;
	private JButton colorPickSelection;
	private JButton lineSelection;
	private JButton eraseSelection;
	private ToolViewController toolViewController;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow window = new MainWindow();
					window.setToolViewController(new ToolViewController(window));
					window.frmDraw.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void setToolViewController(ToolViewController c) {
		toolViewController = c;
	}

	/**
	 * Create the application.
	 */
	public MainWindow() {
		initialize();
		addGlobalKeyListener();
	}

	private void addGlobalKeyListener() {
		KeyboardFocusManager manager = KeyboardFocusManager
				.getCurrentKeyboardFocusManager();
		manager.addKeyEventDispatcher(new KeyEventDispatcher() {
			@Override
			public boolean dispatchKeyEvent(KeyEvent e) {
				globalKeyEvent(e);
				// Allow the event to be re-dispatched
				return false;
			}
		});
	}

	private void globalKeyEvent(KeyEvent e) {
		if (e.getID() == KeyEvent.KEY_TYPED)
			switch (e.getKeyChar()) {
			case 's':
				toolViewController.selectTool(Tool.RectangleSelection);
				break;
			case 'p':
				toolViewController.selectTool(Tool.Pen);
				break;
			case 'f':
				toolViewController.selectTool(Tool.Fill);
				break;
			case 'e':
				toolViewController.selectTool(Tool.Eraser);
				break;
			case 'l':
				toolViewController.selectTool(Tool.Line);
				break;
			case 'c':
				toolViewController.selectTool(Tool.ColorPicker);
				break;
			}
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmDraw = new JFrame();
		frmDraw.addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				toolViewController.viewActivated();
			}
		});
		frmDraw.setTitle("Draw");
		frmDraw.setBounds(100, 100, 450, 300);
		frmDraw.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JMenuBar menuBar = new JMenuBar();
		frmDraw.setJMenuBar(menuBar);

		JMenu mnFilef = new JMenu("File");
		mnFilef.setMnemonic('F');
		menuBar.add(mnFilef);

		JMenuItem mntmNew = new JMenuItem("New");
		mntmNew.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
				InputEvent.CTRL_MASK));
		mnFilef.add(mntmNew);

		JMenuItem mntmOpen = new JMenuItem("Open");
		mntmOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
				InputEvent.CTRL_MASK));
		mnFilef.add(mntmOpen);

		JMenuItem mntmSave = new JMenuItem("Save");
		mntmSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
				InputEvent.CTRL_MASK));
		mnFilef.add(mntmSave);

		JMenuItem mntmSaveAs = new JMenuItem("Save As");
		mntmSaveAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
				InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK));
		mnFilef.add(mntmSaveAs);

		JMenu mnCanvas = new JMenu("Canvas");
		mnCanvas.setMnemonic('C');
		menuBar.add(mnCanvas);

		JMenuItem mntmResize = new JMenuItem("Resize");
		mntmResize.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E,
				InputEvent.CTRL_MASK));
		mnCanvas.add(mntmResize);

		JMenuBar menuBar_1 = new JMenuBar();
		menuBar.add(menuBar_1);
		frmDraw.getContentPane().setLayout(new BorderLayout(0, 0));

		JPanel toolSelectionContainer = new JPanel();
		toolSelectionContainer.setBorder(new BevelBorder(BevelBorder.LOWERED,
				null, null, null, null));
		frmDraw.getContentPane().add(toolSelectionContainer, BorderLayout.WEST);
		GridBagLayout gbl_toolSelectionContainer = new GridBagLayout();
		gbl_toolSelectionContainer.columnWidths = new int[] { 32, 32, 0 };
		gbl_toolSelectionContainer.rowHeights = new int[] { 32, 0, 0, 0 };
		gbl_toolSelectionContainer.columnWeights = new double[] { 0.0, 0.0,
				Double.MIN_VALUE };
		gbl_toolSelectionContainer.rowWeights = new double[] { 0.0, 0.0, 0.0,
				Double.MIN_VALUE };
		toolSelectionContainer.setLayout(gbl_toolSelectionContainer);

		rectangleSelection = new JButton("");
		rectangleSelection.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				toolViewController.selectTool(Tool.RectangleSelection);
			}
		});
		rectangleSelection.setToolTipText("Select Rectangle (S)");
		rectangleSelection.setBackground(Color.WHITE);
		rectangleSelection.setBorder(null);
		rectangleSelection.setIcon(new ImageIcon(
				"/home/me/workspace/Draw/rsc/rect_select.png"));
		GridBagConstraints gbc_rectangleSelection = new GridBagConstraints();
		gbc_rectangleSelection.anchor = GridBagConstraints.NORTHWEST;
		gbc_rectangleSelection.insets = new Insets(0, 0, 5, 5);
		gbc_rectangleSelection.gridx = 0;
		gbc_rectangleSelection.gridy = 0;
		toolSelectionContainer.add(rectangleSelection, gbc_rectangleSelection);

		penSelection = new JButton("");
		penSelection.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				toolViewController.selectTool(Tool.Pen);
			}
		});
		penSelection.setToolTipText("Pen (P)");
		penSelection.setIcon(new ImageIcon(
				"/home/me/workspace/Draw/rsc/pen.png"));
		penSelection.setBorder(null);
		penSelection.setBackground(Color.WHITE);
		GridBagConstraints gbc_penSelection = new GridBagConstraints();
		gbc_penSelection.insets = new Insets(0, 0, 5, 0);
		gbc_penSelection.anchor = GridBagConstraints.NORTHWEST;
		gbc_penSelection.gridx = 1;
		gbc_penSelection.gridy = 0;
		toolSelectionContainer.add(penSelection, gbc_penSelection);

		fillSelection = new JButton("");
		fillSelection.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				toolViewController.selectTool(Tool.Fill);
			}
		});
		fillSelection.setToolTipText("Fill (F)");
		fillSelection.setIcon(new ImageIcon(
				"/home/me/workspace/Draw/rsc/fill.png"));
		fillSelection.setBorder(null);
		fillSelection.setBackground(Color.WHITE);
		GridBagConstraints gbc_fillSelection = new GridBagConstraints();
		gbc_fillSelection.insets = new Insets(0, 0, 5, 5);
		gbc_fillSelection.gridx = 0;
		gbc_fillSelection.gridy = 1;
		toolSelectionContainer.add(fillSelection, gbc_fillSelection);

		colorPickSelection = new JButton("");
		colorPickSelection.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				toolViewController.selectTool(Tool.ColorPicker);
			}
		});
		colorPickSelection.setIcon(new ImageIcon(
				"/home/me/workspace/Draw/rsc/pick.png"));
		colorPickSelection.setToolTipText("Color Selection (C)");
		colorPickSelection.setBorder(null);
		colorPickSelection.setBackground(Color.WHITE);
		GridBagConstraints gbc_colorPickSelection = new GridBagConstraints();
		gbc_colorPickSelection.insets = new Insets(0, 0, 5, 0);
		gbc_colorPickSelection.gridx = 1;
		gbc_colorPickSelection.gridy = 1;
		toolSelectionContainer.add(colorPickSelection, gbc_colorPickSelection);

		lineSelection = new JButton("");
		lineSelection.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				toolViewController.selectTool(Tool.Line);
			}
		});
		lineSelection.setIcon(new ImageIcon(
				"/home/me/workspace/Draw/rsc/line.png"));
		lineSelection.setToolTipText("Line (L)");
		lineSelection.setBorder(null);
		lineSelection.setBackground(Color.WHITE);
		GridBagConstraints gbc_lineSelection = new GridBagConstraints();
		gbc_lineSelection.insets = new Insets(0, 0, 0, 5);
		gbc_lineSelection.gridx = 0;
		gbc_lineSelection.gridy = 2;
		toolSelectionContainer.add(lineSelection, gbc_lineSelection);

		eraseSelection = new JButton("");
		eraseSelection.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				toolViewController.selectTool(Tool.Eraser);
			}
		});
		eraseSelection.setIcon(new ImageIcon(
				"/home/me/workspace/Draw/rsc/erase.png"));
		eraseSelection.setToolTipText("Eraser (E)");
		eraseSelection.setBorder(null);
		eraseSelection.setBackground(Color.WHITE);
		GridBagConstraints gbc_eraseSelection = new GridBagConstraints();
		gbc_eraseSelection.gridx = 1;
		gbc_eraseSelection.gridy = 2;
		toolSelectionContainer.add(eraseSelection, gbc_eraseSelection);

		JPanel panel_1 = new JPanel();
		frmDraw.getContentPane().add(panel_1, BorderLayout.SOUTH);

		JPanel panel_2 = new JPanel();
		frmDraw.getContentPane().add(panel_2, BorderLayout.CENTER);
	}

	@Override
	public void setSelection(Tool selected) {
		deselectAllTools();
		getToolButton(selected).setBackground(Color.lightGray);
	}

	private void deselectAllTools() {
		JButton[] tools = { rectangleSelection, penSelection, eraseSelection,
				colorPickSelection, lineSelection, fillSelection };
		for (JButton button : tools)
			button.setBackground(Color.white);
	}

	private JButton getToolButton(Tool tool) {
		switch (tool) {
		case RectangleSelection:
			return rectangleSelection;
		case ColorPicker:
			return colorPickSelection;
		case Eraser:
			return eraseSelection;
		case Fill:
			return fillSelection;
		case Line:
			return lineSelection;
		case Pen:
			return penSelection;
		}
		return null;
	}
}

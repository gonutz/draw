package draw;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.FlowLayout;
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
import javax.swing.JScrollPane;
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
	private CurrentColors currentColors;
	private ColorPalette colorPalette;
	private DrawArea drawArea;
	private DrawAreaController drawAreaController;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow window = new MainWindow();
					ToolViewController toolController = new ToolViewController(
							window);
					window.setToolViewController(toolController);
					ColorPaletteViewController paletteController = new ColorPaletteViewController(
							window.colorPalette, window.currentColors);
					window.colorPalette
							.setAndActivateController(paletteController);
					window.drawAreaController = new DrawAreaController(
							window.drawArea);
					window.drawArea.setController(window.drawAreaController);
					window.drawAreaController
							.setDrawSettings(new DrawSettingsAdapter(
									paletteController, toolController));
					window.drawAreaController.newImage(320, 240);
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
				// returning false Allows the event to be re-dispatched
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

		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic('F');
		menuBar.add(fileMenu);

		JMenuItem newImage = new JMenuItem("New");
		newImage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				EventQueue.invokeLater(new Runnable() {
					public void run() {
						DimensionChooser dim = new DimensionChooser();
						dim.setVisible(true);
						if (dim.wasAccepted()) {
							drawAreaController.newImage(dim.getCanvasWidth(),
									dim.getCanvasHeight());
						}
					}
				});
			}
		});
		newImage.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
				InputEvent.CTRL_MASK));
		fileMenu.add(newImage);

		JMenuItem openImage = new JMenuItem("Open");
		openImage.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
				InputEvent.CTRL_MASK));
		fileMenu.add(openImage);

		JMenuItem saveImage = new JMenuItem("Save");
		saveImage.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
				InputEvent.CTRL_MASK));
		fileMenu.add(saveImage);

		JMenuItem saveImageAs = new JMenuItem("Save As");
		saveImageAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
				InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK));
		fileMenu.add(saveImageAs);

		JMenu canvasMenu = new JMenu("Canvas");
		canvasMenu.setMnemonic('C');
		menuBar.add(canvasMenu);

		JMenuItem mntmResize = new JMenuItem("Resize");
		mntmResize.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E,
				InputEvent.CTRL_MASK));
		canvasMenu.add(mntmResize);
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
		rectangleSelection.setFocusable(false);
		rectangleSelection.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				toolViewController.selectTool(Tool.RectangleSelection);
			}
		});
		rectangleSelection.setToolTipText("Select Rectangle (S)");
		rectangleSelection.setBackground(Color.WHITE);
		rectangleSelection.setBorder(null);
		rectangleSelection.setIcon(new ImageIcon("./rsc/rect_select.png"));
		GridBagConstraints gbc_rectangleSelection = new GridBagConstraints();
		gbc_rectangleSelection.anchor = GridBagConstraints.NORTHWEST;
		gbc_rectangleSelection.insets = new Insets(0, 0, 5, 5);
		gbc_rectangleSelection.gridx = 0;
		gbc_rectangleSelection.gridy = 0;
		toolSelectionContainer.add(rectangleSelection, gbc_rectangleSelection);

		penSelection = new JButton("");
		penSelection.setFocusable(false);
		penSelection.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				toolViewController.selectTool(Tool.Pen);
			}
		});
		penSelection.setToolTipText("Pen (P)");
		penSelection.setIcon(new ImageIcon("./rsc/pen.png"));
		penSelection.setBorder(null);
		penSelection.setBackground(Color.WHITE);
		GridBagConstraints gbc_penSelection = new GridBagConstraints();
		gbc_penSelection.insets = new Insets(0, 0, 5, 0);
		gbc_penSelection.anchor = GridBagConstraints.NORTHWEST;
		gbc_penSelection.gridx = 1;
		gbc_penSelection.gridy = 0;
		toolSelectionContainer.add(penSelection, gbc_penSelection);

		fillSelection = new JButton("");
		fillSelection.setFocusable(false);
		fillSelection.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				toolViewController.selectTool(Tool.Fill);
			}
		});
		fillSelection.setToolTipText("Fill (F)");
		fillSelection.setIcon(new ImageIcon("./rsc/fill.png"));
		fillSelection.setBorder(null);
		fillSelection.setBackground(Color.WHITE);
		GridBagConstraints gbc_fillSelection = new GridBagConstraints();
		gbc_fillSelection.insets = new Insets(0, 0, 5, 5);
		gbc_fillSelection.gridx = 0;
		gbc_fillSelection.gridy = 1;
		toolSelectionContainer.add(fillSelection, gbc_fillSelection);

		colorPickSelection = new JButton("");
		colorPickSelection.setFocusable(false);
		colorPickSelection.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				toolViewController.selectTool(Tool.ColorPicker);
			}
		});
		colorPickSelection.setIcon(new ImageIcon("./rsc/pick.png"));
		colorPickSelection.setToolTipText("Color Selection (C)");
		colorPickSelection.setBorder(null);
		colorPickSelection.setBackground(Color.WHITE);
		GridBagConstraints gbc_colorPickSelection = new GridBagConstraints();
		gbc_colorPickSelection.insets = new Insets(0, 0, 5, 0);
		gbc_colorPickSelection.gridx = 1;
		gbc_colorPickSelection.gridy = 1;
		toolSelectionContainer.add(colorPickSelection, gbc_colorPickSelection);

		lineSelection = new JButton("");
		lineSelection.setFocusable(false);
		lineSelection.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				toolViewController.selectTool(Tool.Line);
			}
		});
		lineSelection.setIcon(new ImageIcon("./rsc/line.png"));
		lineSelection.setToolTipText("Line (L)");
		lineSelection.setBorder(null);
		lineSelection.setBackground(Color.WHITE);
		GridBagConstraints gbc_lineSelection = new GridBagConstraints();
		gbc_lineSelection.insets = new Insets(0, 0, 0, 5);
		gbc_lineSelection.gridx = 0;
		gbc_lineSelection.gridy = 2;
		toolSelectionContainer.add(lineSelection, gbc_lineSelection);

		eraseSelection = new JButton("");
		eraseSelection.setFocusable(false);
		eraseSelection.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				toolViewController.selectTool(Tool.Eraser);
			}
		});
		eraseSelection.setIcon(new ImageIcon("./rsc/erase.png"));
		eraseSelection.setToolTipText("Eraser (E)");
		eraseSelection.setBorder(null);
		eraseSelection.setBackground(Color.WHITE);
		GridBagConstraints gbc_eraseSelection = new GridBagConstraints();
		gbc_eraseSelection.gridx = 1;
		gbc_eraseSelection.gridy = 2;
		toolSelectionContainer.add(eraseSelection, gbc_eraseSelection);

		JPanel colorContainer = new JPanel();
		FlowLayout flowLayout = (FlowLayout) colorContainer.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		flowLayout.setHgap(20);
		frmDraw.getContentPane().add(colorContainer, BorderLayout.SOUTH);

		currentColors = new CurrentColors();
		colorContainer.add(currentColors);
		colorPalette = new ColorPalette();
		colorContainer.add(colorPalette);

		JScrollPane paintAreaScroller = new JScrollPane();
		frmDraw.getContentPane().add(paintAreaScroller, BorderLayout.CENTER);
		drawArea = new DrawArea();
		paintAreaScroller.getViewport().add(drawArea, null);
	}
}

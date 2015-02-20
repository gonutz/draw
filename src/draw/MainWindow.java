package draw;

import io.ImageFromFileLoader;
import io.ImageLoadController;
import io.ImageSaveController;
import io.ImageToFileSaver;
import io.SwingFileDialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
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
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.border.BevelBorder;

public class MainWindow implements ToolView, ErrorDisplay, PositionView,
		CurrentFileNameObserver, ZoomView {

	private static final int INITIAL_CANVAS_WIDTH = 640;
	private static final int INITIAL_CANVAS_HEIGHT = 480;

	private JFrame mainFrame;
	private JButton rectangleSelection;
	private JButton pen;
	private JButton fill;
	private JButton colorPicker;
	private JButton line;
	private JButton eraser;
	private ToolViewController toolViewController;
	private CurrentColors currentColors;
	private ColorPalette colorPalette;
	private ColorPaletteViewController paletteController;
	private DrawArea drawArea;
	private DrawAreaController drawAreaController;
	private ImageSaveController imageSaveController;
	private ImageLoadController imageLoadController;
	private JLabel positionLabel;
	private JLabel zoomLabel;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {

			public void run() {
				try {
					MainWindow window = new MainWindow();
					wireUpControllers(window);
					window.mainFrame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			private void wireUpControllers(MainWindow window) {
				window.toolViewController = new ToolViewController(window);
				window.paletteController = new ColorPaletteViewController(
						window.colorPalette, window.currentColors);
				window.colorPalette
						.setAndActivateController(window.paletteController);
				window.drawAreaController = new DrawAreaController(
						window.drawArea);
				window.drawArea.setController(window.drawAreaController);
				window.drawAreaController
						.setDrawSettings(window.paletteController);
				window.drawAreaController
						.setToolController(window.toolViewController);
				window.drawAreaController.setClipboard(new SystemClipboard());
				window.toolViewController
						.setObserver(window.drawAreaController);
				window.drawAreaController.newImage(INITIAL_CANVAS_WIDTH,
						INITIAL_CANVAS_HEIGHT);
				SwingFileDialog dialog = new SwingFileDialog();
				window.imageSaveController = new ImageSaveController(dialog,
						window.drawAreaController, new ImageToFileSaver(),
						window);
				window.imageSaveController.setCurrentFileNameObserver(window);
				window.imageLoadController = new ImageLoadController(dialog,
						new ImageFromFileLoader(), window.drawAreaController,
						window);
				window.imageLoadController
						.setObserver(window.imageSaveController);
			}
		});
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
		if (!mainFrame.isFocused())
			return;
		int offset = 0;
		if (e.isShiftDown())
			offset = 10;
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
		if (e.getID() == KeyEvent.KEY_PRESSED)
			switch (e.getKeyCode()) {
			case KeyEvent.VK_ESCAPE:
				drawAreaController.escape();
				break;
			case KeyEvent.VK_DELETE:
				drawAreaController.delete();
				break;
			case KeyEvent.VK_LEFT:
				drawAreaController.move(-1, 0);
				break;
			case KeyEvent.VK_RIGHT:
				drawAreaController.move(1, 0);
				break;
			case KeyEvent.VK_UP:
				drawAreaController.move(0, -1);
				break;
			case KeyEvent.VK_DOWN:
				drawAreaController.move(0, 1);
				break;
			case KeyEvent.VK_1:
			case KeyEvent.VK_NUMPAD1:
				if (e.isControlDown())
					paletteController.selectBackgroundColor(0 + offset);
				else
					paletteController.selectForegroundColor(0 + offset);
				break;
			case KeyEvent.VK_2:
			case KeyEvent.VK_NUMPAD2:
				if (e.isControlDown())
					paletteController.selectBackgroundColor(1 + offset);
				else
					paletteController.selectForegroundColor(1 + offset);
				break;
			case KeyEvent.VK_3:
			case KeyEvent.VK_NUMPAD3:
				if (e.isControlDown())
					paletteController.selectBackgroundColor(2 + offset);
				else
					paletteController.selectForegroundColor(2 + offset);
				break;
			case KeyEvent.VK_4:
			case KeyEvent.VK_NUMPAD4:
				if (e.isControlDown())
					paletteController.selectBackgroundColor(3 + offset);
				else
					paletteController.selectForegroundColor(3 + offset);
				break;
			case KeyEvent.VK_5:
			case KeyEvent.VK_NUMPAD5:
				if (e.isControlDown())
					paletteController.selectBackgroundColor(4 + offset);
				else
					paletteController.selectForegroundColor(4 + offset);
				break;
			case KeyEvent.VK_6:
			case KeyEvent.VK_NUMPAD6:
				if (e.isControlDown())
					paletteController.selectBackgroundColor(5 + offset);
				else
					paletteController.selectForegroundColor(5 + offset);
				break;
			case KeyEvent.VK_7:
			case KeyEvent.VK_NUMPAD7:
				if (e.isControlDown())
					paletteController.selectBackgroundColor(6 + offset);
				else
					paletteController.selectForegroundColor(6 + offset);
				break;
			case KeyEvent.VK_8:
			case KeyEvent.VK_NUMPAD8:
				if (e.isControlDown())
					paletteController.selectBackgroundColor(7 + offset);
				else
					paletteController.selectForegroundColor(7 + offset);
				break;
			case KeyEvent.VK_9:
			case KeyEvent.VK_NUMPAD9:
				if (e.isControlDown())
					paletteController.selectBackgroundColor(8 + offset);
				else
					paletteController.selectForegroundColor(8 + offset);
				break;
			case KeyEvent.VK_0:
			case KeyEvent.VK_NUMPAD0:
				if (e.isControlDown())
					paletteController.selectBackgroundColor(9 + offset);
				else
					paletteController.selectForegroundColor(9 + offset);
				break;
			}
	}

	@Override
	public void setSelection(Tool selected) {
		deselectAllTools();
		getToolButton(selected).setBackground(Color.lightGray);
	}

	private void deselectAllTools() {
		JButton[] tools = { rectangleSelection, pen, eraser, colorPicker, line,
				fill };
		for (JButton button : tools)
			button.setBackground(Color.white);
	}

	private JButton getToolButton(Tool tool) {
		switch (tool) {
		case RectangleSelection:
			return rectangleSelection;
		case ColorPicker:
			return colorPicker;
		case Eraser:
			return eraser;
		case Fill:
			return fill;
		case Line:
			return line;
		case Pen:
			return pen;
		}
		return null;
	}

	private void initialize() {
		mainFrame = new JFrame();
		mainFrame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				toolViewController.viewActivated();
			}
		});
		mainFrame.setTitle("Draw");
		mainFrame.setBounds(100, 100, 800, 600);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JMenuBar menuBar = new JMenuBar();
		mainFrame.setJMenuBar(menuBar);

		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic('F');
		menuBar.add(fileMenu);

		JMenuItem newImage = new JMenuItem("New");
		final DimensionChooser dim = new DimensionChooser(INITIAL_CANVAS_WIDTH,
				INITIAL_CANVAS_HEIGHT);
		newImage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				EventQueue.invokeLater(new Runnable() {
					public void run() {
						dim.setVisible(true);
						if (dim.wasAccepted()) {
							drawAreaController.newImage(dim.getCanvasWidth(),
									dim.getCanvasHeight());
							imageSaveController.newImageWasCreated();
						}
					}
				});
			}
		});
		newImage.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
				InputEvent.CTRL_MASK));
		fileMenu.add(newImage);

		JMenuItem openImage = new JMenuItem("Open");
		openImage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				imageLoadController.load();
			}
		});
		openImage.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
				InputEvent.CTRL_MASK));
		fileMenu.add(openImage);

		JMenuItem saveImage = new JMenuItem("Save");
		saveImage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				imageSaveController.save();
			}
		});
		saveImage.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
				InputEvent.CTRL_MASK));
		fileMenu.add(saveImage);

		JMenuItem saveImageAs = new JMenuItem("Save As");
		saveImageAs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				imageSaveController.saveAsNewFile();
			}
		});
		saveImageAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
				InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK));
		fileMenu.add(saveImageAs);

		JMenu editMenu = new JMenu("Edit");
		editMenu.setMnemonic('E');
		menuBar.add(editMenu);

		JMenuItem undo = new JMenuItem("Undo");
		undo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				drawAreaController.undoLastAction();
			}
		});
		undo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z,
				InputEvent.CTRL_MASK));
		editMenu.add(undo);

		JMenuItem redo = new JMenuItem("Redo");
		redo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				drawAreaController.redoPreviousAction();
			}
		});
		redo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y,
				InputEvent.CTRL_MASK));
		editMenu.add(redo);

		JMenuItem mntmCopy = new JMenuItem("Copy");
		mntmCopy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				drawAreaController.copy();
			}
		});
		mntmCopy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,
				InputEvent.CTRL_MASK));
		editMenu.add(mntmCopy);

		JMenuItem mntmPaste = new JMenuItem("Paste");
		mntmPaste.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				drawAreaController.paste();
			}
		});

		JMenuItem mntmCut = new JMenuItem("Cut");
		mntmCut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				drawAreaController.cut();
			}
		});
		mntmCut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,
				InputEvent.CTRL_MASK));
		editMenu.add(mntmCut);
		mntmPaste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V,
				InputEvent.CTRL_MASK));
		editMenu.add(mntmPaste);

		JMenuItem mntmSelectAll = new JMenuItem("Select All");
		mntmSelectAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				drawAreaController.selectAll();
			}
		});
		mntmSelectAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,
				InputEvent.CTRL_MASK));
		editMenu.add(mntmSelectAll);

		JMenu canvasMenu = new JMenu("Canvas");
		canvasMenu.setMnemonic('C');
		menuBar.add(canvasMenu);

		JMenuItem resizeCanvas = new JMenuItem("Resize");
		resizeCanvas.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E,
				InputEvent.CTRL_MASK));
		canvasMenu.add(resizeCanvas);

		JMenu viewMenu = new JMenu("View");
		viewMenu.setMnemonic('V');
		menuBar.add(viewMenu);

		JMenuItem zoomIn = new JMenuItem("Zoom In");
		viewMenu.add(zoomIn);
		zoomIn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				drawArea.zoomIn();
			}
		});
		zoomIn.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS,
				InputEvent.CTRL_MASK));

		JMenuItem zoomOut = new JMenuItem("Zoom Out");
		viewMenu.add(zoomOut);
		zoomOut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				drawArea.zoomOut();
			}
		});
		zoomOut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS,
				InputEvent.CTRL_MASK));
		mainFrame.getContentPane().setLayout(new BorderLayout(0, 0));

		JPanel toolSelectionContainer = new JPanel();
		toolSelectionContainer.setBorder(new BevelBorder(BevelBorder.LOWERED,
				null, null, null, null));
		mainFrame.getContentPane().add(toolSelectionContainer,
				BorderLayout.WEST);
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
			public void actionPerformed(ActionEvent e) {
				toolViewController.selectTool(Tool.RectangleSelection);
			}
		});
		rectangleSelection.setToolTipText("Select Rectangle (S)");
		rectangleSelection.setBackground(Color.WHITE);
		rectangleSelection.setBorder(null);
		rectangleSelection.setIcon(new ImageIcon(getClass().getResource(
				"/rsc/rect_select.png")));
		GridBagConstraints gbc_rectangleSelection = new GridBagConstraints();
		gbc_rectangleSelection.anchor = GridBagConstraints.NORTHWEST;
		gbc_rectangleSelection.insets = new Insets(0, 0, 5, 5);
		gbc_rectangleSelection.gridx = 0;
		gbc_rectangleSelection.gridy = 0;
		toolSelectionContainer.add(rectangleSelection, gbc_rectangleSelection);

		pen = new JButton("");
		pen.setFocusable(false);
		pen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				toolViewController.selectTool(Tool.Pen);
			}
		});
		pen.setToolTipText("Pen (P)");
		pen.setIcon(new ImageIcon(getClass().getResource("/rsc/pen.png")));
		pen.setBorder(null);
		pen.setBackground(Color.WHITE);
		GridBagConstraints gbc_pen = new GridBagConstraints();
		gbc_pen.insets = new Insets(0, 0, 5, 0);
		gbc_pen.anchor = GridBagConstraints.NORTHWEST;
		gbc_pen.gridx = 0;
		gbc_pen.gridy = 1;
		toolSelectionContainer.add(pen, gbc_pen);

		fill = new JButton("");
		fill.setFocusable(false);
		fill.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				toolViewController.selectTool(Tool.Fill);
			}
		});
		fill.setToolTipText("Fill (F)");
		fill.setIcon(new ImageIcon(getClass().getResource("/rsc/fill.png")));
		fill.setBorder(null);
		fill.setBackground(Color.WHITE);
		GridBagConstraints gbc_fill = new GridBagConstraints();
		gbc_fill.insets = new Insets(0, 0, 5, 5);
		gbc_fill.gridx = 0;
		gbc_fill.gridy = 2;
		toolSelectionContainer.add(fill, gbc_fill);

		colorPicker = new JButton("");
		colorPicker.setFocusable(false);
		colorPicker.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				toolViewController.selectTool(Tool.ColorPicker);
			}
		});
		colorPicker.setIcon(new ImageIcon(getClass().getResource(
				"/rsc/pick.png")));
		colorPicker.setToolTipText("Color Selection (C)");
		colorPicker.setBorder(null);
		colorPicker.setBackground(Color.WHITE);
		GridBagConstraints gbc_colorPicker = new GridBagConstraints();
		gbc_colorPicker.insets = new Insets(0, 0, 5, 0);
		gbc_colorPicker.gridx = 1;
		gbc_colorPicker.gridy = 0;
		toolSelectionContainer.add(colorPicker, gbc_colorPicker);

		line = new JButton("");
		line.setFocusable(false);
		line.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				toolViewController.selectTool(Tool.Line);
			}
		});
		line.setIcon(new ImageIcon(getClass().getResource("/rsc/line.png")));
		line.setToolTipText("Line (L)");
		line.setBorder(null);
		line.setBackground(Color.WHITE);
		GridBagConstraints gbc_line = new GridBagConstraints();
		gbc_line.insets = new Insets(0, 0, 0, 5);
		gbc_line.gridx = 1;
		gbc_line.gridy = 1;
		toolSelectionContainer.add(line, gbc_line);

		eraser = new JButton("");
		eraser.setFocusable(false);
		eraser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				toolViewController.selectTool(Tool.Eraser);
			}
		});
		eraser.setIcon(new ImageIcon(getClass().getResource("/rsc/erase.png")));
		eraser.setToolTipText("Eraser (E)");
		eraser.setBorder(null);
		eraser.setBackground(Color.WHITE);
		GridBagConstraints gbc_eraser = new GridBagConstraints();
		gbc_eraser.gridx = 1;
		gbc_eraser.gridy = 2;
		toolSelectionContainer.add(eraser, gbc_eraser);

		JPanel colorContainer = new JPanel();
		FlowLayout flowLayout = (FlowLayout) colorContainer.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		flowLayout.setHgap(20);

		JPanel colorAndStatusContainer = new JPanel(new BorderLayout());
		colorAndStatusContainer.add(colorContainer, BorderLayout.WEST);

		zoomLabel = new JLabel("Zoom 100% ");
		zoomLabel.setFont(new Font("Tahoma", Font.PLAIN, 18));
		colorAndStatusContainer.add(zoomLabel, BorderLayout.EAST);

		mainFrame.getContentPane().add(colorAndStatusContainer,
				BorderLayout.SOUTH);

		currentColors = new CurrentColors();
		colorContainer.add(currentColors);
		colorPalette = new ColorPalette();
		colorContainer.add(colorPalette);

		positionLabel = new JLabel("");
		positionLabel.setFont(new Font("Tahoma", Font.PLAIN, 18));
		colorContainer.add(positionLabel);

		JScrollPane paintAreaScroller = new JScrollPane();

		mainFrame.getContentPane().add(paintAreaScroller, BorderLayout.CENTER);
		drawArea = new DrawArea();
		drawArea.setPositionView(this);
		drawArea.setZoomView(this);
		paintAreaScroller.setViewportView(drawArea);
		paintAreaScroller.addMouseWheelListener(drawArea);
	}

	@Override
	public void showError(String message) {
		JOptionPane.showMessageDialog(null, message, "Error",
				JOptionPane.ERROR_MESSAGE);
	}

	@Override
	public void setNoPosition() {
		positionLabel.setText("");
	}

	@Override
	public void setPosition(int x, int y) {
		positionLabel.setText("x: " + x + "  y: " + y);
	}

	@Override
	public void currentFileNameChangedTo(String fileName) {
		if (fileName == null)
			mainFrame.setTitle("Draw");
		else
			mainFrame.setTitle("Draw - " + fileName);
	}

	@Override
	public void setZoomFactor(int zoomFactor) {
		zoomLabel.setText("Zoom " + zoomFactor * 100 + "% ");
	}
}

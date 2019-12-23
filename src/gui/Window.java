package gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import cards.Stats;
import main.Main;
import main.Settings;

public class Window {
	private JFrame frame;
	private SceneHolder sceneHolder;

	public Window(Scene s, String title) {
		frame = new JFrame();
		sceneHolder = new SceneHolder(frame, s);
		frame.setTitle(title);
		try {
			if (Main.loadFromJar)
			frame.setIconImage(ImageIO.read(Window.class.getResourceAsStream("/assets/icon.png")));
			else
				frame.setIconImage(ImageIO.read(new File(new File("").getAbsoluteFile(), "/assets/icon.png")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				onClose();
			}
		});
		JPanel panel = new JPanel() {
			protected void paintComponent(Graphics g) {
				((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				sceneHolder.paint(g, getSize());
			}
		};
		frame.setContentPane(panel);
		frame.addComponentListener(new ComponentAdapter() {
			public void componentMoved(ComponentEvent e) {
				boolean save = frame.getExtendedState() == JFrame.NORMAL;
				if (save && e.getComponent().isShowing()) {
					Point l = e.getComponent().getLocationOnScreen();
					Settings.windowX = l.x;
					Settings.windowY = l.y;
				}
			}
		});
		panel.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				boolean save = frame.getExtendedState() == JFrame.NORMAL;
				Dimension s = e.getComponent().getSize();
				sceneHolder.getScene().setSize(s);
				if (save) {
					Settings.windowW = s.width;
					Settings.windowH = s.height;
				}
			}
		});
		panel.setPreferredSize(new Dimension(Settings.windowW, Settings.windowH));
		frame.pack();
		if (Settings.windowX == Integer.MAX_VALUE || Settings.windowY == Integer.MAX_VALUE)
			frame.setLocationRelativeTo(null);
		else
			frame.setLocation(Settings.windowX, Settings.windowY);
		MouseAdapter m = new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				sceneHolder.getScene().mousePressed(e);
			}

			public void mouseReleased(MouseEvent e) {
				sceneHolder.getScene().mouseReleased(e);
			}

			public void mouseClicked(MouseEvent e) {
				sceneHolder.getScene().mouseClicked(e);
			}

			public void mouseMoved(MouseEvent e) {
				sceneHolder.setMousePos(e.getPoint());
				sceneHolder.getScene().mouseMoved(e);
			}

			public void mouseDragged(MouseEvent e) {
				sceneHolder.setMousePos(e.getPoint());
				sceneHolder.getScene().mouseDragged(e);
			}

			public void mouseWheelMoved(MouseWheelEvent e) {
				sceneHolder.getScene().mouseWheelMoved(e);
			}
		};
		panel.addMouseListener(m);
		panel.addMouseMotionListener(m);
		panel.addMouseWheelListener(m);
		frame.addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent e) {
				sceneHolder.getScene().keyTyped(e);
			}

			public void keyReleased(KeyEvent e) {
				sceneHolder.getScene().keyReleased(e);
			}

			public void keyPressed(KeyEvent e) {
				sceneHolder.getScene().keyTyped(e);
				sceneHolder.getScene().keyPressed(e);
			}
		});
	}

	private void onClose() {
		Stats.save();
		Settings.save();
	}

	public void show() {
		frame.setVisible(true);
	}
}

package gui;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Toolkit;
import java.util.ArrayList;

import javax.swing.JFrame;

public final class SceneHolder {
	private int sceneChangeTime = 600;

	private static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

	private JFrame frame;
	private Scene scene, fadingScene;
	private Point mousePos;
	private long changeTime;
	private RepaintingObject sceneChangeRep;
	private boolean onShutRun;

	public SceneHolder(JFrame frame, Scene s) {
		sceneChangeRep = new RepaintingObject(() -> frame.getContentPane().repaint());
		sceneChangeRep.add();
		this.frame = frame;
		setScene(s);
	}

	public void setScene(Scene s) {
		System.gc();
		long time = System.currentTimeMillis();
		if (time - changeTime < sceneChangeTime + 100)
			return;
		onShutRun = false;
		s.setSize(frame.getContentPane().getSize());
		fadingScene = scene;
		scene = s;
		s.setHolder(this);
		s.runOnDisplay();
		changeTime = time;
		sceneChangeRep.setActive(true);
	}

	public void setMousePos(Point p) {
		mousePos = p;
	}

	public Point getMousePos() {
		return mousePos;
	}

	public Scene getScene() {
		return scene;
	}

	public JFrame getFrame() {
		return frame;
	}

	public void paint(Graphics g, Dimension size) {
		Theme.paintBackground(g, size);
		double t = (double) (System.currentTimeMillis() - changeTime) / sceneChangeTime;
		if (t < 1) {
			float sine = (float) HoverCalc.sineSigmoid(t);
			Graphics2D g2d = (Graphics2D) g;
			Composite comp = g2d.getComposite();
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, sine));
			scene.paintBackground(g2d);
			if (fadingScene != null) {
				g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1 - sine));
				fadingScene.paintBackground(g2d);
			}
			g2d.setComposite(comp);

			if (fadingScene != null)
				paintFadingElements(fadingScene.getElements(), t, g);
			paintSlidingElements(scene.getElements(), t, g);
		} else {
			scene.paint(g);
			if (!onShutRun && fadingScene != null) {
				fadingScene.runOnShut();
				onShutRun = true;
			}
		}
		if (t > 2 && t < 3 && sceneChangeRep.isActive())
			sceneChangeRep.setActive(false);
	}

	private void paintSlidingElements(ArrayList<Element> list, double t, Graphics g) {
		for (int i = 0; i < list.size(); i++) {
			double tc;
			if (list.size() != 1) {
				int j = list.size() - 1 - i;
				tc = 3d / 2 * (t - 1d / 3 / (list.size() - 1) * j);
			} else
				tc = t;
			tc = Math.max(0, Math.min(1, tc));
			tc = HoverCalc.invSquareSigmoid(tc);
			int dev = (int) ((1 - tc) * screenSize.height);

			g.translate(0, -dev);
			list.get(i).paint(g);
			g.translate(0, dev);
		}
	}

	private void paintFadingElements(ArrayList<Element> list, double t, Graphics g) {
		for (int i = 0; i < list.size(); i++) {
			double tc;
			if (list.size() != 1) {
				int j = list.size() - 1 - i;
				tc = 3d / 2 * (t - 1d / 3 / (list.size() - 1) * j);
			} else
				tc = t;
			tc = Math.max(0, Math.min(1, tc));
			tc = HoverCalc.squareSigmoid(tc);
			int dev = (int) (tc * screenSize.height);

			g.translate(0, dev);
			list.get(i).paint(g);
			g.translate(0, -dev);
		}
	}
}

package gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;

public abstract class Scene {
	public static boolean drawElementsOutline = false;

	private ArrayList<Element> elements = new ArrayList<>();
	private Dimension size;
	private SceneHolder holder;

	public void mousePressed(MouseEvent e) {
		elements.forEach(el -> {
			boolean c = el.getBounds().contains(e.getPoint());
			if (c != el.isFocused())
				el.setFocused(c);
			el.mousePressed(e, c);
		});
	}

	public void mouseReleased(MouseEvent e) {
		elements.forEach(el -> el.mouseReleased(e, el.getBounds().contains(e.getPoint())));
	}

	public void mouseClicked(MouseEvent e) {
		elements.forEach(el -> el.mouseClicked(e, el.getBounds().contains(e.getPoint())));
	}

	public void mouseMoved(MouseEvent e) {
		elements.forEach(el -> el.mouseMoved(e, el.getBounds().contains(e.getPoint())));
	}

	public void mouseDragged(MouseEvent e) {
		elements.forEach(el -> el.mouseDragged(e, el.getBounds().contains(e.getPoint())));
	}

	public void mouseWheelMoved(MouseWheelEvent e) {
		elements.forEach(el -> el.mouseWheelMoved(e, el.getBounds().contains(e.getPoint())));
	}

	public void keyPressed(KeyEvent e) {
		elements.forEach(el -> el.keyPressed(e));
	}

	public void keyReleased(KeyEvent e) {
		elements.forEach(el -> el.keyReleased(e));
	}

	public void keyTyped(KeyEvent e) {
		elements.forEach(el -> el.keyTyped(e));
	}

	public final void paintElements(Graphics g) {
		elements.forEach(e -> {
			e.paint(g);
			if (drawElementsOutline) {
				g.setColor(Theme.foreground);
				g.drawRect(e.getBoundsR().x, e.getBoundsR().y, e.getBounds().getWidth(), e.getBounds().getHeight());
			}
		});
	}

	public void paintBackground(Graphics g) {
	}

	public void paint(Graphics g) {
		paintBackground(g);
		paintElements(g);
	}

	public final void add(Element e) {
		elements.add(e);
	}

	public final void remove(Element e) {
		elements.remove(e);
	}

	public final ArrayList<Element> getElements() {
		return elements;
	}

	public final void setSize(Dimension d) {
		size = d;
		elements.forEach(el -> el.getBounds().setSize(d));
	}

	public final Dimension getSize() {
		return size;
	}

	public final void repaint() {
		holder.getFrame().repaint();
	}

	public final void repaint(Rectangle r) {
		if (holder != null)
			holder.getFrame().getContentPane().repaint(r.x, r.y, r.width, r.height);
	}

	public final void setHolder(SceneHolder h) {
		holder = h;
	}

	public final SceneHolder getHolder() {
		return holder;
	}

	public void updateLocalization() {
	}

	public final void runOnDisplay() {
		runOnDisplayElements();
		onDisplay();
	}

	public final void runOnShut() {
		runOnShutElements();
		onShut();
	}

	public void runOnDisplayElements() {
		elements.forEach(e -> e.onDisplay());
	}

	public void runOnShutElements() {
		elements.forEach(e -> e.onShut());
	}

	public void onDisplay() {
	}

	public void onShut() {
	}
}

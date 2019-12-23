package gui;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

public abstract class Element {
	private Scene container;
	private Bounds bounds;
	private boolean isFocused;

	public void mousePressed(MouseEvent e, boolean contains) {
	}

	public void mouseReleased(MouseEvent e, boolean contains) {
	}

	public void mouseClicked(MouseEvent e, boolean contains) {
	}

	public void mouseMoved(MouseEvent e, boolean contains) {
	}

	public void mouseDragged(MouseEvent e, boolean contains) {
	}

	public void mouseWheelMoved(MouseWheelEvent e, boolean contains) {
	}

	public void keyPressed(KeyEvent e) {
	}

	public void keyReleased(KeyEvent e) {
	}

	public void keyTyped(KeyEvent e) {
	}

	public final void setBounds(Bounds b) {
		if (bounds == null) {
			bounds = b;
			return;
		}
		b.setSize(bounds.getSize());
		Rectangle r1 = bounds.getBounds(), r2 = b.getBounds();
		bounds = b;
		onBoundsChange();
		if (!r1.getSize().equals(r2.getSize()))
			onSizeChange();
		if (!r1.getLocation().equals(r2.getLocation()))
			onLocationChange();
	}

	public void onBoundsChange() {
	}

	public void onLocationChange() {
	}

	public void onSizeChange() {
	}

	public final Bounds getBounds() {
		return bounds;
	}

	public final Rectangle getBoundsR() {
		return bounds.getBounds();
	}

	public final boolean isFocused() {
		return isFocused;
	}

	public final void setFocused(boolean b) {
		isFocused = b;
		onFocusChange(b);
	}

	public void onFocusChange(boolean newFocus) {
	}

	public abstract void paint(Graphics g);

	public final void setContainer(Scene s) {
		container = s;
	}

	public final Scene getContainer() {
		return container;
	}

	public final void repaint() {
		if (container == null)
			return;
		Rectangle r = getBoundsR();
		container.repaint(new Rectangle(r.x, r.y, r.width + 1, r.height + 1));
	}

	public final void repaint(Rectangle r) {
		container.repaint(r);
	}

	public void onDisplay() {
	}

	public void onShut() {
	}
}

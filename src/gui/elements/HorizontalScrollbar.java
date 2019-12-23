package gui.elements;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import gui.Bounds;
import gui.Element;
import gui.Scene;
import gui.Theme;

public class HorizontalScrollbar extends Element {
	private double pos;
	private int sliderW;
	private Runnable r;

	public HorizontalScrollbar(Runnable r, Bounds b, Scene container) {
		setBounds(b);
		setContainer(container);

		this.r = r;
		sliderW = b.getHeight();
	}

	public void mousePressed(MouseEvent e, boolean contains) {
		mouseDragged(e, contains);
	}

	public void mouseDragged(MouseEvent e, boolean contains) {
		if (!isFocused())
			return;
		Rectangle r = getBoundsR();
		double f = (e.getX() - r.x - sliderW / 2d) / (r.width - sliderW);
		if (f > 1)
			f = 1;
		else if (f < 0)
			f = 0;
		if (pos != f) {
			pos = f;
			this.r.run();
			repaint();
		}
	}

	public void setPos(double pos) {
		if (pos > 1)
			pos = 1;
		else if (pos < 0)
			pos = 0;
		this.pos = pos;
	}

	public double getPos() {
		return pos;
	}

	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		Rectangle r = getBoundsR();

		int sliderX = (int) Math.round(pos * (r.width - sliderW));
		g2d.setColor(Theme.getFg());
		g2d.drawRect(r.x, r.y, r.width, r.height);
		g2d.drawRect(r.x + 1, r.y + 1, r.width - 2, r.height - 2);
		g2d.setColor(Theme.interpolate(Theme.getBg(), Theme.getFg(), 0.75d));
		g2d.fillRect(r.x + sliderX + 1, r.y + 1, sliderW - 1, sliderW - 1);
	}
}

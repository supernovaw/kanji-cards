package gui.elements;

import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;

import gui.Bounds;
import gui.Element;
import gui.HoverCalc;
import gui.Scene;
import gui.Theme;

public class Button extends Element {
	private double blackoutFraction = .7d;
	private float outlineThickness = 2f;
	private int hoverTime = 170, holdTime = 65;

	private String text;
	private Runnable r;
	private HoverCalc hoverCalc, holdCalc;
	private boolean hold;
	private Polygon polygon;
	private Point textPos;
	private float fontSize;
	private Font font;

	public Button(String text, Runnable r, Bounds b, Scene container) {
		this.text = text;
		this.r = r;
		fontSize = Theme.fontSize;
		font = Theme.interfaceFont.deriveFont(fontSize);
		setContainer(container);
		setBounds(b);
		updTextPos();
		updPolygon();
		hoverCalc = new HoverCalc(hoverTime, this, false);
		holdCalc = new HoverCalc(holdTime, this, false);
	}

	public void setText(String s) {
		text = s;
		updTextPos();
	}

	public void onBoundsChange() {
		if (getContainer().getHolder() == null)
			return;
		Point mouse = getContainer().getHolder().getMousePos();
		boolean contains = mouse == null ? false : getBoundsR().contains(getContainer().getHolder().getMousePos());
		hoverCalc.focusChanged(contains);
	}

	public void onSizeChange() {
		updTextPos();
		updPolygon();
	}

	private void updTextPos() {
		Rectangle r = getBoundsR();
		FontMetrics m = new Canvas().getFontMetrics(font);
		textPos = new Point(r.width / 2 - m.stringWidth(text) / 2, r.height / 2 + (m.getAscent() - m.getDescent()) / 2);
	}

	private void updPolygon() {
		int parts = 15;

		Rectangle bounds = getBoundsR();
		int pointsAmt = parts * 2 + 2;
		int[] xs = new int[pointsAmt], ys = new int[pointsAmt];
		int h = bounds.height / 2, x1 = h, x2 = bounds.width - h;
		for (int i = 0; i <= parts; i++) {
			double ang = Math.PI * i / parts, sin = Math.sin(ang), cos = Math.cos(ang);
			xs[i] = (int) Math.round(x2 + h * sin);
			ys[i] = (int) Math.round(h - h * cos);

			xs[i + 1 + parts] = (int) Math.round(x1 - h * sin);
			ys[i + 1 + parts] = (int) Math.round(h + h * cos);
		}
		polygon = new Polygon(xs, ys, pointsAmt);
	}

	public void mouseClicked(MouseEvent e, boolean contains) {
		if (contains)
			r.run();
	}

	public void mousePressed(MouseEvent e, boolean contains) {
		if (contains && !hold) {
			hold = true;
			holdCalc.focusChanged(true);
			repaint();
		}
	}

	public void mouseReleased(MouseEvent e, boolean contains) {
		if (hold && holdCalc.getPhase() != 0) {
			hold = false;
			holdCalc.focusChanged(false);
			repaint();
		}
	}

	public void mouseMoved(MouseEvent e, boolean contains) {
		hoverCalc.focusChanged(contains);
	}

	public void mouseDragged(MouseEvent e, boolean contains) {
		mouseMoved(e, contains);
	}

	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.setStroke(new BasicStroke(outlineThickness));
		Rectangle r = getBounds().getBounds();
		double hover = HoverCalc.invSquareSigmoid(hoverCalc.getPhase()), hold = holdCalc.getPhase();
		Polygon p = new Polygon(polygon.xpoints.clone(), polygon.ypoints.clone(), polygon.npoints);
		p.translate(r.x, r.y);

		g2d.setColor(Theme.interpolate(Theme.getBg(), Theme.getFg(), hover * (1 - hold * blackoutFraction)));
		Object antialias = g2d.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		g2d.fillPolygon(p);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, antialias);

		g2d.setColor(Theme.getFg());
		g2d.drawPolygon(p);

		g2d.setColor(Theme.interpolate(Theme.getFg(), Theme.getBg(), hover));
		g2d.setFont(font);
		g2d.drawString(text, textPos.x + r.x, textPos.y + r.y);
	}

	public void onDisplay() {
		hoverCalc.start();
		holdCalc.start();
	}

	public void onShut() {
		hoverCalc.stop();
		hoverCalc.focusChanged(false);
		holdCalc.stop();
		hoverCalc.focusChanged(false);
	}
}

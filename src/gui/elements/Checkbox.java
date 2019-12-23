package gui.elements;

import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;

import gui.Bounds;
import gui.Element;
import gui.HoverCalc;
import gui.Scene;
import gui.Theme;

public class Checkbox extends Element {
	private float checkMarkThickness = 3f;
	private int activationTime = 150;

	private String text;
	private boolean isChosen;
	private Point textPos;
	private float fontSize;
	private Font font;
	private HoverCalc activationCalc;

	public Checkbox(String text, boolean chosen, Bounds b, Scene container) {
		isChosen = chosen;
		this.text = text;
		fontSize = Theme.fontSize;
		font = Theme.interfaceFont.deriveFont(fontSize);
		setBounds(b);
		setContainer(container);
		updTextPos();
		activationCalc = new HoverCalc(activationTime, this, chosen);
	}

	public void onSizeChange() {
		updTextPos();
	}

	private void updTextPos() {
		Rectangle r = getBoundsR();
		FontMetrics m = new Canvas().getFontMetrics(font);
		textPos = new Point(r.height, r.height / 2 + (m.getAscent() - m.getDescent()) / 2);
	}

	public void mousePressed(MouseEvent e, boolean contains) {
		if (contains)
			activationCalc.focusChanged(isChosen = !isChosen);
	}

	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		Rectangle r = getBoundsR();
		g2d.setColor(Theme.getFg());
		g2d.setFont(font);

		g2d.drawRect(r.x + r.height / 4, r.y + r.height / 4, r.height / 2, r.height / 2);
		g2d.drawString(text, r.x + textPos.x, r.y + textPos.y);
		drawCheckMark(g2d);
	}

	public boolean isChosen() {
		return isChosen;
	}

	private void drawCheckMark(Graphics g) {
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.setStroke(new BasicStroke(checkMarkThickness, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL, 0));
		double p = HoverCalc.sineSigmoid(activationCalc.getPhase()), p1 = Math.min(p * 3, 1), p2 = (p * 3 - 1) / 2;
		if (p == 0)
			return;
		Rectangle r = getBoundsR();
		double sx = r.x + r.height / 4, sy = r.y + r.height / 4, d = r.height / 16d;
		double x1 = sx + d, y1 = sy + 4 * d, x2 = sx + 3 * d, y2 = sy + 6 * d, x3 = sx + 7 * d, y3 = sy + 2 * d;
		g2d.draw(new Line2D.Double(x1, y1, x1 * (1 - p1) + x2 * p1, y1 * (1 - p1) + y2 * p1));
		if (p2 > 0)
			g2d.draw(new Line2D.Double(x2, y2, x2 * (1 - p2) + x3 * p2, y2 * (1 - p2) + y3 * p2));
	}

	public void onDisplay() {
		activationCalc.start();
	}

	public void onShut() {
		activationCalc.stop();
	}
}

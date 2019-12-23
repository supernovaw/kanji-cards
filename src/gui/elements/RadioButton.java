package gui.elements;

import java.awt.Canvas;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;

import gui.Bounds;
import gui.Element;
import gui.HoverCalc;
import gui.Scene;
import gui.Theme;

public class RadioButton extends Element {
	private int activationTime = 70;

	private String text;
	private HoverCalc activationCalc;
	private boolean isOn;
	private Point textPos;
	private float fontSize;
	private Font font;
	private ButtonGroup group;

	public RadioButton(String text, boolean chosen, Bounds b, Scene container) {
		isOn = chosen;
		setContainer(container);
		setBounds(b);
		this.text = text;
		activationCalc = new HoverCalc(activationTime, this, chosen);
		fontSize = Theme.fontSize;
		font = Theme.interfaceFont.deriveFont(fontSize);
		updTextPos();
	}

	public void setText(String s) {
		text = s;
	}

	public void mousePressed(MouseEvent e, boolean contains) {
		if (contains)
			group.push(this);
	}

	public void onSizeChange() {
		updTextPos();
	}

	private void updTextPos() {
		Rectangle r = getBoundsR();
		FontMetrics m = new Canvas().getFontMetrics(font);
		textPos = new Point(r.height, r.height / 2 + (m.getAscent() - m.getDescent()) / 2);
	}

	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(Theme.getFg());
		g2d.setFont(font);
		Rectangle r = getBoundsR();
		double rad = r.height * Math.pow(activationCalc.getPhase(), 1 / 3d) / 8d;
		g2d.draw(new Ellipse2D.Double(r.x + r.height / 4, r.y + r.height / 4, r.height / 2, r.height / 2));
		g2d.fill(new Ellipse2D.Double(r.x + r.height / 2d - rad, r.y + r.height / 2d - rad, rad * 2, rad * 2));
		g2d.drawString(text, r.x + textPos.x, r.y + textPos.y);
	}

	public void setOn(boolean isOn) {
		this.isOn = isOn;
		activationCalc.focusChanged(isOn);
	}

	public boolean isOn() {
		return isOn;
	}

	public void setGroup(ButtonGroup g) {
		group = g;
	}

	public void onDisplay() {
		activationCalc.start();
	}

	public void onShut() {
		activationCalc.stop();
	}
}

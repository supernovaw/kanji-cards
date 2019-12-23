package gui.elements;

import java.awt.AlphaComposite;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import cards.KanjiGroup;
import gui.Bounds;
import gui.Element;
import gui.HoverCalc;
import gui.Scene;
import gui.Theme;

public class ProgressDisplay extends Element {
	private int fillChangeDuration;
	private String text;
	private Point textPos;
	private Font font;
	private float fontSize;
	private double fill, oldFill;
	private long fillChangeTime;

	public ProgressDisplay(KanjiGroup group, int fillChangeDuration, Bounds b, Scene container) {
		setBounds(b);
		setContainer(container);

		this.fillChangeDuration = fillChangeDuration;
		text = "";
		fontSize = Theme.fontSize;
		font = Theme.interfaceFont.deriveFont(fontSize);
	}

	private void updTextPos() {
		FontMetrics m = new Canvas().getFontMetrics(font);
		int h = getBounds().getHeight();
		textPos = new Point((getBounds().getWidth() - m.stringWidth(text)) / 2,
				h / 2 + (m.getAscent() - m.getDescent()) / 2);
	}

	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		Rectangle r = getBoundsR();

		double t = (double) (System.currentTimeMillis() - fillChangeTime) / fillChangeDuration;
		double fill;
		if (t < 1) {
			double f = HoverCalc.sineSigmoid(t);
			fill = oldFill * (1 - f) + this.fill * f;
		}
		else
			fill = this.fill;

		BufferedImage img = new BufferedImage(r.width, r.height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d2 = (Graphics2D) img.getGraphics();
		g2d2.setFont(font);
		g2d2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, g2d.getRenderingHint(RenderingHints.KEY_ANTIALIASING));
		g2d2.setColor(Theme.getFg());
		g2d2.fillRect(0, 0, r.width, r.height);
		g2d2.setColor(Theme.getBg());
		g2d2.drawString(text, textPos.x, textPos.y);
		g2d2.setComposite(AlphaComposite.Src);
		g2d2.setColor(new Color(0, true));

		int sx = (int) (fill * r.width);
		g2d2.setClip(sx, 0, r.width - sx, r.height);
		g2d2.fillRect(0, 0, r.width, r.height);
		g2d2.setColor(Theme.getFg());
		g2d2.drawString(text, textPos.x, textPos.y);
		g2d.drawImage(img, r.x, r.y, null);
	}

	public void changeFillFraction(double d, long time) {
		oldFill = fill;
		fill = d;
		fillChangeTime = time;
	}

	public void setFillFraction(double d) {
		fill = d;
	}

	public void setText(String text) {
		this.text = text;
		updTextPos();
	}
}

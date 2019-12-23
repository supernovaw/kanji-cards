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

import gui.Bounds;
import gui.Element;
import gui.HoverCalc;
import gui.RepaintingObject;
import gui.Scene;
import gui.Theme;

public class Label extends Element {
	private int changeTime = 600;

	private String text, fadingText;
	private Point textPos, fadingTextPos;
	private Font font;
	private float fontSize;
	private long lastChange;
	private RepaintingObject changeAnimation;
	private int maxFadingWidth, alignmentX;

	public Label(String text, Bounds b, Scene container) {
		this(text, Theme.fontSize, b, container);
	}

	public Label(String text, float fontSize, Bounds b, Scene container) {
		setBounds(b);
		setContainer(container);
		this.text = text;
		fadingText = "";
		this.fontSize = fontSize;
		font = Theme.interfaceFont.deriveFont(this.fontSize);
		changeAnimation = new RepaintingObject(() -> repaint());
		updTextPos();
	}

	public void setAlignmentX(int x) {
		alignmentX = x;
		updTextPos();
	}

	private int getStringX(int componentWidth, int stringWidth) {
		switch (alignmentX) {
		case -1:
			return 0;
		case 0:
			return (componentWidth - stringWidth) / 2;
		case 1:
			return componentWidth - stringWidth;
		default:
			throw new Error("unsupported alignmentX " + alignmentX);
		}
	}

	private void updTextPos() {
		Rectangle r = getBoundsR();
		FontMetrics m = new Canvas().getFontMetrics(font);
		textPos = new Point(getStringX(r.width, m.stringWidth(text)), r.height / 2 + (m.getAscent() - m.getDescent()) / 2);
		fadingTextPos = new Point(getStringX(r.width, m.stringWidth(fadingText)),
				r.height / 2 + (m.getAscent() - m.getDescent()) / 2);
	}

	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D) g.create();
		Rectangle r = getBoundsR();
		g2d.setColor(Theme.getFg());
		g2d.setFont(font);
		double f = (double) (System.currentTimeMillis() - lastChange) / changeTime;
		if (f > 1) {
			g2d.drawString(text, textPos.x + r.x, textPos.y + r.y);
			changeAnimation.setActive(false);
		} else {
			f = HoverCalc.invSquareSigmoid(f);
			BufferedImage img = new BufferedImage(r.width, r.height, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2d2 = (Graphics2D) img.getGraphics();
			g2d2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					g2d.getRenderingHint(RenderingHints.KEY_ANTIALIASING));
			g2d2.setFont(font);
			g2d2.setColor(Theme.getFg());
			g2d2.drawString(fadingText, fadingTextPos.x, fadingTextPos.y);
			g2d2.setClip(Math.min(textPos.x, fadingTextPos.x), 0, (int) (maxFadingWidth * f), r.height);
			g2d2.setComposite(AlphaComposite.Src);
			g2d2.setColor(new Color(0, 0, 0, 0));
			g2d2.fillRect(0, 0, r.width, r.height);
			g2d2.setColor(Theme.getFg());
			g2d2.drawString(text, textPos.x, textPos.y);
			g2d.drawImage(img, r.x, r.y, null);
		}
	}

	public void onSizeChange() {
		updTextPos();
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
		updTextPos();
	}

	public void changeText(String text) {
		lastChange = System.currentTimeMillis();
		fadingText = this.text;
		this.text = text;
		changeAnimation.setActive(true);
		FontMetrics m = new Canvas().getFontMetrics(font);
		maxFadingWidth = Math.max(m.stringWidth(text), m.stringWidth(fadingText));
		updTextPos();
	}

	public void onDisplay() {
		changeAnimation.add();
	}

	public void onShut() {
		changeAnimation.remove();
	}
}

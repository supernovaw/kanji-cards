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

public class KanjiDisplay extends Element {
	private int changeTime = 475;

	private char kanji, fadingKanji;
	private Point textPos;
	private Font font;
	private float fontSize;
	private RepaintingObject changeAnimation;
	private long lastChange;

	public KanjiDisplay(char kanji, Bounds b, Scene container) {
		setBounds(b);
		setContainer(container);
		this.kanji = kanji;
		updTextPos();
		changeAnimation = new RepaintingObject(() -> repaint());
	}

	public void onSizeChange() {
		updTextPos();
	}

	private void updTextPos() {
		fontSize = getBounds().getHeight();
		font = Theme.japaneseFont.deriveFont(fontSize);
		FontMetrics m = new Canvas().getFontMetrics(font);
		textPos = new Point(0, m.getAscent());
	}

	public void setChar(char c) {
		kanji = c;
	}

	public void changeChar(char newChar) {
		fadingKanji = kanji;
		kanji = newChar;
		lastChange = System.currentTimeMillis();
		changeAnimation.setActive(true);
	}

	public void paint(Graphics g) {
		Rectangle r = getBoundsR();
		double phase = (double) (System.currentTimeMillis() - lastChange) / changeTime;
		if (phase > 1) {
			changeAnimation.setActive(false);
			g.setColor(Theme.getFg());
			g.setFont(font);
			g.drawString(Character.toString(kanji), r.x + textPos.x, r.y + textPos.y);
		} else {
			BufferedImage img = new BufferedImage(r.width, r.height, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2d = (Graphics2D) img.getGraphics();
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					((Graphics2D) g).getRenderingHint(RenderingHints.KEY_ANTIALIASING));
			g2d.setColor(Theme.getFg());
			g2d.setFont(font);
			g2d.drawString(Character.toString(kanji), textPos.x, textPos.y);
			int f = (int) (r.width * HoverCalc.invSquareSigmoid(phase));
			g2d.setClip(f, 0, r.width - f, r.height);
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC));
			g2d.setColor(new Color(0, 0, 0, 0));
			g2d.fillRect(0, 0, r.width, r.height);
			g2d.setColor(Theme.getFg());
			g2d.drawString(Character.toString(fadingKanji), textPos.x, textPos.y);
			g.drawImage(img, r.x, r.y, null);
		}
	}

	public void onDisplay() {
		changeAnimation.add();
	}

	public void onShut() {
		changeAnimation.remove();
	}
}

package gui.elements;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;

import cards.Cards;
import cards.Kanji;
import gui.Bounds;
import gui.Element;
import gui.HoverCalc;
import gui.RepaintingObject;
import gui.Scene;
import gui.Theme;

public class KanjiWheel extends Element {
	private float kanjiSize = 100f, fontSize = 60f, outlineWidth = 4f;
	private int hoverTime = 400, changeTime = 300;

	private int kanjiAmt, kanjiAimed = -1;
	private Point[] kanjiPositions;
	private char[] kanjiCharacters, fadingKanji;
	private Point kanjiPos;
	private Font kanjiFont, font;
	private HoverCalc[] kanjiCalcs;
	private double[] startAngles;
	private long answerTime;
	private RepaintingObject rep;
	private String text, fadingText;
	private Point textPos, fadingTextPos;
	private int maxFadingWidth;
	private Runnable onAnswer;
	private boolean isAnswerCorrect;

	public KanjiWheel(int kanjiAmt, Runnable onAnswer, Bounds b, Scene container) {
		setBounds(b);
		setContainer(container);
		kanjiFont = Theme.japaneseFont.deriveFont(kanjiSize);
		this.onAnswer = onAnswer;
		font = Theme.interfaceFont.deriveFont(fontSize);
		rep = new RepaintingObject(() -> repaint());
		this.kanjiAmt = kanjiAmt;

		kanjiPositions = new Point[kanjiAmt];
		double startAng = ((kanjiAmt - 2) % 4 == 0) ? Math.PI / kanjiAmt : 0, step = Math.PI * 2 / kanjiAmt,
				cx = b.getWidth() / 2d, cy = b.getHeight() / 2d, r = (b.getHeight() - kanjiSize) / 2d;
		for (int i = 0; i < kanjiAmt; i++) {
			double ang = startAng + step * i;
			kanjiPositions[i] = new Point((int) Math.round(cx + Math.sin(ang) * r),
					(int) Math.round(cy - Math.cos(ang) * r));
		}

		text = "";
		setNewKanji();
		updTextPos();
		updKanjiPos();

		kanjiCalcs = new HoverCalc[kanjiAmt];
		for (int i = 0; i < kanjiAmt; i++) {
			final int index = i;
			kanjiCalcs[i] = new HoverCalc(hoverTime, () -> {
				Rectangle rect = getBoundsR();
				repaint(new Rectangle(rect.x + kanjiPositions[index].x - (int) kanjiSize,
						rect.y + kanjiPositions[index].y - (int) kanjiSize, (int) (kanjiSize * 2),
						(int) (kanjiSize * 2)));
			}, false);
		}

		startAngles = new double[kanjiAmt];
		for (int i = 0; i < kanjiAmt; i++)
			startAngles[i] = Math.PI * Math.random();
	}

	public void changeKanjiAmt(int kanjiAmt) {
		this.kanjiAmt = kanjiAmt;
		Bounds b = getBounds();

		kanjiPositions = new Point[kanjiAmt];
		double startAng = ((kanjiAmt - 2) % 4 == 0) ? Math.PI / kanjiAmt : 0, step = Math.PI * 2 / kanjiAmt,
				cx = b.getWidth() / 2d, cy = b.getHeight() / 2d, r = (b.getHeight() - kanjiSize) / 2d;
		for (int i = 0; i < kanjiAmt; i++) {
			double ang = startAng + step * i;
			kanjiPositions[i] = new Point((int) Math.round(cx + Math.sin(ang) * r),
					(int) Math.round(cy - Math.cos(ang) * r));
		}

		setNewKanji();
		updTextPos();
		updKanjiPos();

		kanjiCalcs = new HoverCalc[kanjiAmt];
		for (int i = 0; i < kanjiAmt; i++) {
			final int index = i;
			kanjiCalcs[i] = new HoverCalc(hoverTime, () -> {
				Rectangle rect = getBoundsR();
				repaint(new Rectangle(rect.x + kanjiPositions[index].x - (int) kanjiSize,
						rect.y + kanjiPositions[index].y - (int) kanjiSize, (int) (kanjiSize * 2),
						(int) (kanjiSize * 2)));
			}, false);
		}

		startAngles = new double[kanjiAmt];
		for (int i = 0; i < kanjiAmt; i++)
			startAngles[i] = Math.PI * Math.random();
	}

	public int getKanjiAmt() {
		return kanjiAmt;
	}

	private void updKanjiPos() {
		FontMetrics m = new Canvas().getFontMetrics(kanjiFont);
		kanjiPos = new Point((int) (-kanjiSize / 2), m.getAscent() - (int) (kanjiSize / 2));
	}

	private void updTextPos() {
		Rectangle r = getBoundsR();
		FontMetrics m = new Canvas().getFontMetrics(font);
		textPos = new Point(r.width / 2 - m.stringWidth(text) / 2, r.height / 2 + (m.getAscent() - m.getDescent()) / 2);
		fadingTextPos = new Point(r.width / 2 - m.stringWidth(fadingText) / 2,
				r.height / 2 + (m.getAscent() - m.getDescent()) / 2);
	}

	public void mousePressed(MouseEvent e, boolean contains) {
		if (kanjiAimed != -1 && System.currentTimeMillis() - answerTime > changeTime) {
			isAnswerCorrect = kanjiCharacters[kanjiAimed] == Cards.getCurrentKanji().character;
			onAnswer.run();

			kanjiCalcs[kanjiAimed].focusChanged(false);
			answerTime = System.currentTimeMillis();
			fadingKanji = kanjiCharacters;
			setNewKanji();
			FontMetrics m = new Canvas().getFontMetrics(font);
			maxFadingWidth = Math.max(m.stringWidth(text), m.stringWidth(fadingText));
			updTextPos();
			rep.setActive(true);
		}
	}

	public void mouseMoved(MouseEvent e, boolean contains) {
		Rectangle r = getBoundsR();
		Point p = new Point(e.getX() - r.x, e.getY() - r.y);
		double smallestDist = Math.hypot(p.x - kanjiPositions[0].x, p.y - kanjiPositions[0].y);
		int index = 0;
		for (int i = 1; i < kanjiAmt; i++) {
			double d = Math.hypot(p.x - kanjiPositions[i].x, p.y - kanjiPositions[i].y);
			if (d < smallestDist) {
				smallestDist = d;
				index = i;
			}
		}
		if (smallestDist > 150)
			index = -1;
		kanjiAimed = index;
		if (System.currentTimeMillis() - answerTime > changeTime)
			for (int i = 0; i < kanjiAmt; i++)
				kanjiCalcs[i].focusChanged(i == index);
	}

	public void mouseDragged(MouseEvent e, boolean contains) {
		mouseMoved(e, contains);
	}

	private void setNewKanji() {
		kanjiCharacters = new char[kanjiAmt];
		for (int i = 0, j = (int) (Math.random() * kanjiAmt); i < kanjiAmt; i++) {
			if (i == j) {
				Kanji k = Cards.getCurrentKanji();
				kanjiCharacters[i] = k.character;
				fadingText = text;
				text = k.getMeaning();
			} else
				kanjiCharacters[i] = Cards.getUnbiasedRandomKanji().character;
		}
	}

	public void paint(Graphics g) {
		Rectangle r = getBoundsR();
		g.setColor(Theme.getFg());
		g.setFont(kanjiFont);

		if (!rep.isActive()) {
			for (int i = 0; i < kanjiAmt; i++)
				g.drawString(Character.toString(kanjiCharacters[i]), r.x + kanjiPositions[i].x + kanjiPos.x,
						r.y + kanjiPositions[i].y + kanjiPos.y);
			g.setFont(font);
			g.drawString(text, r.x + textPos.x, r.y + textPos.y);
		} else {
			Graphics2D g2d = (Graphics2D) g;
			double t = (double) (System.currentTimeMillis() - answerTime) / changeTime;
			if (t > 2)
				rep.setActive(false);
			if (t > 1) {
				t = 1;
				if (kanjiAimed != -1)
					kanjiCalcs[kanjiAimed].focusChanged(true);
			}
			t = HoverCalc.invSquareSigmoid(t);

			int shift = (kanjiAmt - 1) / 2, xs[] = new int[kanjiAmt], ys[] = new int[kanjiAmt];
			for (int i = 0; i < kanjiAmt; i++) {
				int i2 = (i + shift) % kanjiAmt;
				xs[i] = (int) (kanjiPositions[i].x * (1 - t) + kanjiPositions[i2].x * t) + r.x + kanjiPos.x;
				ys[i] = (int) (kanjiPositions[i].y * (1 - t) + kanjiPositions[i2].y * t) + r.y + kanjiPos.y;
			}

			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1 - (float) t));
			for (int i = 0; i < kanjiAmt; i++)
				g.drawString(Character.toString(fadingKanji[i]), xs[i], ys[i]);
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) t));
			for (int i = 0; i < kanjiAmt; i++) {
				int i2 = (i + shift) % kanjiAmt;
				g.drawString(Character.toString(kanjiCharacters[i2]), xs[i], ys[i]);
			}
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));

			BufferedImage textImg = new BufferedImage(r.width, r.height, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2d2 = (Graphics2D) textImg.getGraphics();
			g2d2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					g2d.getRenderingHint(RenderingHints.KEY_ANTIALIASING));
			g2d2.setFont(font);
			g2d2.setColor(Theme.getFg());
			g2d2.drawString(fadingText, fadingTextPos.x, fadingTextPos.y);
			g2d2.setClip((r.width - maxFadingWidth) / 2, 0, (int) (t * maxFadingWidth), r.height);
			g2d2.setComposite(AlphaComposite.Src);
			g2d2.setColor(new Color(0, 0, 0, 0));
			g2d2.fillRect(0, 0, r.width, r.height);
			g2d2.setColor(Theme.getFg());
			g2d2.drawString(text, textPos.x, textPos.y);

			g2d.drawImage(textImg, r.x, r.y, null);
		}

		Graphics2D g2d = (Graphics2D) g.create();
		g2d.setStroke(new BasicStroke(outlineWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		for (int i = 0; i < kanjiAmt; i++)
			drawHoverAnimation(g2d, i);
	}

	private void drawHoverAnimation(Graphics2D g, int index) {
		Rectangle rect = getBoundsR();
		double f = kanjiCalcs[index].getPhase();
		f = HoverCalc.sineSigmoid(f);
		if (f == 0)
			return;
		int parts = 20, x = rect.x + kanjiPositions[index].x, y = rect.y + kanjiPositions[index].y;
		double step = Math.PI / parts, dest = f * Math.PI, r = kanjiSize / 1.7, start = startAngles[index];
		for (double ang = step; ang < dest; ang += step) {
			double x1 = r * Math.cos(ang - step + start), x2 = r * Math.cos(ang + start),
					y1 = r * Math.sin(ang - step + start), y2 = r * Math.sin(ang + start);
			g.draw(new Line2D.Double(x + x1, y + y1, x + x2, y + y2));
			g.draw(new Line2D.Double(x - x1, y - y1, x - x2, y - y2));
		}
		double ang1 = dest - dest % step;
		if (f == 1)
			ang1 = Math.PI - step;
		else if (ang1 > Math.PI - step / 2)
			ang1 -= step;
		double x1 = r * Math.cos(ang1 + start), y1 = r * Math.sin(ang1 + start);
		double x2 = r * Math.cos(ang1 + step + start), y2 = r * Math.sin(ang1 + step + start);
		double x3 = x1 * (1 - f) + x2 * f, y3 = y1 * (1 - f) + y2 * f;
		g.draw(new Line2D.Double(x + x1, y + y1, x + x3, y + y3));
		g.draw(new Line2D.Double(x - x1, y - y1, x - x3, y - y3));
	}

	public boolean isAnswerCorrect() {
		return isAnswerCorrect;
	}

	public void upd() {
		setNewKanji();
		text = Cards.getCurrentKanji().getMeaning();
		updTextPos();
	}

	public void onDisplay() {
		rep.add();
		for (HoverCalc c : kanjiCalcs)
			c.start();
	}

	public void onShut() {
		rep.remove();
		for (HoverCalc c : kanjiCalcs)
			c.stop();
	}
}

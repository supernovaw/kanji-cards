package gui.elements;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import gui.Bounds;
import gui.Element;
import gui.HoverCalc;
import gui.RepaintingObject;
import gui.Scene;
import gui.Theme;
import recognition.Drawing;
import recognition.Stroke;

public class KanjiStrokesPicker extends Element {
	private float lineThickness = 15f, strokeAlpha = 0.3f;
	private int pointFillTime = 200, strokeFillTime = 150, blinkTime = 200, changeDuration = 400;
	private double mistakeDotRadius = 40d, maxDistFromDot = 50d; // maxDist is in terms of kanji coords (in file),
																	// changed when scaling

	private RepaintingObject rep;
	private Drawing drawing, fadingDrawing;
	private HoverCalc[] pointCalcs;
	private int strokesAmt, pickedStrokes;
	private long pickTime, mistakeTime, changeTime;
	private Point mistakePoint;
	private Runnable run;
	private boolean completed, mistaken;

	public KanjiStrokesPicker(Drawing d, Runnable r, Bounds b, Scene container) {
		setBounds(b);
		setContainer(container);

		rep = new RepaintingObject(() -> repaint());
		run = r;
		setDrawing(d);
	}

	public boolean hasMistaken() {
		return mistaken;
	}

	public void setDrawing(Drawing d) {
		fadingDrawing = drawing;
		drawing = d;
		strokesAmt = d.getStrokesCount();
		pointCalcs = new HoverCalc[strokesAmt * 2];
		for (int i = 0; i < strokesAmt * 2; i++) {
			pointCalcs[i] = new HoverCalc(pointFillTime, this, false);
			pointCalcs[i].start();
		}
		completed = false;
		mistaken = false;
		pickedStrokes = 0;
	}

	public void changeDrawing(Drawing d) {
		changeTime = System.currentTimeMillis();
		setDrawing(d);
		rep.setActive(true);
	}

	public void mousePressed(MouseEvent e, boolean contains) {
		int dot = findDot(e.getPoint());
		if (dot == -1)
			return;
		long time = System.currentTimeMillis();
		if (dot == pickedStrokes * 2) {
			pointCalcs[pickedStrokes].focusChanged(false);
			pointCalcs[pickedStrokes + 1].focusChanged(false);
			pickedStrokes++;
			pickTime = time;
			updHoverCalcs(getContainer().getHolder().getMousePos());
		} else {
			mistaken = true;
			if (time - mistakeTime > blinkTime) {
				mistakeTime = time;
				mistakePoint = e.getPoint();
			}
		}
		rep.setActive(true);
	}

	private void updHoverCalcs(Point2D m) {
		int d = findDot(m);
		for (int i = 0; i < strokesAmt * 2; i++)
			pointCalcs[i].focusChanged(i == d);
	}

	public void mouseMoved(MouseEvent e, boolean contains) {
		updHoverCalcs(e.getPoint());
	}

	public void mouseDragged(MouseEvent e, boolean contains) {
		updHoverCalcs(e.getPoint());
	}

	private int findDot(Point2D m) {
		if (!getBoundsR().contains(m))
			return -1;
		if (strokesAmt == pickedStrokes)
			return -1;
		Point2D tm;
		try {
			tm = Stroke.getTransormForStrokes(getBoundsR()).inverseTransform(m, null);
		} catch (Exception e) {
			e.printStackTrace();
			tm = null;
		}

		Stroke[] ss = drawing.getStrokes();
		double[] dist = new double[2 * strokesAmt];
		for (int i = 0; i < strokesAmt; i++) {
			dist[2 * i] = tm.distance(ss[i].getStart());
			dist[2 * i + 1] = tm.distance(ss[i].getEnd());
		}
		int j = pickedStrokes * 2;
		for (int i = pickedStrokes * 2; i < dist.length; i++)
			if (dist[i] < dist[j])
				j = i;
		if (dist[j] > maxDistFromDot)
			return -1;
		return j;
	}

	private void paintDot(Point2D p, AffineTransform at, double phase, Graphics2D g2d) {
		double d = HoverCalc.invSquareSigmoid(phase) * lineThickness;
		double sc = at.getScaleX();
		g2d.fill(new Ellipse2D.Double(at.getTranslateX() + sc * p.getX() - d / 2,
				at.getTranslateY() + sc * p.getY() - d / 2, d, d));
	}

	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		Rectangle r = getBoundsR();
		g2d.setColor(Theme.getFg());
		Graphics2D sg = Stroke.getGraphicsToPaintStrokes(g2d, r, lineThickness);

		long time = System.currentTimeMillis();
		double t = (double) (time - pickTime) / strokeFillTime, ct = (double) (time - changeTime) / changeDuration,
				mistakeT = (double) (time - mistakeTime) / blinkTime;
		float changeAlpha = ct > 1 ? 1 : (float) HoverCalc.sineSigmoid(ct);

		if (mistakeT > 2 && t > 2 && ct > 2)
			rep.setActive(false);

		if (ct < 1) {
			Stroke[] ss = fadingDrawing.getStrokes();
			sg.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1 - changeAlpha));
			for (Stroke s : ss)
				sg.draw(s.getPath());
			sg.setComposite(AlphaComposite.SrcOver);
		}

		if (mistakeT < 1) {
			int size = (int) (2 * mistakeDotRadius) + 5;
			BufferedImage circle = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2 = (Graphics2D) circle.getGraphics();
			double r1 = mistakeDotRadius * (2 * mistakeT - mistakeT * mistakeT),
					r2 = mistakeDotRadius * (mistakeT * mistakeT);
			g2.setColor(Theme.secondary);
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, g2d.getRenderingHint(RenderingHints.KEY_ANTIALIASING));
			g2.fill(new Ellipse2D.Double(size / 2 - r1, size / 2 - r1, r1 * 2, r1 * 2));
			g2.setColor(new Color(0, true));
			g2.setComposite(AlphaComposite.Src);
			g2.fill(new Ellipse2D.Double(size / 2 - r2, size / 2 - r2, r2 * 2, r2 * 2));
			g2d.setClip(r.x, r.y, r.width + 1, r.height + 1);
			g2d.drawImage(circle, mistakePoint.x - size / 2, mistakePoint.y - size / 2, null);
		}

		Stroke[] ss = drawing.getStrokes();
		for (int i = 0; i < pickedStrokes - 1; i++)
			sg.draw(ss[i].getPath());

		if (t < 1) {
			int i = pickedStrokes - 1;
			sg.draw(ss[i].trimPathFromEnd(t));
			double ph = Math.max(pointCalcs[2 * i].getPhase(), pointCalcs[2 * i + 1].getPhase());
			float a = strokeAlpha + (1 - strokeAlpha) * (float) ph / 3;
			sg.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, a * changeAlpha));
			sg.draw(ss[i].trimPathFromStart(t));
			sg.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, changeAlpha));
		} else if (pickedStrokes != 0)
			sg.draw(ss[pickedStrokes - 1].getPath());

		float alpha = strokeAlpha;
		sg.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha * changeAlpha));
		for (int i = pickedStrokes; i < strokesAmt; i++) {
			double ph = Math.max(pointCalcs[2 * i].getPhase(), pointCalcs[2 * i + 1].getPhase());
			ph = HoverCalc.sineSigmoid(ph);
			float a = strokeAlpha + (1 - strokeAlpha) * (float) ph / 3;
			if (a != alpha)
				sg.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, a * changeAlpha));
			alpha = a;

			sg.draw(ss[i].getPath());
		}

		AffineTransform at = Stroke.getTransormForStrokes(r);
		for (int i = 0; i < strokesAmt; i++) {
			paintDot(ss[i].getStart(), at, pointCalcs[2 * i].getPhase(), g2d);
			paintDot(ss[i].getEnd(), at, pointCalcs[2 * i + 1].getPhase(), g2d);
		}

		if (pickedStrokes == strokesAmt && t > 1 && !completed) {
			completed = true;
			run.run();
		}
	}

	public void onDisplay() {
		rep.add();
		for (HoverCalc c : pointCalcs)
			c.start();
	}

	public void onShut() {
		rep.remove();
		for (HoverCalc c : pointCalcs)
			c.stop();
	}
}

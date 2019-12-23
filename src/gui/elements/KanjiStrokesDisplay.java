package gui.elements;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import gui.Bounds;
import gui.Element;
import gui.HoverCalc;
import gui.RepaintingObject;
import gui.Scene;
import gui.Theme;
import recognition.Drawing;
import recognition.Stroke;

public class KanjiStrokesDisplay extends Element {
	private float lineThickness = 5f;
	private int strokeDrawingTime = 350, changeDuration = 500;

	private Drawing drawing, fadingDrawing;
	private long changeTime, fadingTiming;
	private RepaintingObject rep;

	public KanjiStrokesDisplay(Bounds b, Scene container) {
		setBounds(b);
		setContainer(container);

		changeTime = System.currentTimeMillis();
		rep = new RepaintingObject(() -> repaint());
		rep.setActive(true);
	}

	public void setDrawing(Drawing d) {
		drawing = d;
	}

	public void changeDrawing(Drawing d) {
		fadingDrawing = drawing;
		fadingTiming = changeTime;
		drawing = d;
		changeTime = System.currentTimeMillis();
		if (drawing != null)
			rep.setActive(true);
	}

	private void paintDrawing(Drawing d, double t, Graphics g, Rectangle br) {
		t %= d.getStrokesCount();
		int full = (int) t;
		t -= full;
		t = HoverCalc.sineSigmoid(t);

		g.setColor(Theme.getFg());
		Graphics2D g2d = Stroke.getGraphicsToPaintStrokes(g, br, lineThickness);
		Stroke[] strokes = d.getStrokes();
		for (int i = 0; i < full; i++)
			g2d.draw(strokes[i].getPath());
		g2d.draw(strokes[full].trimPathFromEnd(t));
	}

	public void paint(Graphics g) {
		long time = System.currentTimeMillis();
		Rectangle r = getBoundsR();
		Graphics2D g2d = (Graphics2D) g;
		double f = (double) (time - changeTime) / changeDuration;
		boolean fade = f < 1;
		if (f > 2 && drawing == null)
			rep.setActive(false);

		if (drawing != null) {
			double t = (double) (time - changeTime) / strokeDrawingTime;
			if (fade)
				g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) f));
			paintDrawing(drawing, t, g2d, r);
			if (fade)
				g2d.setComposite(AlphaComposite.SrcOver);
		}
		if (fade && fadingDrawing != null) {
			double t = (double) (time - fadingTiming) / strokeDrawingTime;
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1 - (float) f));
			paintDrawing(fadingDrawing, t, g2d, r);
			g2d.setComposite(AlphaComposite.SrcOver);
		}
	}

	public void onDisplay() {
		rep.add();
	}

	public void onShut() {
		rep.remove();
	}
}

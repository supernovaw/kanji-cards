package gui.elements;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import javax.swing.SwingUtilities;

import gui.Bounds;
import gui.Element;
import gui.HoverCalc;
import gui.RepaintingObject;
import gui.Scene;
import gui.Theme;
import recognition.Drawing;
import recognition.Stroke;

public class KanjiDrawArea extends Element {
	private float lineThickness = 3f, defaultOutlineAlpha = 0.3f;
	private int blinkTime = 300, strokeFadeoutTime = 200;

	private Drawing drawing;
	private Runnable r;
	private boolean mouseHoldInside;
	private long borderQuitTime, strokeRemoveTime;
	private RepaintingObject borderFlasher, strokeFadeRep;
	private Stroke fadingStroke;

	public KanjiDrawArea(Runnable r, Bounds b, Scene container) {
		setBounds(b);
		setContainer(container);

		drawing = new Drawing();
		this.r = r;
		borderFlasher = new RepaintingObject(() -> repaint());
		strokeFadeRep = new RepaintingObject(() -> repaint());
	}

	public Drawing getDrawingAndCleanUp() {
		Drawing d = drawing;
		drawing = new Drawing();
		fadingStroke = null;
		repaint();
		return d;
	}

	private void removeStroke() {
		fadingStroke = drawing.removeStroke();
		if (fadingStroke != null) {
			strokeFadeRep.setActive(true);
			strokeRemoveTime = System.currentTimeMillis();
		}
	}

	public void mousePressed(MouseEvent e, boolean contains) {
		Rectangle r = getBoundsR();
		if (!r.contains(e.getPoint()))
			return;
		if (e.getButton() == 1) {
			Rectangle rShifted = r.getBounds();
			rShifted.grow(-(int) (lineThickness / 2), -(int) (lineThickness / 2));
			if (rShifted.contains(e.getPoint())) {
				mouseHoldInside = true;
				drawing.newStroke(new Point2D.Double(e.getX() - r.x, e.getY() - r.y));
			}
		} else if (e.getButton() == 3)
			removeStroke();
		else if (e.getButton() == 2)
			this.r.run();
	}

	public void mouseReleased(MouseEvent e, boolean contains) {
		mouseHoldInside = false;
	}

	public void mouseDragged(MouseEvent e, boolean contains) {
		Rectangle r = getBoundsR(), rShifted = r.getBounds();
		rShifted.grow(-(int) (lineThickness / 2), -(int) (lineThickness / 2));
		if (!SwingUtilities.isLeftMouseButton(e))
			return;
		if (rShifted.contains(e.getPoint())) {
			if (mouseHoldInside) {
				drawing.mouseDragged(new Point2D.Double(e.getX() - r.x, e.getY() - r.y));
				repaint();
			}
		} else {
			long time = System.currentTimeMillis();
			if (time - borderQuitTime > blinkTime && mouseHoldInside) {
				borderQuitTime = time;
				borderFlasher.setActive(true);
				mouseHoldInside = false;
			}
		}
	}

	public void paint(Graphics g) {
		Rectangle r = getBoundsR();
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.setColor(Color.gray);
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, defaultOutlineAlpha));
		g2d.drawRect(r.x, r.y, r.width, r.height);

		long time = System.currentTimeMillis();
		double t = (double) (time - borderQuitTime) / blinkTime;
		if (t > 2)
			borderFlasher.setActive(false);
		if (t < 1) {
			t = 2 * t;
			if (t > 1)
				t = 2 - t;
			t = HoverCalc.invSquareSigmoid(t);
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) t));
			g2d.setColor(Theme.secondary);
			g2d.drawRect(r.x, r.y, r.width, r.height);
		}

		g2d.setComposite(AlphaComposite.SrcOver);
		g2d.setColor(Theme.getFg());
		g2d.setStroke(new BasicStroke(lineThickness, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		g2d.translate(r.x, r.y);
		for (Stroke s : drawing.getStrokes())
			g2d.draw(s.getPath());

		t = (double) (time - strokeRemoveTime) / strokeFadeoutTime;
		if (t > 2)
			strokeFadeRep.setActive(false);
		if (t < 1)
			if (fadingStroke == null)
				strokeFadeRep.setActive(false);
			else
				g2d.draw(fadingStroke.trimPathFromStart(HoverCalc.invSquareSigmoid(t)));
	}

	public void onDisplay() {
		borderFlasher.add();
		strokeFadeRep.add();
	}

	public void onShut() {
		borderFlasher.remove();
		strokeFadeRep.remove();
	}
}

package recognition;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;

public class Stroke {
	private static double kanjiScale = 0.9d;

	private ArrayList<Point2D> path;
	private ArrayList<Double> dist;
	private GeneralPath graphicsPath;
	private double length;

	public Stroke(Point2D start) {
		path = new ArrayList<>();
		dist = new ArrayList<>();
		path.add(start);
		graphicsPath = new GeneralPath();
	}

	public Stroke(Point2D[] path) {
		this.path = new ArrayList<>(Arrays.asList(path));
		dist = new ArrayList<>();
		for (int i = 1; i < path.length; i++) {
			double d = path[i - 1].distance(path[i]);
			dist.add(d);
			length += d;
		}
		graphicsPath = new GeneralPath();
		for (int i = 1; i < path.length; i++)
			graphicsPath.append(new Line2D.Double(path[i - 1], path[i]), true);
	}

	public void addPoint(Point2D p) {
		Point2D end = getEnd();
		graphicsPath.append(new Line2D.Double(end, p), true);
		path.add(p);
		double d = end.distance(p);
		dist.add(d);
		length += d;
	}

	public Point2D[] getPoints() {
		return path.toArray(new Point2D[path.size()]);
	}

	public int getSize() {
		return path.size();
	}

	public Point2D getStart() {
		return path.get(0);
	}

	public Point2D getEnd() {
		return path.get(path.size() - 1);
	}

	public GeneralPath getPath() {
		return graphicsPath;
	}

	public GeneralPath trimPathFromEnd(double t) {
		GeneralPath p = new GeneralPath();
		t *= length;
		int index = -1;
		for (int i = 0; i < dist.size(); i++) {
			if (t <= dist.get(i)) {
				index = i;
				t /= dist.get(i);
				break;
			}
			t -= dist.get(i);
		}
		if (index == -1)
			return graphicsPath;
		Point2D[] pts = path.toArray(new Point2D[path.size()]);
		Point2D last = pts[0];
		for (int i = 1; i < index; i++)
			p.append(new Line2D.Double(last, last = pts[i]), true);
		p.append(new Line2D.Double(last, new Point2D.Double(pts[index].getX() * (1 - t) + pts[index + 1].getX() * t,
				pts[index].getY() * (1 - t) + pts[index + 1].getY() * t)), true);
		return p;
	}

	public GeneralPath trimPathFromStart(double t) {
		GeneralPath p = new GeneralPath();
		t *= length;
		int index = -1;
		for (int i = 0; i < dist.size(); i++) {
			if (t <= dist.get(i)) {
				index = i;
				t /= dist.get(i);
				break;
			}
			t -= dist.get(i);
		}
		if (index == -1)
			return new GeneralPath();
		Point2D[] pts = path.toArray(new Point2D[path.size()]);
		Point2D last = new Point2D.Double(pts[index].getX() * (1 - t) + pts[index + 1].getX() * t,
				pts[index].getY() * (1 - t) + pts[index + 1].getY() * t);
		for (int i = index + 1; i < pts.length; i++)
			p.append(new Line2D.Double(last, last = pts[i]), true);
		return p;
	}

	public double getLength() {
		return length;
	}

	public static AffineTransform getTransormForStrokes(Rectangle r) {
		int size = Math.min(r.width, r.height);
		double sc = kanjiScale * size / 100;
		AffineTransform at = new AffineTransform();
		at.translate(r.x + r.width / 2, r.y + r.height / 2);
		at.scale(sc, sc);
		at.translate(-50, -50);
		return at;
	}

	public static Graphics2D getGraphicsToPaintStrokes(Graphics g, Rectangle r, float thickness) {
		Graphics2D g2d = (Graphics2D) g.create();
		AffineTransform at = getTransormForStrokes(r);
		g2d.transform(at);
		g2d.setStroke(
				new BasicStroke(thickness / (float) at.getScaleX(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		return g2d;
	}
}

package recognition;

import java.awt.geom.Point2D;
import java.util.ArrayList;

public class Drawing {
	private ArrayList<Stroke> strokes;

	public Drawing() {
		strokes = new ArrayList<>();
	}

	public Drawing(String[] lines, int start) {
		this();
			int strokesCount = Integer.parseInt(lines[1 + start].substring(9));
			for (int i = 0; i < strokesCount; i++) {
				String s = lines[2 + i + start];
				ArrayList<Point2D> points = new ArrayList<>();
				int sp = s.indexOf(' ');
				int len = Integer.parseInt(s.substring(0, sp));
				String[] split = s.substring(sp).split("\\)");
				for (int j = 0; j < len; j++)
					addPoints(split[j], points, 15, j == len - 1);
				strokes.add(new Stroke(points.toArray(new Point2D[points.size()])));
			}
	}

	public static int newIndex(String[] lines, int start) {
		int strokesCount = Integer.parseInt(lines[1 + start].substring(9));
		return strokesCount + 2 + start;
	}

	private static Point2D b(Point2D p1, Point2D p2, double f) {
		return new Point2D.Double(p1.getX() * (1 - f) + p2.getX() * f, p1.getY() * (1 - f) + p2.getY() * f);
	}

	private static Point2D b(Point2D p1, Point2D p2, Point2D p3, double f) {
		return b(b(p1, p2, f), b(p2, p3, f), f);
	}

	private static Point2D b(Point2D p1, Point2D p2, Point2D p3, Point2D p4, double f) {
		Point2D r1 = b(p1, p2, f), r2 = b(p2, p3, f), r3 = b(p3, p4, f);
		return b(r1, r2, r3, f);
	}

	private static void addPoints(String curve, ArrayList<Point2D> list, int pts, boolean addLast) {
		char c = curve.charAt(1);
		if (c != 'c' && c != 'q')
			throw new IllegalArgumentException('"' + curve + "\".charAt(1) is neither 'c' or 'q'");
		String[] split = curve.substring(3).split(" ");
		Point2D[] ps = new Point2D[split.length];
		for (int i = 0; i < ps.length; i++) {
			int index = split[i].indexOf(';');
			ps[i] = new Point2D.Double(Double.parseDouble(split[i].substring(0, index)),
					Double.parseDouble(split[i].substring(index + 1)));
		}
		if (c == 'c') {
			for (int i = 0; i < pts; i++)
				list.add(b(ps[0], ps[1], ps[2], ps[3], (double) i / pts));
			if (addLast)
				list.add(ps[3]);
		} else if (c == 'q') {
			for (int i = 0; i < pts; i++)
				list.add(b(ps[0], ps[1], ps[2], (double) i / pts));
			if (addLast)
				list.add(ps[2]);
		} else
			throw new IllegalArgumentException(curve);
	}

	public void newStroke(Point2D p) {
		if (!strokes.isEmpty() && strokes.get(strokes.size() - 1).getSize() == 1)
			strokes.set(strokes.size() - 1, new Stroke(p));
		else
			strokes.add(new Stroke(p));
	}

	public void mouseDragged(Point2D p) {
		if (!strokes.isEmpty())
			strokes.get(strokes.size() - 1).addPoint(p);
	}

	public Stroke removeStroke() {
		if (strokes.isEmpty())
			return null;
		if (strokes.get(0).getSize() <= 1) {
			strokes.remove(strokes.size() - 1);
			return null;
		}
		return strokes.remove(strokes.size() - 1);
	}

	public Stroke[] getStrokes() {
		return strokes.toArray(new Stroke[strokes.size()]);
	}

	public int getStrokesCount() {
		return strokes.size();
	}
}

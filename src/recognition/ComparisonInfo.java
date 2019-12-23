package recognition;

import java.awt.geom.Point2D;

public class ComparisonInfo {
	private int strokes;
	private double[][] angles;
	private Connection[] connections;

	public ComparisonInfo(Drawing d) {
		strokes = d.getStrokesCount();
		Stroke[] ss = d.getStrokes();
		angles = new double[strokes][];
		for (int i = 0; i < strokes; i++)
			angles[i] = createAngles(ss[i]);
		connections = new Connection[Math.max(strokes - 1, 0)];
		for (int i = 0; i < connections.length; i++)
			connections[i] = new Connection(ss[i], ss[i + 1]);
	}

	public static ComparisonResult compare(char kanji, ComparisonInfo c1, ComparisonInfo c2) {
		if (c1.strokes != c2.strokes)
			return new ComparisonResult(kanji);
		int strokes = c1.strokes;
		double angDiff = 0;
		for (int i = 0; i < strokes; i++)
			angDiff += compareAngles(c1.angles[i], c2.angles[i]);
		angDiff /= strokes;
		double posDiff = 0;
		if (strokes != 1) {
			for (int i = 0; i < strokes - 1; i++)
				posDiff += Connection.compare(c1.connections[i], c2.connections[i]);
			posDiff /= strokes - 1;
		}
		return new ComparisonResult(kanji, angDiff, posDiff, angDiff + posDiff);
	}

	private static double[] interpolateAngles(double[] ang, int newSize) {
		double[] result = new double[newSize];
		for (int i = 0; i < newSize; i++) {
			double j = (double) i / (newSize - 1) * (ang.length - 1);
			double f = j % 1;
			if (f == 0) {
				result[i] = ang[(int) j];
				continue;
			}
			double a1 = ang[(int) Math.floor(j)], a2 = ang[(int) Math.ceil(j)];
			double dist0 = a2 - a1, distP = dist0 + Math.PI * 2, distM = dist0 - Math.PI * 2, dist;
			if (Math.abs(dist0) > Math.abs(distM))
				dist = distM;
			else if (Math.abs(dist0) > Math.abs(distP))
				dist = distP;
			else
				dist = dist0;
			result[i] = a1 + dist * f;
		}
		return result;
	}

	private static double compareAngles(double[] ang1, double[] ang2) {
		double[] larger = ang1.length > ang2.length ? ang1 : ang2, smaller = ang1.length > ang2.length ? ang2 : ang1;
		if (larger.length != smaller.length)
			smaller = interpolateAngles(smaller, larger.length);
		double result = 0;
		for (int i = 0; i < larger.length; i++)
			result += Math.abs(larger[i] - smaller[i]);
		result /= larger.length;
		return result;
	}

	private static double[] createAngles(Stroke s) {
		double[] result = new double[s.getSize() - 1];
		Point2D path[] = s.getPoints(), p = path[0];
		for (int i = 1; i < result.length; i++) {
			Point2D n = path[i];
			result[i] = Math.atan2(n.getX() - p.getX(), n.getY() - p.getY());
			p = n;
		}
		return result;
	}
}

package recognition;

public class Connection {
	public double sToS_x, sToS_y, eToE_x, eToE_y;

	public Connection(Stroke s1, Stroke s2) {
		double u = Math.max(s1.getStart().distance(s1.getEnd()), s2.getStart().distance(s2.getEnd()));
		sToS_x = (s2.getStart().getX() - s1.getStart().getX()) / u;
		sToS_y = (s2.getStart().getY() - s1.getStart().getY()) / u;
		eToE_x = (s2.getEnd().getX() - s1.getEnd().getX()) / u;
		eToE_y = (s2.getEnd().getY() - s1.getEnd().getY()) / u;
	}

	public static double compare(Connection c1, Connection c2) {
		return Math.hypot(c2.sToS_x - c1.sToS_x, c2.sToS_y - c1.sToS_y)
				+ Math.hypot(c2.eToE_x - c1.eToE_x, c2.eToE_y - c1.eToE_y);
	}
}

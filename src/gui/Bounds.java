package gui;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

public final class Bounds {
	private Rectangle bounds, realCoordsBounds;
	private int relX, relY;
	private Dimension size;

	public Bounds(int x, int y, int w, int h, int relX, int relY) {
		if (Math.max(Math.abs(relX), Math.abs(relY)) > 1)
			throw new IllegalArgumentException("relX and relY can only be -1, 0 or 1");
		size = new Dimension();
		bounds = new Rectangle(x, y, w, h);
		this.relX = relX;
		this.relY = relY;
		realCoordsBounds = new Rectangle(0, 0, w, h);
	}

	public int getX() {
		return realCoordsBounds.x;
	}

	public int getY() {
		return realCoordsBounds.y;
	}

	public int getWidth() {
		return realCoordsBounds.width;
	}

	public int getHeight() {
		return realCoordsBounds.height;
	}

	public boolean contains(Point p) {
		int x = p.x - getX(), y = p.y - getY();
		if (x < 0 || y < 0)
			return false;
		return x < bounds.width && y < bounds.height;
	}

	private void updateCoordinates() {
		int x, y;
		switch (relX) {
		case -1:
			x = bounds.x;
			break;
		case 0:
			x = bounds.x + size.width / 2;
			break;
		case 1:
			x = size.width + bounds.x;
			break;
		default:
			throw new Error();
		}
		switch (relY) {
		case -1:
			y = bounds.y;
			break;
		case 0:
			y = bounds.y + size.height / 2;
			break;
		case 1:
			y = size.height + bounds.y;
			break;
		default:
			throw new Error();
		}
		realCoordsBounds.setLocation(x, y);
		realCoordsBounds.setSize(bounds.width, bounds.height);
	}

	public void setSize(Dimension d) {
		size = d;
		updateCoordinates();
	}

	public Dimension getSize() {
		return size;
	}

	public Rectangle getBounds() {
		return realCoordsBounds;
	}
}

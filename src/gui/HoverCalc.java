package gui;

public final class HoverCalc {
	private RepaintingObject rep;
	private int changePeriod;
	private boolean focus;
	private long timing;

	public HoverCalc(int changePeriod, Element e, boolean focus) {
		this.changePeriod = changePeriod;
		this.focus = focus;
		rep = new RepaintingObject(() -> e.repaint());
	}

	public HoverCalc(int changePeriod, Runnable r, boolean focus) {
		this.changePeriod = changePeriod;
		this.focus = focus;
		rep = new RepaintingObject(r);
	}

	public HoverCalc(int changePeriod, boolean focus) {
		this.changePeriod = changePeriod;
		this.focus = focus;
	}

	public void start() {
		rep.add();
	}

	public void stop() {
		rep.remove();
	}

	public double getPhase() {
		double p = (double) (System.currentTimeMillis() - timing) / changePeriod;
		p = trim(p);
		p = focus ? p : 1 - p;
		return p;
	}

	public static double sineSigmoid(double t) {
		return (1 - Math.cos(Math.PI * t)) / 2;
	}

	public static double invSquareSigmoid(double t) {
		return 2 * t - t * t;
	}

	public static double squareSigmoid(double t) {
		return t * t;
	}

	private double trim(double p) {
		if (p > 1) {
			if (p > 2 && rep != null)
				rep.setActive(false);
			return 1;
		}
		return p;
	}

	public void instantChange(boolean focus) {
		timing = 0;
		this.focus = focus;
	}

	public boolean isActive() {
		return System.currentTimeMillis() - timing < changePeriod;
	}

	public void focusChanged(boolean focus) {
		if (focus == this.focus)
			return;
		if (rep != null)
			rep.setActive(true);
		long time = System.currentTimeMillis();
		if (time > timing + changePeriod)
			timing = time;
		else
			timing = 2 * time - timing - changePeriod;
		this.focus = focus;
	}
}

package gui;

import java.util.ArrayList;

public class RepaintingObject {
	private static int animationsFPS = 65;

	private static ArrayList<RepaintingObject> queue = new ArrayList<>(), toAdd = new ArrayList<>(),
			toRemove = new ArrayList<>();
	private static Thread thread = new Thread() {
		public void run() {
			while (true) {
				try {
					if (!toRemove.isEmpty()) {
						queue.removeAll(toRemove);
						toRemove.clear();
					}
					if (!toAdd.isEmpty()) {
						queue.addAll(toAdd);
						toAdd.clear();
					}
					queue.forEach(r -> r.proceed());
					Thread.sleep(1000 / animationsFPS);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	};

	public static void startThread() {
		thread.start();
	}

	private Runnable r;
	private boolean active;

	public RepaintingObject(Runnable r) {
		this.r = r;
		if (r == null) {
			System.out.println("null r in repaintingObject");
			System.out.println(new Exception().getStackTrace());
		}
	}

	public void setActive(boolean a) {
		active = a;
	}

	public boolean isActive() {
		return active;
	}

	public void add() {
		if (!toAdd.contains(this) && !queue.contains(this))
			toAdd.add(this);
	}

	public void remove() {
		if (!toRemove.contains(this) && queue.contains(this))
			toRemove.add(this);
	}

	private void proceed() {
		if (active)
			r.run();
	}
}

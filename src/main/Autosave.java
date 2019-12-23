package main;

import cards.Stats;

public class Autosave {
	private static int delay = 10 * 60 * 1000;
	private static Thread thread = new Thread(() -> {
		try {
			Thread.sleep(delay);
			save();
		} catch (Exception e) {
			e.printStackTrace();
		}
	});

	public static void run() {
		thread.start();
	}

	private static void save() {
		Stats.save();
		Settings.save();
	}
}

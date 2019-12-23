package cards;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;

import main.Main;

public final class Stats {
	public static File statsFile = new File("C:/Users/" + System.getProperty("user.name")
			+ "/AppData/Roaming/Kanji learning app by supernova/stats file.txt");

	private static final int listsAmount = Cards.modesAmount * 2;
	private static AnswersLog[] logs;

	public static void initialize() {
		if (statsFile.isFile())
			load();
		else {
			logs = new AnswersLog[listsAmount];
			for (int i = 0; i < listsAmount; i++)
				logs[i] = new AnswersLog();
			save();
		}
	}

	private static void load() {
		byte[] array;
		try {
			FileInputStream fileIn = new FileInputStream(statsFile);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			int read;
			byte[] buffer = new byte[1024];
			while ((read = fileIn.read(buffer)) != -1)
				out.write(buffer, 0, read);
			fileIn.close();
			array = out.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		ByteBuffer buffer = ByteBuffer.wrap(array);

		long v = buffer.getLong();
		if (v != Main.version) {
			System.err.println("Incompatible stats data file version " + v + " (current version " + Main.version + ")");
			logs = new AnswersLog[listsAmount];
			for (int i = 0; i < listsAmount; i++)
				logs[i] = new AnswersLog();
			return;
		}

		int sizes[] = new int[listsAmount], sum = 0;
		for (int i = 0; i < listsAmount; i++) {
			sizes[i] = buffer.getInt();
			sum += sizes[i];
		}

		int expectedFilesize = 8 + 4 * listsAmount + 12 * sum;
		if (array.length != expectedFilesize) {
			System.err.println(
					"Stats data file size is incorrect (" + array.length + " B), expected: " + expectedFilesize + " B");
			logs = new AnswersLog[listsAmount];
			for (int i = 0; i < listsAmount; i++)
				logs[i] = new AnswersLog();
			return;
		}

		logs = new AnswersLog[listsAmount];
		for (int i = 0; i < listsAmount; i++)
			logs[i] = new AnswersLog(buffer, sizes[i]);
	}

	public static void save() {
		try {
			int sum = 0;
			for (int i = 0; i < listsAmount; i++)
				sum += logs[i].getSize();

			ByteBuffer buffer = ByteBuffer.allocate(8 + 4 * listsAmount + 12 * sum);
			buffer.putLong(Main.version);
			for (int i = 0; i < listsAmount; i++)
				buffer.putInt(logs[i].getSize());
			for (int i = 0; i < listsAmount; i++)
				logs[i].writeTo(buffer);

			FileOutputStream out = new FileOutputStream(statsFile);
			out.write(buffer.array());
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void put(int index, boolean isAnswerCorrect) {
		logs[Cards.getMode() * 2 + (isAnswerCorrect ? 0 : 1)].put(index);
	}

	public static AnswersLog getLog(int log) {
		return logs[log];
	}

	public static void applyStatistics(Kanji[] kanji) {
		for (int i = 0; i < listsAmount; i++)
			logs[i].applyStatistics(kanji, i);
	}
}

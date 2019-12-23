package main;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.file.FileSystems;
import java.util.HashMap;
import java.util.Map;

public class Settings {
	public static File settingsFile = new File("C:/Users/" + System.getProperty("user.name")
			+ "/AppData/Roaming/Kanji learning app by supernova/settings.txt");
	private static int defaultLength = Long.BYTES + Integer.BYTES * 12 + Double.BYTES * 1 + Integer.BYTES * 4;

	public static int windowX = Integer.MAX_VALUE, windowY = Integer.MAX_VALUE, windowW = 1366, windowH = 768,
			kanjiPickMode, learningMode, progressLearningMode, progressInfoType, bg = 0x000000, fg = 0xffffff,
			kanjiAmt = 8, language;
	public static double dim = 0.08d;
	public static String loopsString = "3", periodString = "2 hours", picFileString = "clouds.jpg";
	public static boolean[] chosenKanji = { true };

	public static void save() {
		try {
			byte[] loops = loopsString.getBytes("UTF8"), period = periodString.getBytes("UTF8"),
					pic = picFileString.getBytes("UTF8");

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ByteBuffer buffer = ByteBuffer.allocate(defaultLength);

			buffer.putLong(Main.version);
			buffer.putInt(windowX);
			buffer.putInt(windowY);
			buffer.putInt(windowW);
			buffer.putInt(windowH);
			buffer.putInt(kanjiPickMode);
			buffer.putInt(learningMode);
			buffer.putInt(progressLearningMode);
			buffer.putInt(progressInfoType);
			buffer.putInt(bg);
			buffer.putInt(fg);
			buffer.putInt(kanjiAmt);
			buffer.putInt(language);
			buffer.putDouble(dim);
			buffer.putInt(loops.length);
			buffer.putInt(period.length);
			buffer.putInt(pic.length);
			buffer.putInt(chosenKanji.length);
			out.write(buffer.array());
			out.write(loops);
			out.write(period);
			out.write(pic);
			for (int i = 0; i < chosenKanji.length / 8; i++) {
				int in = i * 8;
				byte b = 0;
				for (int j = 0; j < 8; j++)
					b |= (chosenKanji[in + j] ? 1 : 0) << (7 - j);
				out.write(b);
			}
			extra: {
				int extra = chosenKanji.length % 8;
				if (extra == 0)
					break extra;
				int start = chosenKanji.length - extra;
				byte b = 0;
				for (int i = 0; i < extra; i++)
					b |= (chosenKanji[start + i] ? 1 : 0) << (7 - i);
				out.write(b);
			}

			FileOutputStream fileOut = new FileOutputStream(settingsFile);
			out.writeTo(fileOut);
			fileOut.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void load() {
		if (Main.loadFromJar) {
			try {
				URI uri = Settings.class.getResource("/assets/writings.txt").toURI();
				Map<String, String> env = new HashMap<>();
				env.put("create", "true");
				FileSystems.newFileSystem(uri, env);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (!settingsFile.getParentFile().exists())
			settingsFile.getParentFile().mkdir();
		if (!settingsFile.exists()) {
			save();
			return;
		}
		try {
			ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
			byte[] buf = new byte[1024];
			int read;

			FileInputStream fileIn = new FileInputStream(settingsFile);
			while ((read = fileIn.read(buf)) != -1)
				byteOut.write(buf, 0, read);
			fileIn.close();
			byte[] allBytes = byteOut.toByteArray();

			ByteBuffer buffer = ByteBuffer.wrap(byteOut.toByteArray(), 0, defaultLength);

			long version = buffer.getLong();
			if (Main.version != version) {
				System.err.println("Incompatible settings data file version " + version + " (current version "
						+ Main.version + ")");
				save();
				return;
			}

			windowX = buffer.getInt();
			windowY = buffer.getInt();
			windowW = buffer.getInt();
			windowH = buffer.getInt();
			kanjiPickMode = buffer.getInt();
			learningMode = buffer.getInt();
			progressLearningMode = buffer.getInt();
			progressInfoType = buffer.getInt();
			bg = buffer.getInt();
			fg = buffer.getInt();
			kanjiAmt = buffer.getInt();
			language = buffer.getInt();
			dim = buffer.getDouble();
			int loopsLen = buffer.getInt();
			int periodLen = buffer.getInt();
			int picLen = buffer.getInt();
			int chosenLen = buffer.getInt();

			byte[] loopsBytes = {};
			byte[] periodBytes = {};
			byte[] picBytes = {};
			boolean[] chosenBs = {};
			int index = defaultLength;
			loopsBytes = new byte[loopsLen];
			try {
				for (int i = 0; i < loopsLen; i++)
					loopsBytes[i] = allBytes[index + i];
			} catch (ArrayIndexOutOfBoundsException e) {
				e.printStackTrace();
			}
			index += loopsLen;
			periodBytes = new byte[periodLen];
			try {
				for (int i = 0; i < periodLen; i++)
					periodBytes[i] = allBytes[index + i];
			} catch (ArrayIndexOutOfBoundsException e) {
				e.printStackTrace();
			}
			index += periodLen;
			picBytes = new byte[picLen];
			try {
				for (int i = 0; i < picLen; i++)
					picBytes[i] = allBytes[index + i];
			} catch (ArrayIndexOutOfBoundsException e) {
				e.printStackTrace();
			}
			index += picLen;

			chosenBs = new boolean[chosenLen];
			try {
				for (int i = 0; i < chosenLen; i++) {
					byte b = allBytes[index + i / 8];
					b >>>= 7 - i % 8;
					chosenBs[i] = (b & 1) == 1;
				}
			} catch (ArrayIndexOutOfBoundsException e) {
				e.printStackTrace();
			}
			String loops = new String(loopsBytes, "UTF8");
			String period = new String(periodBytes, "UTF8");
			String pic = new String(picBytes, "UTF8");
			chosenKanji = chosenBs;

			loopsString = loops;
			periodString = period;
			picFileString = pic;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

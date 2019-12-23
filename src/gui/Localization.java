package gui;

import java.awt.Color;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import main.Main;
import main.Settings;

public final class Localization {
	public static Color[] colors;
	public static final Localization[] localizations;
	private static Localization current;

	private String name;
	private String[] inscriptions, colorsNames;
	private int magicNumbers[], id;

	static {
		colors = new Color[] { new Color(0xffffff), new Color(0x000000), new Color(0x646464), new Color(0x00ff64),
				new Color(0x0000ff) };
		localizations = new Localization[2];
		localizations[0] = new Localization("english", 0);
		localizations[1] = new Localization("russian", 1);
		current = localizations[Settings.language];
	}

	public static void updateLanguage(int i) {
		current = localizations[i];
	}

	private Localization(String locale, int id) {
		try {
			this.id = id;
			name = Character.toUpperCase(locale.charAt(0)) + locale.substring(1).toLowerCase();
			inscriptions = read("inscriptions-" + locale + ".txt");
			magicNumbers = readInt("magicNumbers-" + locale + ".txt");
			colorsNames = read("colors-" + locale + ".txt");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static int[] readInt(String locale) {
		String[] ss = read(locale);
		int[] is = new int[ss.length];
		for (int i = 0; i < ss.length; i++)
			is[i] = Integer.parseInt(ss[i].contains(" ") ? ss[i].substring(0, ss[i].indexOf(' ')) : ss[i]);
		return is;
	}

	private static String[] read(String locale) {
		try {
			String string;
			if (Main.loadFromJar)
				string = new String(Files.readAllBytes(
						Paths.get(Localization.class.getResource("/assets/localization/" + locale).toURI())));
			else
				string = new String(Files
						.readAllBytes(Paths.get(new File(new File("").getAbsoluteFile(), "assets/localization/" + locale).toURI())));
			if (string.charAt(0) == '\uFEFF')
				string = string.substring(1);
			return string.split("\n");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String getText(int i) {
		return current.inscriptions[i];
	}

	public static int getNum(int i) {
		return current.magicNumbers[i];
	}

	public static int getNum(int... is) {
		int s = 0;
		for (int i : is)
			s += getNum(i);
		return s;
	}

	public static int getId() {
		return current.id;
	}

	public String getName() {
		return name;
	}

	public static String getColorName(int i) {
		return current.colorsNames[i];
	}
}

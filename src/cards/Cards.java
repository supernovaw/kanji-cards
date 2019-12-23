package cards;

import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

import gui.Localization;
import main.Main;
import main.Settings;
import recognition.ComparisonInfo;
import recognition.ComparisonResult;
import recognition.Drawing;

public final class Cards {
	public static final int modesAmount = 4; // wordcards / kanji select / kanji draw / stroke practice

	private static KanjiGroup groups[], grades[][];
	private static ArrayList<Kanji> currentList = new ArrayList<>(), examList, mistakesExam;
	private static Kanji currentKanji;
	private static int scoreCorrect, scoreWrong;
	private static Kanji lastCard;
	private static int mode = Settings.learningMode;
	private static boolean examMode;
	private static int examAnswered, examCorrect, examWrong;
	private static HashMap<Character, Drawing> drawingsMap;

	private static void loadDrawings() throws Exception {
		Path path;
		if (Main.loadFromJar) {
			URI uri = Cards.class.getResource("/assets/writings.txt").toURI();
			path = Paths.get(uri);
		} else
			path = Paths.get(new File(new File("").getAbsoluteFile(), "assets/writings.txt").toURI());

		String s = new String(Files.readAllBytes(path));
		if (s.charAt(0) == '\uFEFF')
			s = s.substring(1);
		s = s.replace(System.lineSeparator(), "\n");
		String[] lines = s.split("\n");
		int index = 0;
		drawingsMap = new HashMap<>();
		while (index != lines.length) {
			drawingsMap.put((char) Integer.parseInt(lines[index].substring(7), 16), new Drawing(lines, index));
			index = Drawing.newIndex(lines, index);
		}
	}

	public static void load() {
		try {
			loadDrawings();

			char[] kanjiChars = read("/assets/kanji.txt").toCharArray();

			if (kanjiChars.length > Settings.chosenKanji.length) {
				boolean[] newArray = new boolean[kanjiChars.length];
				for (int i = 0; i < Settings.chosenKanji.length; i++)
					newArray[i] = Settings.chosenKanji[i];
				Settings.chosenKanji = newArray;
			}

			String[][] meaning = new String[Localization.localizations.length][];
			for (int i = 0; i < meaning.length; i++) {
				meaning[i] = read("/assets/meanings " + Localization.localizations[i].getName().toLowerCase() + ".txt")
						.split("\n");
				if (meaning[i].length != kanjiChars.length)
					throw new Error("chars: " + kanjiChars.length + ", words: " + meaning[i].length + " (locale " + i
							+ " - " + Localization.localizations[i].getName() + ")");
			}
			int kanjiAmt = kanjiChars.length;
			Kanji[] allKanji = new Kanji[kanjiAmt];
			for (int i = 0; i < kanjiAmt; i++) {
				String[] ms = new String[meaning.length];
				for (int j = 0; j < ms.length; j++)
					ms[j] = meaning[j][i];
				allKanji[i] = new Kanji(kanjiChars[i], ms, drawingsMap.get(kanjiChars[i]), i);
			}

			Stats.initialize();
			Stats.applyStatistics(allKanji);

			String groupsInfo[] = read("/assets/groups.txt").split("\n");
			ArrayList<KanjiGroup> allGroups = new ArrayList<>();
			grades = new KanjiGroup[groupsInfo.length][];
			for (int i = 0; i < groupsInfo.length; i++) {
				String gradeGroupsInfo[] = groupsInfo[i].split(";");
				grades[i] = new KanjiGroup[gradeGroupsInfo.length];
				for (int j = 0; j < gradeGroupsInfo.length; j++) {
					String info = gradeGroupsInfo[j];
					int index = info.indexOf('-');
					int from = Integer.parseInt(info.substring(0, index));
					int to = Integer.parseInt(info.substring(index + 1));
					KanjiGroup group = createGroup(allKanji, from, to);
					allGroups.add(group);
					grades[i][j] = group;
				}
			}
			groups = allGroups.toArray(new KanjiGroup[allGroups.size()]);
			for (Kanji k : allGroups.get(0).kanji)
				currentList.add(k);
			currentKanji = random();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static KanjiGroup createGroup(Kanji[] all, int from, int to) {
		Kanji[] result = new Kanji[to - from + 1];
		for (int i = 0; i < result.length; i++)
			result[i] = all[i + from - 1];
		return new KanjiGroup(result, from + new String(new char[] { 8212 }) + to);
	}

	private static String read(String path) {
		try {
			String s;
			if (Main.loadFromJar)
			s = new String(Files.readAllBytes(Paths.get(Cards.class.getResource(path).toURI())), "UTF8");
			else
				s = new String(Files.readAllBytes(Paths.get(new File(new File("").getAbsoluteFile(), path).toURI())), "UTF8");
			if (s.charAt(0) == '\uFEFF') // file can start with an unnecesarry char
				s = s.substring(1);
			s = s.replace(System.lineSeparator(), "\n");
			return s;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void next(boolean isCorrect) {
		currentKanji.applyAnswer(isCorrect); // affect kanji statistics
		if (examMode && !mistakesExam.contains(currentKanji) && !isCorrect)
			mistakesExam.add(currentKanji);
		if (isCorrect) {
			if (examMode)
				examCorrect++;
			else
				scoreCorrect++;
		} else {
			if (examMode)
				examWrong++;
			else
				scoreWrong++;
		}
		lastCard = currentKanji;
		if (examMode)
			examAnswered++;
		if (examMode) {
			if (examAnswered < examList.size())
				currentKanji = examList.get(examAnswered);
			else
				examMode = false;
		} else
			currentKanji = random();
	}

	public static boolean isLastExamAnswer() {
		return examMode && examAnswered >= examList.size() - 1;
	}

	public static int getExamCorrect() {
		return examCorrect;
	}

	public static int getExamWrong() {
		return examWrong;
	}

	public static int getExamAnswered() {
		return examAnswered;
	}

	public static ArrayList<Kanji> getMistakesExam() {
		return mistakesExam;
	}

	public static Kanji findKanji(char character) {
		for (KanjiGroup kg : groups)
			for (Kanji k : kg.kanji)
				if (k.character == character)
					return k;
		throw new IllegalArgumentException("character not found: " + character);
	}

	public static boolean isDrawingCorrect(Drawing d) {
		if (d.getStrokesCount() != currentKanji.getDrawing().getStrokesCount())
			return false; // if the amount of strokes doesn't match, this is always an incorrect answer
		ComparisonInfo dInfo = new ComparisonInfo(d);
		ComparisonResult[] rs = new ComparisonResult[currentList.size()];
		for (int i = 0; i < currentList.size(); i++) {
			Kanji k = currentList.get(i);
			rs[i] = ComparisonInfo.compare(k.character, dInfo, k.getComparisonInfo());
		}
		int mostSimilar = -1;
		for (int i = 0; i < rs.length; i++)
			if (!rs[i].strokesDontMatch) {
				mostSimilar = i; // place index at kanji with the same amount of strokes
				break;
			}
		if (mostSimilar == -1)
			throw new Error("there is no kanji with the same amount of strokes");
		for (int i = 0; i < rs.length; i++) {
			if (!rs[i].strokesDontMatch && rs[i].diff < rs[mostSimilar].diff)
				mostSimilar = i; // when a new, more similar kanji is found, set new index
		}
		// compare the most similar kanji with the expected kanji
		return rs[mostSimilar].kanji == currentKanji.character;
	}

	private static int findBiggestInaccuracyRate() {
		int b = currentList.get(0).getInaccuracyRate();
		for (int i = 1; i < currentList.size(); i++) {
			int j = currentList.get(i).getInaccuracyRate();
			if (b < j)
				b = j;
		}
		return b;
	}

	public static Kanji random() {
		double sum = 0, prob[] = new double[currentList.size()];
		int j = currentList.indexOf(currentKanji), sm = findBiggestInaccuracyRate();
		for (int i = 0; i < prob.length; i++) {
			prob[i] = i != j ? Math.pow(2, currentList.get(i).getInaccuracyRate() - sm) : 0;
			sum += prob[i]; // find the probability of each kanji to be chosen and add to sum
		}
		double d = Math.random() * sum; // a number which lies on some kanji (bigger number = bigger probability)
		for (int i = 0; i < prob.length; i++) { // find which kanji is chosen
			d -= prob[i];
			if (d < 0)
				return currentList.get(i);
		}
		throw new Error("d=" + d + " (" + Long.toHexString(Double.doubleToLongBits(d)) + "); sum=" + sum + " ("
				+ Long.toHexString(Double.doubleToLongBits(sum)) + ") ; len=" + prob.length);
	}

	public static Kanji getCurrentKanji() {
		return currentKanji;
	}

	public static void setCurrentList(ArrayList<Kanji> l) {
		if (l.size() <= 1)
			throw new Error("list contains too few kanji (" + l.size() + ")");
		examMode = false;
		currentList.clear();
		currentList.addAll(l);
		currentKanji = random();
	}

	private static ArrayList<Kanji> shuffle(ArrayList<Kanji> list, Kanji last) {
		ArrayList<Kanji> result = new ArrayList<>(), copy = new ArrayList<>();
		copy.addAll(list);
		if (last != null) {
			int i = (int) (Math.random() * (list.size() - 1));
			if (i >= list.indexOf(last))
				i++;
			result.add(copy.remove(i));
		}
		while (!copy.isEmpty()) {
			int i = (int) (Math.random() * copy.size());
			result.add(copy.remove(i));
		}
		return result;
	}

	public static void setExamKanji(ArrayList<Kanji> list, int loops) {
		if (list.size() <= 1)
			throw new Error("list contains too few kanji (" + list.size() + ")");
		currentList.clear();
		currentList.addAll(list);
		examList = new ArrayList<>();
		Kanji last = null;
		for (int i = 0; i < loops; i++) {
			examList.addAll(shuffle(list, last));
			last = list.get(list.size() - 1);
		}
		examMode = true;
		examAnswered = 0;
		examCorrect = 0;
		examWrong = 0;
		mistakesExam = new ArrayList<>();
		currentKanji = examList.get(0);
	}

	public static Kanji getUnbiasedRandomKanji() {
		// the random() method returns a random kanji where the more you answer
		// incorrectly the more probability of getting this kanji again. this
		// getUnbiasedRandomKanji() returns a kanji where all kanji have the same
		// probability of being chosen
		return currentList.get((int) (currentList.size() * Math.random()));
	}

	public static String getStatus() {
		if (examMode)
			return examCorrect + ":" + examWrong + " (" + examAnswered + "/" + examList.size() + ")";
		else
			return scoreCorrect + ":" + scoreWrong;
	}

	public static KanjiGroup[] getGroups() {
		return groups;
	}

	public static KanjiGroup[][] getGrades() {
		return grades;
	}

	public static int getScoreCorrect() {
		return scoreCorrect;
	}

	public static int getScoreWrong() {
		return scoreWrong;
	}

	public static Kanji getLastCard() {
		return lastCard;
	}

	public static int getMode() {
		return mode;
	}

	public static void setMode(int mode) {
		Cards.mode = mode;
	}

	public static void resetScore() {
		scoreCorrect = 0;
		scoreWrong = 0;
	}
}

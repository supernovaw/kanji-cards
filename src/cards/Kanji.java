package cards;

import gui.Localization;
import recognition.ComparisonInfo;
import recognition.Drawing;

public final class Kanji {
	public final char character;
	public final String[] meaning;
	public final int index;
	private final Drawing drawing;
	private final ComparisonInfo comparisonInfo;
	private int scoreCorrect[] = new int[Cards.modesAmount], scoreMistakes[] = new int[Cards.modesAmount];

	public Kanji(char character, String[] meaning, Drawing d, int index) {
		this.character = character;
		this.meaning = meaning;
		this.index = index;
		drawing = d;
		comparisonInfo = new ComparisonInfo(drawing);
	}

	public void addScore(int list) {
		int index = list / 2;
		if (index % 2 == 0)
			scoreCorrect[index]++;
		else
			scoreMistakes[index]++;
	}

	public void applyAnswer(boolean isCorrect) {
		int m = Cards.getMode();
		if (isCorrect)
			scoreCorrect[m]++;
		else
			scoreMistakes[m]++;
		Stats.put(index, isCorrect);
	}

	public int getInaccuracyRate() { // the inaccuracy rate is increased when you answer incorrectly, and vice versa.
										// to get the probability of being chosen to be next kanji used as power of some
										// number
		int m = Cards.getMode();
		return scoreMistakes[m] - scoreCorrect[m];
	}

	public String getMeaning() {
		return meaning[Localization.getId()];
	}

	public Drawing getDrawing() {
		return drawing;
	}

	public ComparisonInfo getComparisonInfo() {
		return comparisonInfo;
	}

	public boolean equals(Object o) {
		if (o == null || !(o instanceof Kanji))
			return false;
		Kanji k = (Kanji) o;
		return k.index == index;
	}
}

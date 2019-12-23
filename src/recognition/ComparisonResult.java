package recognition;

public class ComparisonResult {
	public char kanji;
	public boolean strokesDontMatch;
	public double angDiff, posDiff, diff;

	public ComparisonResult(char kanji) {
		strokesDontMatch = true;
		this.kanji = kanji;
	}

	public ComparisonResult(char kanji, double angDiff, double posDiff, double diff) {
		this.kanji = kanji;
		this.angDiff = angDiff;
		this.posDiff = posDiff;
		this.diff = diff;
	}

	public String toString() {
		if (strokesDontMatch)
			return kanji + " strokes don't match";
		return kanji + " " + diff;
	}
}

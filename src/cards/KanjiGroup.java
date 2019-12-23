package cards;

public class KanjiGroup {
	public Kanji[] kanji;
	public String name;
	private int[] indices;

	public KanjiGroup(Kanji[] kanji, String name) {
		this.kanji = kanji;
		this.name = name;
		indices = new int[kanji.length];
		for (int i = 0; i < kanji.length; i++)
			indices[i] = kanji[i].index;
	}

	public int[] getIndices() {
		return indices;
	}
}

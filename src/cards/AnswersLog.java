package cards;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class AnswersLog {
	private ArrayList<Long> timecodes;
	private ArrayList<Integer> indices;
	private int size;

	public AnswersLog() {
		timecodes = new ArrayList<>();
		indices = new ArrayList<>();
	}

	public AnswersLog(ByteBuffer data, int size) {
		this.size = size;
		timecodes = new ArrayList<>();
		indices = new ArrayList<>();
		for (int i = 0; i < size; i++) {
			timecodes.add(data.getLong());
			indices.add(data.getInt());
		}
	}

	public void writeTo(ByteBuffer data) {
		for (int i = 0; i < size; i++) {
			data.putLong(timecodes.get(i));
			data.putInt(indices.get(i));
		}
	}

	public int getSize() {
		return size;
	}

	public void put(int index) {
		timecodes.add(System.currentTimeMillis());
		indices.add(index);
		size++;
	}

	public void applyStatistics(Kanji[] kanji, int list) {
		for (int i = 0; i < size; i++)
			kanji[indices.get(i)].addScore(list);
	}

	public int getAmount(long start, int[] ind) {
		if (size == 0)
			return 0;
		if (timecodes.get(size - 1) < start)
			return 0;

		int startIndex = -1;
		if (timecodes.get(0) > start)
			startIndex = 0;
		else
			for (int i = size - 1; i >= 0; i--)
				if (timecodes.get(i) < start) {
					startIndex = i + 1;
					break;
				}
		if (startIndex == -1)
			throw new Error();

		int total = 0;
		for (int i = startIndex; i < size; i++) {
			int index = indices.get(i);
			for (int j = 0; j < ind.length; j++)
				if (ind[j] == index) {
					total++;
					break;
				}
		}
		return total;
	}
}

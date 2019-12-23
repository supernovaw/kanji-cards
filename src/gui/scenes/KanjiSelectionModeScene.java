package gui.scenes;

import cards.Cards;
import gui.Bounds;
import gui.Localization;
import gui.Scene;
import gui.elements.Button;
import gui.elements.KanjiDisplay;
import gui.elements.KanjiWheel;
import gui.elements.Label;
import main.Main;
import main.Settings;

public class KanjiSelectionModeScene extends Scene {
	private KanjiWheel kanjiWheel;
	private Button returnButton;
	private Label infoLabel;
	private KanjiDisplay kanjiDisplay;

	public KanjiSelectionModeScene() {
		kanjiWheel = new KanjiWheel(Settings.kanjiAmt, () -> next(), new Bounds(-300, -300, 600, 600, 0, 0), this);
		returnButton = new Button(Localization.getText(28), () -> getHolder().setScene(Main.main),
				new Bounds(-Localization.getNum(4) - 10, -35, Localization.getNum(4), 25, 1, 1), this);
		infoLabel = new Label(Cards.getStatus(), new Bounds(10, -40, 160, 30, -1, 1), this);
		kanjiDisplay = new KanjiDisplay(' ', new Bounds(450, -75, 150, 150, 0, 0), this);
		add(kanjiWheel);
		add(returnButton);
		add(infoLabel);
		add(kanjiDisplay);
	}

	public void updateLocalization() {
		returnButton.setText(Localization.getText(28));
		returnButton.setBounds(new Bounds(-Localization.getNum(4) - 10, -35, Localization.getNum(4), 25, 1, 1));
	}

	public void changeKanjiAmt(int amt) {
		kanjiWheel.changeKanjiAmt(amt);
	}

	public int getKanjiAmt() {
		return kanjiWheel.getKanjiAmt();
	}

	private void next() {
		boolean correct = kanjiWheel.isAnswerCorrect();
		if (Cards.isLastExamAnswer()) {
			Cards.next(correct);
			getHolder().setScene(Main.examResults);
		} else {
			Cards.next(correct);
			infoLabel.changeText(Cards.getStatus());
			kanjiDisplay.changeChar(correct ? ' ' : Cards.getLastCard().character);
		}
	}

	public void onDisplay() {
		kanjiWheel.upd();
		infoLabel.changeText(Cards.getStatus());
	}
}

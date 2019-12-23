package gui.scenes;

import cards.Cards;
import gui.Bounds;
import gui.Localization;
import gui.Scene;
import gui.elements.Button;
import gui.elements.KanjiStrokesPicker;
import gui.elements.Label;
import main.Main;

public class StrokePracticeScene extends Scene {
	private float fontSize = 50f;

	private Button returnButton;
	private KanjiStrokesPicker kanji;
	private Label infoLabel, meaningLabel;

	public StrokePracticeScene() {
		kanji = new KanjiStrokesPicker(Cards.random().getDrawing(), () -> next(),
				new Bounds(-300, -250, 600, 600, 0, 0), this);
		infoLabel = new Label(Cards.getStatus(), new Bounds(10, -40, 160, 30, -1, 1), this);
		meaningLabel = new Label(Cards.getCurrentKanji().getMeaning(), fontSize, new Bounds(-350, -350, 700, 80, 0, 0),
				this);
		returnButton = new Button(Localization.getText(28), () -> getHolder().setScene(Main.main),
				new Bounds(-Localization.getNum(4) - 10, -35, Localization.getNum(4), 25, 1, 1), this);
		add(kanji);
		add(infoLabel);
		add(meaningLabel);
		add(returnButton);
	}

	public void updateLocalization() {
		returnButton.setText(Localization.getText(28));
		returnButton.setBounds(new Bounds(-Localization.getNum(4) - 10, -35, Localization.getNum(4), 25, 1, 1));
	}

	private void next() {
		boolean correct = !kanji.hasMistaken();
		if (Cards.isLastExamAnswer()) {
			Cards.next(correct);
			getHolder().setScene(Main.examResults);
		} else {
			Cards.next(correct);
			kanji.changeDrawing(Cards.getCurrentKanji().getDrawing());
			infoLabel.changeText(Cards.getStatus());
			meaningLabel.changeText(Cards.getCurrentKanji().getMeaning());
		}
	}

	public void onDisplay() {
		kanji.setDrawing(Cards.getCurrentKanji().getDrawing());
		infoLabel.setText(Cards.getStatus());
		meaningLabel.setText(Cards.getCurrentKanji().getMeaning());
	}
}

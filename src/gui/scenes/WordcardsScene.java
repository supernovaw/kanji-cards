package gui.scenes;

import cards.Cards;
import gui.Bounds;
import gui.Localization;
import gui.Scene;
import gui.elements.Button;
import gui.elements.KanjiDisplay;
import gui.elements.Label;
import gui.elements.TextField;
import main.Main;

public class WordcardsScene extends Scene {
	private KanjiDisplay kanji;
	private TextField input;
	private Button returnButton;
	private Label infoLabel, answerLabel;
	private boolean examOver;

	public WordcardsScene() {
		kanji = new KanjiDisplay(Cards.getCurrentKanji().character, new Bounds(-200, -390, 400, 400, 0, 0), this);
		input = new TextField(() -> next(), new Bounds(-250, 50, 500, 50, 0, 0), this);
		returnButton = new Button(Localization.getText(28), () -> getHolder().setScene(Main.main), new Bounds(-Localization.getNum(4) - 10, -35, Localization.getNum(4), 25, 1, 1),
				this);
		infoLabel = new Label(Cards.getStatus(), new Bounds(10, -40, 160, 30, -1, 1), this);
		answerLabel = new Label("", 60f, new Bounds(-500, 130, 1000, 80, 0, 0), this);
		add(kanji);
		add(input);
		add(returnButton);
		add(infoLabel);
		add(answerLabel);
	}

	public void updateLocalization() {
		returnButton.setText(Localization.getText(28));
		returnButton.setBounds(new Bounds(-Localization.getNum(4) - 10, -35, Localization.getNum(4), 25, 1, 1));
	}

	public void onShut() {
		if (examOver) {
			examOver = false;
			input.setText("");
			kanji.changeChar(' ');
			answerLabel.setText("");
		}
	}

	private void next() {
		boolean correct = Cards.getCurrentKanji().getMeaning().equals(input.getText());
		if (Cards.isLastExamAnswer()) {
			Cards.next(correct);
			getHolder().setScene(Main.examResults);
			examOver = true;
		} else {
			Cards.next(correct);
			input.setText("");
			kanji.changeChar(Cards.getCurrentKanji().character);
			infoLabel.changeText(Cards.getStatus());
			answerLabel.changeText(correct ? "" : Cards.getLastCard().getMeaning());
		}
	}

	public void onDisplay() {
		kanji.setChar(Cards.getCurrentKanji().character);
		answerLabel.setText("");
		infoLabel.setText(Cards.getStatus());
	}
}

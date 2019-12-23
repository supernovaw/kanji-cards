package gui.scenes;

import cards.Cards;
import gui.Bounds;
import gui.Localization;
import gui.Scene;
import gui.elements.Button;
import gui.elements.KanjiDrawArea;
import gui.elements.KanjiStrokesDisplay;
import gui.elements.Label;
import main.Main;

public class KanjiDrawScene extends Scene {
	private KanjiDrawArea drawArea;
	private Label infoLabel, questionLabel;
	private Button button, returnButton;
	private KanjiStrokesDisplay answer;

	public KanjiDrawScene() {
		drawArea = new KanjiDrawArea(() -> next(), new Bounds(-250, -350, 500, 500, 0, 0), this);
		infoLabel = new Label(Cards.getStatus(), new Bounds(10, -40, 160, 30, -1, 1), this);
		questionLabel = new Label(Cards.getCurrentKanji().getMeaning(), 60f, new Bounds(-500, 170, 1000, 80, 0, 0),
				this);
		button = new Button(Localization.getText(27), () -> next(), new Bounds(-150, 270, 300, 100, 0, 0), this);
		answer = new KanjiStrokesDisplay(new Bounds(350, -225, 250, 250, 0, 0), this);
		returnButton = new Button(Localization.getText(28), () -> getHolder().setScene(Main.main),
				new Bounds(-Localization.getNum(4) - 10, -35, Localization.getNum(4), 25, 1, 1), this);
		add(drawArea);
		add(infoLabel);
		add(questionLabel);
		add(button);
		add(answer);
		add(returnButton);
	}

	public void updateLocalization() {
		button.setText(Localization.getText(27));
		returnButton.setText(Localization.getText(28));
		returnButton.setBounds(new Bounds(-Localization.getNum(4) - 10, -35, Localization.getNum(4), 25, 1, 1));
	}

	private void next() {
		boolean b = Cards.isDrawingCorrect(drawArea.getDrawingAndCleanUp());
		if (Cards.isLastExamAnswer()) {
			Cards.next(b);
			getHolder().setScene(Main.examResults);
		} else {
			answer.changeDrawing(b ? null : Cards.getCurrentKanji().getDrawing());
			Cards.next(b);
			infoLabel.changeText(Cards.getStatus());
			questionLabel.changeText(Cards.getCurrentKanji().getMeaning());
		}
	}

	public void onDisplay() {
		infoLabel.changeText(Cards.getStatus());
		questionLabel.changeText(Cards.getCurrentKanji().getMeaning());
	}
}

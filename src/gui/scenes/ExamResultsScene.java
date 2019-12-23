package gui.scenes;

import java.util.ArrayList;

import cards.Cards;
import cards.Kanji;
import gui.Bounds;
import gui.Localization;
import gui.Scene;
import gui.elements.Button;
import gui.elements.KanjiDisplay;
import gui.elements.KanjiStrokesDisplay;
import gui.elements.Label;
import main.Main;

public class ExamResultsScene extends Scene {
	private Label titleLabel, correctAnswersLabel, mistakesLabel, meaningLabel;
	private KanjiDisplay kanjiDisplay;
	private KanjiStrokesDisplay kanjiStrokes;
	private ArrayList<Kanji> mistaken;
	private Runnable updaterRun;
	private Thread updater;
	private int mode, index, updateDelay;
	private Button continueButton;

	public ExamResultsScene() {
		titleLabel = new Label(Localization.getText(22), new Bounds(-100, -150, 200, 30, 0, 0), this);
		correctAnswersLabel = new Label("", new Bounds(-200, -120, 400, 30, 0, 0), this);
		mistakesLabel = new Label("", new Bounds(-200, -90, 400, 30, 0, 0), this);
		meaningLabel = new Label("", new Bounds(-200, -60, 400, 30, 0, 0), this);
		kanjiDisplay = new KanjiDisplay(' ', new Bounds(-75, -30, 150, 150, 0, 0), this);
		kanjiStrokes = new KanjiStrokesDisplay(new Bounds(-75, -30, 150, 150, 0, 0), this);
		continueButton = new Button(Localization.getText(27), () -> getHolder().setScene(Main.kanjiSelection),
				new Bounds(-75, 150, 150, 40, 0, 0), this);
		add(titleLabel);
		add(correctAnswersLabel);
		add(mistakesLabel);
		add(meaningLabel);
		add(continueButton);
		add(kanjiDisplay);
		add(kanjiStrokes);

		updaterRun = () -> {
			while (true) {
				try {
					Thread.sleep(updateDelay);
				} catch (Exception e) {
				}
				if (updater == null)
					break;
				index++;
				if (index >= mistaken.size())
					index = 0;
				Kanji k = mistaken.get(index);
				meaningLabel.changeText(index + 1 + ". " + k.getMeaning());
				if (mode == 0)
					kanjiDisplay.changeChar(k.character);
				else
					kanjiStrokes.changeDrawing(k.getDrawing());
			}
		};
	}

	public void updateLocalization() {
		titleLabel.setText(Localization.getText(22));
		continueButton.setText(Localization.getText(27));
	}

	public void onShut() {
		updater = null;
		kanjiDisplay.setChar(' ');
		kanjiStrokes.setDrawing(null);
	}

	public void onDisplay() {
		boolean ace = Cards.getExamCorrect() == Cards.getExamAnswered();
		if (ace) {
			correctAnswersLabel.setText(String.format(Localization.getText(23), Cards.getExamAnswered()));
			mistakesLabel.setText("");
			meaningLabel.setText("");
		} else {
			mistaken = Cards.getMistakesExam();
			mistaken.sort((a, b) -> a.index - b.index);
			if (Main.learningScene == Main.wordcards || Main.learningScene == Main.kanjiSelectionMode)
				mode = 0;
			else
				mode = 1;
			correctAnswersLabel
					.setText(String.format(Localization.getText(24), Cards.getExamCorrect(), Cards.getExamAnswered()));
			mistakesLabel.setText(String.format(Localization.getText(25), mistaken.size()));
			Kanji k = mistaken.get(0);
			meaningLabel.setText("1. " + k.getMeaning());
			if (mode == 0) {
				kanjiDisplay.setChar(k.character);
				kanjiDisplay.getBounds().setSize(getSize());
				updateDelay = 2000;
			} else {
				kanjiStrokes.setDrawing(k.getDrawing());
				kanjiStrokes.getBounds().setSize(getSize());
				updateDelay = 5000;
			}
			if (mistaken.size() > 1) {
				index = 0;
				updater = new Thread(updaterRun);
				updater.start();
			}
		}
	}
}

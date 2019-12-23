package gui.scenes;

import java.util.ArrayList;

import cards.Cards;
import cards.Kanji;
import gui.Bounds;
import gui.Localization;
import gui.Scene;
import gui.elements.Button;
import gui.elements.ButtonGroup;
import gui.elements.Checkbox;
import gui.elements.Label;
import gui.elements.RadioButton;
import gui.elements.TextField;
import main.Main;
import main.Settings;

public class KanjiSelectionScene extends Scene {
	private String noneSelectedWarn = Localization.getText(8), invalidNumberWarn = Localization.getText(9);

	private Checkbox[] checkboxes;
	private Label warningLabel;
	private ButtonGroup group;
	private RadioButton cycleAll, test;
	private TextField textField;
	private Button scoreResetButton, returnButton;

	public KanjiSelectionScene() {
		group = new ButtonGroup();
		cycleAll = new RadioButton(Localization.getText(6), Settings.kanjiPickMode == 0,
				new Bounds(50, 30, Localization.getNum(1), 30, -1, -1), this);
		test = new RadioButton(Localization.getText(7), Settings.kanjiPickMode == 1,
				new Bounds(50 + Localization.getNum(1), 30, Localization.getNum(2), 30, -1, -1), this);
		group.addButton(cycleAll);
		group.addButton(test);
		add(cycleAll);
		add(test);
		textField = new TextField(null, 24f, new Bounds(50 + Localization.getNum(1, 2), 30, 100, 30, -1, -1), this);
		textField.setText(Settings.loopsString);
		add(textField);
		scoreResetButton = new Button(Localization.getText(45), () -> Cards.resetScore(), new Bounds(160 + Localization.getNum(1, 2), 30, Localization.getNum(19), 30, -1, -1), this);
		add(scoreResetButton);

		checkboxes = new Checkbox[Cards.getGroups().length];
		for (int grade = 0, i = 0; grade < Cards.getGrades().length; grade++)
			for (int group = 0; group < Cards.getGrades()[grade].length; group++)
				checkboxes[i++] = new Checkbox(Cards.getGrades()[grade][group].name, Settings.chosenKanji[i - 1],
						new Bounds(50 + grade * 130, 80 + group * 30, 130, 30, -1, -1), this);
		for (Checkbox c : checkboxes)
			add(c);

		warningLabel = new Label("", new Bounds(-510, -80, 500, 30, 1, 1), this);
		warningLabel.setAlignmentX(1);
		add(returnButton = new Button(Localization.getText(26), () -> apply(),
				new Bounds(-Localization.getNum(3) - 10, -40, Localization.getNum(3), 30, 1, 1), this));
		add(warningLabel);
	}

	public void updateLocalization() {
		noneSelectedWarn = Localization.getText(8);
		invalidNumberWarn = Localization.getText(9);
		cycleAll.setText(Localization.getText(6));
		cycleAll.setBounds(new Bounds(50, 30, Localization.getNum(1), 30, -1, -1));
		test.setText(Localization.getText(7));
		test.setBounds(new Bounds(50 + Localization.getNum(1), 30, Localization.getNum(2), 30, -1, -1));
		textField.setBounds(new Bounds(50 + Localization.getNum(1, 2), 30, 100, 30, -1, -1));
		scoreResetButton.setText(Localization.getText(45));
		scoreResetButton
				.setBounds(new Bounds(160 + Localization.getNum(1, 2), 30, Localization.getNum(19), 30, -1, -1));

		returnButton.setText(Localization.getText(26));
		returnButton.setBounds(new Bounds(-Localization.getNum(3) - 10, -40, Localization.getNum(3), 30, 1, 1));
	}

	private void apply() {
		ArrayList<Kanji> list = new ArrayList<>();
		for (int i = 0; i < checkboxes.length; i++)
			if (checkboxes[i].isChosen())
				for (Kanji k : Cards.getGroups()[i].kanji)
					list.add(k);
		if (list.isEmpty()) {
			if (!warningLabel.getText().equals(noneSelectedWarn))
				warningLabel.changeText(noneSelectedWarn);
			return;
		}

		for (int i = 0; i < checkboxes.length; i++)
			Settings.chosenKanji[i] = checkboxes[i].isChosen();

		if (cycleAll.isOn()) {
			Settings.loopsString = textField.getText();
			Settings.kanjiPickMode = 0;
			warningLabel.changeText("");
			Cards.setCurrentList(list);
			getHolder().setScene(Main.main);
		} else if (test.isOn()) {
			try {
				int num = Integer.parseInt(textField.getText());
				if (num <= 0) {
					if (!warningLabel.getText().equals(invalidNumberWarn))
						warningLabel.changeText(invalidNumberWarn);
				} else {
					warningLabel.changeText("");
					Cards.setExamKanji(list, num);
					getHolder().setScene(Main.main);
				}
				Settings.loopsString = textField.getText();
				Settings.kanjiPickMode = 1;
			} catch (NumberFormatException e) {
				if (!warningLabel.getText().equals(invalidNumberWarn))
					warningLabel.changeText(invalidNumberWarn);
			}
		}
	}
}

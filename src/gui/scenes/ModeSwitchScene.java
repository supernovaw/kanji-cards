package gui.scenes;

import cards.Cards;
import gui.Bounds;
import gui.Localization;
import gui.Scene;
import gui.elements.Button;
import gui.elements.ButtonGroup;
import gui.elements.RadioButton;
import main.Main;
import main.Settings;

public class ModeSwitchScene extends Scene {
	private RadioButton rb0, rb1, rb2, rb3;
	private ButtonGroup group;
	private Button returnButton;

	public ModeSwitchScene() {
		int mode = Cards.getMode();
		int width = Localization.getNum(5), sy = -60;
		group = new ButtonGroup(() -> {
			Main.learningScene = getSelectedMode();
			Cards.setMode(group.getPushedButton());
		});
		rb0 = new RadioButton(Localization.getText(10), mode == 0, new Bounds(-width / 2, sy, width, 30, 0, 0), this);
		rb1 = new RadioButton(Localization.getText(11), mode == 1, new Bounds(-width / 2, sy + 30, width, 30, 0, 0),
				this);
		rb2 = new RadioButton(Localization.getText(12), mode == 2, new Bounds(-width / 2, sy + 60, width, 30, 0, 0),
				this);
		rb3 = new RadioButton(Localization.getText(13), mode == 3, new Bounds(-width / 2, sy + 90, width, 30, 0, 0),
				this);
		add(rb0);
		add(rb1);
		add(rb2);
		add(rb3);
		group.addButton(rb0);
		group.addButton(rb1);
		group.addButton(rb2);
		group.addButton(rb3);
		add(returnButton = new Button(Localization.getText(26), () -> {
			Settings.learningMode = group.getPushedButton();
			getHolder().setScene(Main.main);
		}, new Bounds(-Localization.getNum(3) - 10, -40, Localization.getNum(3), 30, 1, 1), this));
	}

	public void updateLocalization() {
		rb0.setText(Localization.getText(10));
		rb1.setText(Localization.getText(11));
		rb2.setText(Localization.getText(12));
		rb3.setText(Localization.getText(13));

		int width = Localization.getNum(5), sy = -60;
		rb0.setBounds(new Bounds(-width / 2, sy, width, 30, 0, 0));
		rb1.setBounds(new Bounds(-width / 2, sy + 30, width, 30, 0, 0));
		rb2.setBounds(new Bounds(-width / 2, sy + 60, width, 30, 0, 0));
		rb3.setBounds(new Bounds(-width / 2, sy + 90, width, 30, 0, 0));

		returnButton.setText(Localization.getText(26));
		returnButton.setBounds(new Bounds(-Localization.getNum(3) - 10, -40, Localization.getNum(3), 30, 1, 1));
	}

	public Scene getSelectedMode() {
		switch (group.getPushedButton()) {
		case 0:
			return Main.wordcards;
		case 1:
			return Main.kanjiSelectionMode;
		case 2:
			return Main.kanjiDraw;
		case 3:
			return Main.strokePractice;
		default:
			return null;
		}
	}
}

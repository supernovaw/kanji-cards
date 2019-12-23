package gui.scenes;

import gui.Bounds;
import gui.Localization;
import gui.Scene;
import gui.elements.Button;
import main.Main;

public class MainScene extends Scene {
	private Button[] buttons;

	public MainScene() {
		int buttonsWidth = Localization.getNum(0);
		buttons = new Button[6];
		add(buttons[0] = new Button(Localization.getText(0), () -> getHolder().setScene(Main.kanjiSelection),
				new Bounds(-buttonsWidth / 2, -140, buttonsWidth, 30, 0, 0), this));
		add(buttons[1] = new Button(Localization.getText(1), () -> getHolder().setScene(Main.modeSwitch),
				new Bounds(-buttonsWidth / 2, -90, buttonsWidth, 30, 0, 0), this));
		add(buttons[2] = new Button(Localization.getText(2), () -> getHolder().setScene(Main.progress),
				new Bounds(-buttonsWidth / 2, -40, buttonsWidth, 30, 0, 0), this));
		add(buttons[3] = new Button(Localization.getText(3), () -> getHolder().setScene(Main.learningScene),
				new Bounds(-buttonsWidth / 2, 10, buttonsWidth, 30, 0, 0), this));
		add(buttons[4] = new Button(Localization.getText(4), () -> getHolder().setScene(Main.settings),
				new Bounds(-buttonsWidth / 2, 60, buttonsWidth, 30, 0, 0), this));
		add(buttons[5] = new Button(Localization.getText(5), () -> getHolder().setScene(Main.about),
				new Bounds(-buttonsWidth / 2, 110, buttonsWidth, 30, 0, 0), this));
	}

	public void updateLocalization() {
		int buttonsWidth = Localization.getNum(0);
		buttons[0].setBounds(new Bounds(-buttonsWidth / 2, -140, buttonsWidth, 30, 0, 0));
		buttons[1].setBounds(new Bounds(-buttonsWidth / 2, -90, buttonsWidth, 30, 0, 0));
		buttons[2].setBounds(new Bounds(-buttonsWidth / 2, -40, buttonsWidth, 30, 0, 0));
		buttons[3].setBounds(new Bounds(-buttonsWidth / 2, 10, buttonsWidth, 30, 0, 0));
		buttons[4].setBounds(new Bounds(-buttonsWidth / 2, 60, buttonsWidth, 30, 0, 0));
		buttons[5].setBounds(new Bounds(-buttonsWidth / 2, 110, buttonsWidth, 30, 0, 0));

		buttons[0].setText(Localization.getText(0));
		buttons[1].setText(Localization.getText(1));
		buttons[2].setText(Localization.getText(2));
		buttons[3].setText(Localization.getText(3));
		buttons[4].setText(Localization.getText(4));
		buttons[5].setText(Localization.getText(5));
	}
}

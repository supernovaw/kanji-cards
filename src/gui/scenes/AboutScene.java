package gui.scenes;

import java.awt.Graphics;

import gui.Bounds;
import gui.Localization;
import gui.Scene;
import gui.elements.Button;
import gui.elements.Label;
import main.Main;

public class AboutScene extends Scene {
	private Button returnButton;
	private Label[] labels;

	public AboutScene() {
		labels = new Label[4];
		labels[0] = new Label("From Siberia with love", new Bounds(-250, -60, 500, 30, 0, 0), this);
		labels[1] = new Label("I am Dmitrii, 14 years old. I don't have a website or blog", new Bounds(-250, -30, 500, 30, 0, 0), this);
		labels[2] = new Label("you can contact me via Mail ruszrj@gmail.com or", new Bounds(-250, 0, 500, 30, 0, 0), this);
		labels[3] = new Label("Discord supernova#8877. Feel free to. v1.0, 2019/07/30", new Bounds(-250, 30, 500, 30, 0, 0), this);
		for (Label l : labels)
			add(l);
		add(returnButton = new Button(Localization.getText(28), () -> getHolder().setScene(Main.main),
				new Bounds(-Localization.getNum(4) - 10, -40, Localization.getNum(4), 30, 1, 1), this));
	}

	public void updateLocalization() {
		returnButton.setText(Localization.getText(28));
		returnButton.setBounds(new Bounds(-Localization.getNum(4) - 10, -40, Localization.getNum(4), 30, 1, 1));
	}

	public void paintBackground(Graphics g) {
	}
}

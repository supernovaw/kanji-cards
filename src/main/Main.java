package main;

import cards.Cards;
import gui.RepaintingObject;
import gui.Scene;
import gui.Theme;
import gui.Window;
import gui.scenes.AboutScene;
import gui.scenes.ExamResultsScene;
import gui.scenes.KanjiDrawScene;
import gui.scenes.KanjiSelectionModeScene;
import gui.scenes.KanjiSelectionScene;
import gui.scenes.MainScene;
import gui.scenes.ModeSwitchScene;
import gui.scenes.ProgressScene;
import gui.scenes.SettingsScene;
import gui.scenes.StrokePracticeScene;
import gui.scenes.WordcardsScene;

public class Main {
	// all menus to use
	public static Scene main, kanjiSelection, modeSwitch, wordcards, about, kanjiSelectionMode, kanjiDraw,
			strokePractice, progress, examResults, settings;
	public static Scene learningScene;

	public static final long version = 1;
	public static final boolean loadFromJar = false;

	public static void main(String[] args) {
		Settings.load(); // load settings
		Theme.init();
		Cards.load(); // load everything for kanji

		main = new MainScene();
		kanjiSelection = new KanjiSelectionScene();
		modeSwitch = new ModeSwitchScene();
		wordcards = new WordcardsScene();
		about = new AboutScene();
		kanjiDraw = new KanjiDrawScene();
		kanjiSelectionMode = new KanjiSelectionModeScene();
		strokePractice = new StrokePracticeScene();
		progress = new ProgressScene();
		examResults = new ExamResultsScene();
		settings = new SettingsScene();

		learningScene = ((ModeSwitchScene) modeSwitch).getSelectedMode();
		new Window(main, "Kanji cards").show(); // create a window
		RepaintingObject.startThread(); // start a thread which repaints animating elements
		Autosave.run();
	}

	public static void updateLocalization() {
		main.updateLocalization();
		kanjiSelection.updateLocalization();
		modeSwitch.updateLocalization();
		wordcards.updateLocalization();
		about.updateLocalization();
		kanjiSelectionMode.updateLocalization();
		kanjiDraw.updateLocalization();
		strokePractice.updateLocalization();
		progress.updateLocalization();
		examResults.updateLocalization();
	}
}

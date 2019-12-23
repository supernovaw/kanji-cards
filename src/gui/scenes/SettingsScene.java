package gui.scenes;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import gui.Bounds;
import gui.Element;
import gui.Localization;
import gui.Scene;
import gui.Theme;
import gui.elements.Button;
import gui.elements.ButtonGroup;
import gui.elements.HorizontalScrollbar;
import gui.elements.Label;
import gui.elements.RadioButton;
import gui.elements.TextField;
import main.Main;
import main.Settings;

public class SettingsScene extends Scene {
	private static int kanjiAmtMin = 2, kanjiAmtMax = 20;
	private static int themesAmt = 9;

	private static int[] themesFg = { 0xFFFFFF, 0xFFFFFF, 0xFFFFFF, 0xDCDCDC, 0x00FF64, 0xFD1350, 0xFF70D0, 0x0064FF,
			0x000000 },
			themesBg = { 0x000000, 0x000000, 0x000040, 0x000000, 0x000000, 0x303030, 0x64E0FF, 0xFFFFFF, 0xFFFFFF };
	private static String[] themesPics = { "clouds.jpg", "miku.jpg", "charlotte.jpg", "train.png", "tatsumaki.png",
			"umbrella.jpg", "ariimaw.png", "naoandyuu.jpg", "skytreetokyo.jpg" },
			themesNames = { "Clouds", "Miku", "Nao", "Train", "Tatsumaki", "Umbrella", "Ariimaw", "Nao and Yuu",
					"Skytree" };
	private static float[] themesBgAlpha = { .08f, .2f, .5f, .5f, .3f, .35f, .25f, .7f, .1f };
	private static BufferedImage[] loadedPics = new BufferedImage[themesAmt];

	private boolean doUpdLocalization;
	private int lastLanguage = Settings.language;
	private Label themeTitle, suggestedLabel, fgLabel, bgLabel, hexLabel1, hexLabel2, picLabel1, picLabel2, picLabel3,
			picWarnLabel, dimLabel, miscTitle, kanjiAmtLabel, lngLabel;
	private Button themeButtons[], fgButtons[], bgButtons[], openFolderButton, returnButton;
	private TextField fgField, bgField, picField;
	private HorizontalScrollbar dimScroll, kanjiAmtScroll;
	private RadioButton[] languageButtons;
	private ButtonGroup languageGroup;

	private static int kanjiAmt = Settings.kanjiAmt;

	public SettingsScene() {
		themeTitle = new Label(Localization.getText(30), 2 * Theme.fontSize, new Bounds(50, 10, 300, 50, -1, -1), this);
		themeTitle.setAlignmentX(-1);
		suggestedLabel = new Label(Localization.getText(31), new Bounds(50, 70, 300, 30, -1, -1), this);
		suggestedLabel.setAlignmentX(-1);

		themeButtons = new Button[themesAmt];
		for (int i = 0; i < themesAmt; i++) {
			final int j = i;
			themeButtons[i] = new Button(themesNames[i], () -> {
				Theme.changeBg(new Color(themesBg[j]), this);
				Theme.changeFg(new Color(themesFg[j]), this);
				Theme.changeBgAlpha(1 - themesBgAlpha[j], this);
				if (loadedPics[j] == null)
					try {
						BufferedImage img;
						if (Main.loadFromJar)
							img = ImageIO
									.read(SettingsScene.class.getResourceAsStream("/assets/pics/" + themesPics[j]));
						else
							img = ImageIO
									.read(new File(new File("").getAbsolutePath(), "/assets/pics/" + themesPics[j]));
						Settings.picFileString = themesPics[j];
						picField.setText(themesPics[j]);
						Theme.changeBgPic(loadedPics[j] = img, this);
					} catch (Exception e) {
						e.printStackTrace();
					}
				else
					Theme.changeBgPic(loadedPics[j], this);

				bgField.setText(Theme.getHEX(Theme.background));
				fgField.setText(Theme.getHEX(Theme.foreground));
				dimLabel.setText(String.format(Localization.getText(41), Math.round(10000 * themesBgAlpha[j]) / 100d));
				dimScroll.setPos(themesBgAlpha[j]);
			}, new Bounds(50 + i * 115, 100, 105, 30, -1, -1), this);
		}

		fgLabel = new Label(Localization.getText(32), new Bounds(50, 140, Localization.getNum(14), 30, -1, -1), this);
		fgLabel.setAlignmentX(-1);

		int colors = Localization.colors.length;

		fgButtons = new Button[colors];
		for (int i = 0; i < colors; i++) {
			final int j = i;
			fgButtons[i] = new Button(Localization.getColorName(i), () -> {
				Color c = Localization.colors[j];
				if (Theme.background.equals(c))
					return;
				Theme.changeFg(c, this);
				fgField.setText(Theme.getHEX(c));
				fgField.setCaret(7);
			}, new Bounds(60 + Localization.getNum(14) + i * (10 + Localization.getNum(15)), 140,
					Localization.getNum(15), 30, -1, -1), this);
		}

		hexLabel1 = new Label(Localization.getText(34),
				new Bounds(60 + Localization.getNum(14) + colors * (10 + Localization.getNum(15)), 140,
						Localization.getNum(16), 30, -1, -1),
				this);
		hexLabel1.setAlignmentX(1);
		fgField = new TextField(() -> {
			Color c = getColor(fgField.getText());
			if (c != null)
				Theme.changeFg(c, this);
		}, Theme.fontSize, new Bounds(65 + Localization.getNum(14, 16) + colors * (10 + Localization.getNum(15)), 140,
				200, 30, -1, -1), this);
		fgField.setText(Theme.getHEX(Theme.foreground));
		fgField.setCaret(7);

		bgLabel = new Label(Localization.getText(33), new Bounds(50, 180, Localization.getNum(14), 30, -1, -1), this);
		bgLabel.setAlignmentX(-1);

		bgButtons = new Button[colors];
		for (int i = 0; i < colors; i++) {
			final int j = i;
			bgButtons[i] = new Button(Localization.getColorName(i), () -> {
				Color c = Localization.colors[j];
				if (Theme.foreground.equals(c))
					return;
				Theme.changeBg(c, this);
				bgField.setText(Theme.getHEX(c));
				bgField.setCaret(7);
			}, new Bounds(60 + Localization.getNum(14) + i * (10 + Localization.getNum(15)), 180,
					Localization.getNum(15), 30, -1, -1), this);
		}

		hexLabel2 = new Label(Localization.getText(34),
				new Bounds(60 + Localization.getNum(14) + colors * (10 + Localization.getNum(15)), 180,
						Localization.getNum(16), 30, -1, -1),
				this);
		hexLabel2.setAlignmentX(1);
		bgField = new TextField(() -> {
			Color c = getColor(bgField.getText());
			if (c != null)
				Theme.changeBg(c, this);
		}, Theme.fontSize, new Bounds(65 + Localization.getNum(14, 16) + colors * (10 + Localization.getNum(15)), 180,
				200, 30, -1, -1), this);
		bgField.setText(Theme.getHEX(Theme.background));
		bgField.setCaret(7);

		picLabel1 = new Label(Localization.getText(35), new Bounds(50, 230, Localization.getNum(17), 30, -1, -1), this);
		picLabel1.setAlignmentX(-1);
		openFolderButton = new Button(Localization.getText(36), () -> {
			try {
				Desktop.getDesktop().open(Theme.picsFolder);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}, new Bounds(60 + Localization.getNum(17), 230, Localization.getNum(18), 30, -1, -1), this);
		picLabel2 = new Label(Localization.getText(37), new Bounds(50, 260, 500, 30, -1, -1), this);
		picLabel2.setAlignmentX(-1);
		picLabel3 = new Label(Localization.getText(38), new Bounds(50, 290, 500, 30, -1, -1), this);
		picLabel3.setAlignmentX(-1);
		picWarnLabel = new Label("", new Bounds(360, 320, 300, 30, -1, -1), this);
		picWarnLabel.setAlignmentX(-1);
		picField = new TextField(() -> {
			Settings.picFileString = picField.getText();
			if (picField.getText().isEmpty()) {
				Theme.changeBgPic(null, this);
				picWarnLabel.changeText("");
				return;
			}

			File file = getTypedPictureFile(picField.getText());
			boolean pre = isPicturePreinstalled(picField.getText());
			if (file == null && !pre) {
				picWarnLabel.changeText(Localization.getText(39));
				return;
			}
			BufferedImage img;
			try {
				if (pre) {
					if (file == null)
						img = getPreinstalledPicture(picField.getText());
					else
						img = ImageIO.read(file);
				} else
					img = ImageIO.read(file);
			} catch (Exception e) {
				picWarnLabel.changeText(Localization.getText(40));
				return;
			}
			Theme.changeBgPic(img, this);
			picWarnLabel.changeText("");
		}, Theme.fontSize, new Bounds(50, 320, 300, 30, -1, -1), this);
		picField.setText(Settings.picFileString);

		dimLabel = new Label(String.format(Localization.getText(41), Math.round(10000 * (1 - Theme.bgAlpha)) / 100d),
				new Bounds(50, 360, 300, 30, -1, -1), this);
		dimLabel.setAlignmentX(-1);
		dimScroll = new HorizontalScrollbar(() -> {
			dimLabel.setText(String.format(Localization.getText(41), Math.round(10000 * dimScroll.getPos()) / 100d));
			Theme.changeBgAlpha(1f - (float) dimScroll.getPos(), this);
		}, new Bounds(50, 390, 416, 16, -1, -1), this);
		dimScroll.setPos(1 - Theme.bgAlpha);

		miscTitle = new Label(Localization.getText(42), 2 * Theme.fontSize, new Bounds(50, 430, 300, 50, -1, -1), this);
		miscTitle.setAlignmentX(-1);
		kanjiAmtLabel = new Label(String.format(Localization.getText(43), kanjiAmt),
				new Bounds(50, 490, 500, 30, -1, -1), this);
		kanjiAmtLabel.setAlignmentX(-1);
		kanjiAmtScroll = new HorizontalScrollbar(() -> {
			double d = kanjiAmtScroll.getPos();
			Settings.dim = d;
			kanjiAmt = (int) (kanjiAmtMin + Math.round(d * (kanjiAmtMax - kanjiAmtMin)));
			kanjiAmtLabel.setText(String.format(Localization.getText(43), kanjiAmt));
			kanjiAmtLabel.repaint();
		}, new Bounds(50, 520, 200, 16, -1, -1), this);
		kanjiAmtScroll.setPos((double) (kanjiAmt - kanjiAmtMin) / (kanjiAmtMax - kanjiAmtMin));

		lngLabel = new Label(Localization.getText(44), new Bounds(50, 550, 100, 30, -1, -1), this);
		lngLabel.setAlignmentX(-1);
		languageGroup = new ButtonGroup(() -> Settings.language = languageGroup.getPushedButton());
		languageButtons = new RadioButton[Localization.localizations.length];
		for (int i = 0; i < languageButtons.length; i++) {
			languageButtons[i] = new RadioButton(Localization.localizations[i].getName(), i == Settings.language,
					new Bounds(50 + i * 110, 580, 100, 30, -1, -1), this);
			languageGroup.addButton(languageButtons[i]);
		}

		add(themeTitle);
		add(suggestedLabel);
		for (Element e : themeButtons)
			add(e);
		add(fgLabel);
		for (Element e : fgButtons)
			add(e);
		add(hexLabel1);
		add(fgField);

		add(bgLabel);
		for (Element e : bgButtons)
			add(e);
		add(hexLabel2);
		add(bgField);

		add(picLabel1);
		add(openFolderButton);
		add(picLabel2);
		add(picLabel3);
		add(picField);
		add(picWarnLabel);
		add(dimLabel);
		add(dimScroll);
		add(miscTitle);
		add(kanjiAmtLabel);
		add(kanjiAmtScroll);
		add(lngLabel);
		for (Element e : languageButtons)
			add(e);

		add(returnButton = new Button(Localization.getText(28), () -> apply(),
				new Bounds(-Localization.getNum(4) - 10, -40, Localization.getNum(4), 30, 1, 1), this));
	}

	public void updateLocalization() {
		themeTitle.setText(Localization.getText(30));
		suggestedLabel.setText(Localization.getText(31));
		fgLabel.setText(Localization.getText(32));
		fgLabel.setBounds(new Bounds(50, 140, Localization.getNum(14), 30, -1, -1));
		bgLabel.setText(Localization.getText(33));
		bgLabel.setBounds(new Bounds(50, 180, Localization.getNum(14), 30, -1, -1));

		int colors = Localization.colors.length;
		hexLabel1.setText(Localization.getText(34));
		hexLabel2.setText(Localization.getText(34));
		hexLabel1.setBounds(new Bounds(60 + Localization.getNum(14) + colors * (10 + Localization.getNum(15)), 140,
				Localization.getNum(16), 30, -1, -1));
		hexLabel2.setBounds(new Bounds(60 + Localization.getNum(14) + colors * (10 + Localization.getNum(15)), 180,
				Localization.getNum(16), 30, -1, -1));
		fgField.setBounds(new Bounds(65 + Localization.getNum(14, 16) + colors * (10 + Localization.getNum(15)), 140,
				200, 30, -1, -1));
		bgField.setBounds(new Bounds(65 + Localization.getNum(14, 16) + colors * (10 + Localization.getNum(15)), 180,
				200, 30, -1, -1));
		for (int i = 0; i < colors; i++) {
			fgButtons[i].setText(Localization.getColorName(i));
			bgButtons[i].setText(Localization.getColorName(i));

			fgButtons[i].setBounds(new Bounds(60 + Localization.getNum(14) + i * (10 + Localization.getNum(15)), 140,
					Localization.getNum(15), 30, -1, -1));
			bgButtons[i].setBounds(new Bounds(60 + Localization.getNum(14) + i * (10 + Localization.getNum(15)), 180,
					Localization.getNum(15), 30, -1, -1));
		}

		picLabel1.setText(Localization.getText(35));
		picLabel1.setBounds(new Bounds(50, 230, Localization.getNum(17), 30, -1, -1));
		openFolderButton.setText(Localization.getText(36));
		openFolderButton.setBounds(new Bounds(60 + Localization.getNum(17), 230, Localization.getNum(18), 30, -1, -1));
		picLabel2.setText(Localization.getText(37));
		picLabel3.setText(Localization.getText(38));
		dimLabel.setText(String.format(Localization.getText(41), Math.round(10000 * (1 - Theme.bgAlpha)) / 100d));
		miscTitle.setText(Localization.getText(42));
		kanjiAmtLabel.setText(String.format(Localization.getText(43), kanjiAmt));
		lngLabel.setText(Localization.getText(44));

		returnButton.setText(Localization.getText(28));
		returnButton.setBounds(new Bounds(-Localization.getNum(4) - 10, -40, Localization.getNum(4), 30, 1, 1));
	}

	public void onShut() {
		if (doUpdLocalization)
			updateLocalization();
	}

	private void apply() {
		int lng = languageGroup.getPushedButton();
		if (doUpdLocalization = lng != lastLanguage) {
			Localization.updateLanguage(lng);
			lastLanguage = lng;
			Main.updateLocalization();
		}
		Settings.kanjiAmt = kanjiAmt;
		((KanjiSelectionModeScene) Main.kanjiSelectionMode).changeKanjiAmt(kanjiAmt);
		picWarnLabel.changeText("");
		getHolder().setScene(Main.main);
	}

	private Color getColor(String text) {
		text = text.toLowerCase();
		text = text.trim();
		if (text.isEmpty())
			return null;
		if (text.charAt(0) == '#')
			text = text.substring(1);
		if (text.length() != 6)
			return null;
		int color;
		try {
			color = Integer.parseInt(text, 16);
		} catch (NumberFormatException e) {
			return null;
		}
		if (color < 0)
			return null;
		return new Color(color);
	}

	public static File getTypedPictureFile(String text) {
		File[] files = Theme.picsFolder.listFiles();
		for (int i = 0; i < files.length; i++)
			if (files[i].getName().equalsIgnoreCase(text))
				return files[i].isFile() ? files[i] : null;
		for (int i = 0; i < files.length; i++) {
			if (!files[i].isFile())
				continue;
			String name = files[i].getName();
			if (!name.contains("."))
				continue;
			name = name.substring(0, name.lastIndexOf('.'));
			if (name.equalsIgnoreCase(text))
				return files[i];
		}
		return null;
	}

	public static boolean isPicturePreinstalled(String text) {
		for (int i = 0; i < themesPics.length; i++)
			if (themesPics[i].equals(text))
				return true;
		return false;
	}

	public static BufferedImage getPreinstalledPicture(String pic) {
		try {
			if (Main.loadFromJar)
				return ImageIO.read(SettingsScene.class.getResourceAsStream("/assets/pics/" + pic));
			else
				return ImageIO.read(new File(new File("").getAbsolutePath(), "/assets/pics/" + pic));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}

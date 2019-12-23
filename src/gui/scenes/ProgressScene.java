package gui.scenes;

import cards.Cards;
import cards.KanjiGroup;
import cards.Stats;
import gui.Bounds;
import gui.Localization;
import gui.RepaintingObject;
import gui.Scene;
import gui.elements.Button;
import gui.elements.ButtonGroup;
import gui.elements.Label;
import gui.elements.ProgressDisplay;
import gui.elements.RadioButton;
import gui.elements.TextField;
import main.Main;
import main.Settings;

public class ProgressScene extends Scene {
	private static String[][] unitNames = {
			{ "second", "seconds", "sec", "secs", "s", "„ƒ„u„{„…„~„t„p", "„ƒ„u„{„…„~„t„", "„ƒ„u„{„…„~„t", "„ƒ„u„{", "„ƒ" },
			{ "minute", "minutes", "min", "mins", "„}„y„~„…„„„p", "„}„y„~„…„„„", "„}„y„~„…„„", "„}„y„~" },
			{ "hour", "hours", "hrs", "h", "„‰„p„ƒ", "„‰„p„ƒ„", "„‰„p„ƒ„€„r", "„‰" },
			{ "day", "days", "d", "„t„u„~„Ž", "„t„~„u„z", "„t„~„‘", "„t„~", "„t" },
			{ "week", "weeks", "w", "„~„u„t„u„|„‘", "„~„u„t„u„|„Ž", "„~„u„t„u„|„y", "„~„u„t", "„~„t", "„~" },
			{ "month", "months", "mo", "m", "„}„u„ƒ„‘„ˆ", "„}„u„ƒ„‘„ˆ„u„r", "„}„u„ƒ„‘„ˆ„p", "„}„u„ƒ", "„}" },
			{ "year", "years", "yr", "y", "„s„€„t", "„s„€„t„p", "„|„u„„", "„s" } };
	private static long[] unitDurations = { 1000, 60000, 3600000, 86400000, 604800000, 18144000000L, 220752000000L };

	private int fillChangeDuration = 500;

	private Label modeLabel, periodLabel;
	private ProgressDisplay[] displays;
	private RadioButton rb0, rb1, rb2, rb3, showAmt, showPercents;
	private ButtonGroup modeButtons, infoTypeButtons;
	private TextField periodField;
	private int mode = Settings.progressLearningMode, infoType = Settings.progressInfoType, correct[], wrong[], sum[];
	private RepaintingObject rep;
	private long fillChangeTime, period;
	private Button returnButton;

	public ProgressScene() {
		modeLabel = new Label(Localization.getText(14), new Bounds(50, 30, Localization.getNum(6), 30, -1, -1), this);
		modeLabel.setAlignmentX(-1);
		add(modeLabel);
		rb0 = new RadioButton(Localization.getText(15), mode == 0,
				new Bounds(50 + Localization.getNum(6), 30, Localization.getNum(7), 30, -1, -1), this);
		rb1 = new RadioButton(Localization.getText(16), mode == 1,
				new Bounds(50 + Localization.getNum(6, 7), 30, Localization.getNum(8), 30, -1, -1), this);
		rb2 = new RadioButton(Localization.getText(17), mode == 2,
				new Bounds(50 + Localization.getNum(6, 7, 8), 30, Localization.getNum(9), 30, -1, -1), this);
		rb3 = new RadioButton(Localization.getText(18), mode == 3,
				new Bounds(50 + Localization.getNum(6, 7, 8, 9), 30, Localization.getNum(10), 30, -1, -1), this);
		modeButtons = new ButtonGroup(() -> {
			mode = modeButtons.getPushedButton();
			Settings.progressLearningMode = mode;
			upd();
		});
		modeButtons.addButton(rb0);
		modeButtons.addButton(rb1);
		modeButtons.addButton(rb2);
		modeButtons.addButton(rb3);
		add(rb0);
		add(rb1);
		add(rb2);
		add(rb3);

		showAmt = new RadioButton(Localization.getText(19), Settings.progressInfoType == 0,
				new Bounds(50, 70, Localization.getNum(11), 30, -1, -1), this);
		showPercents = new RadioButton(Localization.getText(20), Settings.progressInfoType == 1,
				new Bounds(50 + Localization.getNum(11), 70, Localization.getNum(12), 30, -1, -1), this);
		infoTypeButtons = new ButtonGroup(() -> {
			infoType = infoTypeButtons.getPushedButton();
			Settings.progressInfoType = infoType;
			upd();
		});
		infoTypeButtons.addButton(showAmt);
		infoTypeButtons.addButton(showPercents);
		add(showAmt);
		add(showPercents);

		periodLabel = new Label(Localization.getText(21), new Bounds(50, 110, Localization.getNum(13), 30, -1, -1),
				this);
		periodLabel.setAlignmentX(-1);
		add(periodLabel);
		periodField = new TextField(() -> {
			Settings.periodString = periodField.getText();
			updPeriod();
		}, 24f, new Bounds(50 + Localization.getNum(13), 110, 150, 30, -1, -1), this);
		periodField.setText(Settings.periodString);
		add(periodField);

		KanjiGroup[][] grades = Cards.getGrades();
		displays = new ProgressDisplay[Cards.getGroups().length];
		for (int grade = 0, i = 0; grade < grades.length; grade++)
			for (int group = 0; group < grades[grade].length; group++) {
				displays[i] = new ProgressDisplay(grades[grade][group], fillChangeDuration,
						new Bounds(50 + grade * 155, 160 + group * 25, 150, 20, -1, -1), this);
				add(displays[i]);
				i++;
			}

		add(returnButton = new Button(Localization.getText(28), () -> getHolder().setScene(Main.main),
				new Bounds(-Localization.getNum(4) - 10, -40, Localization.getNum(4), 30, 1, 1), this));
		rep = new RepaintingObject(() -> {
			repaint();
			if (System.currentTimeMillis() - fillChangeTime > 2 * fillChangeDuration)
				rep.setActive(false);
		});
	}

	public void updateLocalization() {
		modeLabel.setText(Localization.getText(14));
		modeLabel.setBounds(new Bounds(50, 30, Localization.getNum(6), 30, -1, -1));

		rb0.setText(Localization.getText(15));
		rb1.setText(Localization.getText(16));
		rb2.setText(Localization.getText(17));
		rb3.setText(Localization.getText(18));

		rb0.setBounds(new Bounds(50 + Localization.getNum(6), 30, Localization.getNum(7), 30, -1, -1));
		rb1.setBounds(new Bounds(50 + Localization.getNum(6, 7), 30, Localization.getNum(8), 30, -1, -1));
		rb2.setBounds(new Bounds(50 + Localization.getNum(6, 7, 8), 30, Localization.getNum(9), 30, -1, -1));
		rb3.setBounds(new Bounds(50 + Localization.getNum(6, 7, 8, 9), 30, Localization.getNum(10), 30, -1, -1));

		showAmt.setText(Localization.getText(19));
		showAmt.setBounds(new Bounds(50, 70, Localization.getNum(11), 30, -1, -1));
		showPercents.setText(Localization.getText(20));
		showPercents.setBounds(new Bounds(50 + Localization.getNum(11), 70, Localization.getNum(12), 30, -1, -1));
		periodLabel.setText(Localization.getText(21));
		periodLabel.setBounds(new Bounds(50, 110, Localization.getNum(13), 30, -1, -1));

		periodField.setBounds(new Bounds(50 + Localization.getNum(13), 110, 150, 30, -1, -1));
		returnButton.setText(Localization.getText(28));
		returnButton.setBounds(new Bounds(-Localization.getNum(4) - 10, -40, Localization.getNum(4), 30, 1, 1));
	}

	private void upd() {
		fillChangeTime = System.currentTimeMillis();
		updInfo(false);
		rep.setActive(true);
	}

	private void updInfo(boolean instantChange) {
		long time = System.currentTimeMillis();
		long start = time - period;
		KanjiGroup[] groups = Cards.getGroups();
		correct = new int[groups.length];
		wrong = new int[groups.length];
		sum = new int[groups.length];
		int maxSum = 0;
		for (int i = 0; i < groups.length; i++) {
			this.correct[i] = Stats.getLog(mode * 2).getAmount(start, groups[i].getIndices());
			this.wrong[i] = Stats.getLog(mode * 2 + 1).getAmount(start, groups[i].getIndices());
			this.sum[i] = correct[i] + wrong[i];
			if (maxSum < sum[i])
				maxSum = sum[i];
		}

		for (int i = 0; i < groups.length; i++) {
			String info;
			if (infoType == 0)
				info = Integer.toString(sum[i]);
			else if (infoType == 1) {
				if (sum[i] == 0)
					info = "N/A";
				else
					info = (int) Math.round(100d * correct[i] / sum[i]) + "%";
			} else
				throw new Error("infoType is neither 0 or 1 (" + infoType + ")");
			displays[i].setText(groups[i].name + ": " + info);
			double f;
			if (infoType == 0)
				f = maxSum != 0 ? (double) sum[i] / maxSum : 0;
			else
				f = sum[i] != 0 ? (double) correct[i] / sum[i] : 0;
			if (instantChange)
				displays[i].setFillFraction(f);
			else
				displays[i].changeFillFraction(f, time);
		}
	}

	private void updPeriod() {
		String periodString = periodField.getText();
		periodString = periodString.replace(" ", "");
		periodString = periodString.replace(",", ".");
		periodString = periodString.toLowerCase();
		int numsAmt = periodString.length();
		boolean dot = false;
		for (int i = 0; i < periodString.length(); i++) {
			char c = periodString.charAt(i);
			if (c >= '0' && c <= '9')
				continue;
			if (c == '.' && !dot) {
				dot = true;
				continue;
			}
			numsAmt = i;
			break;
		}
		if (numsAmt == 0)
			return;
		double number = Double.parseDouble(periodString.substring(0, numsAmt));
		if (number <= 0)
			return;

		String unit = periodString.substring(numsAmt);
		int unitIndex = -1;
		outer: for (int i = 0; i < unitNames.length; i++)
			for (int j = 0; j < unitNames[i].length; j++)
				if (unitNames[i][j].equals(unit)) {
					unitIndex = i;
					break outer;
				}
		if (unitIndex == -1)
			return;
		period = (long) (number * unitDurations[unitIndex]);
		upd();
	}

	public void onDisplay() {
		updInfo(false);
		rep.add();
	}

	public void onShut() {
		rep.remove();
	}
}

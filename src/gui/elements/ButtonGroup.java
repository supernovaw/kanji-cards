package gui.elements;

import java.util.ArrayList;

public class ButtonGroup {
	private ArrayList<RadioButton> buttons;
	private Runnable onPush;
	private int pushedButton;

	public ButtonGroup() {
		buttons = new ArrayList<>();
	}

	public ButtonGroup(Runnable onPush) {
		this();
		this.onPush = onPush;
	}

	public void addButton(RadioButton b) {
		if (b.isOn())
			pushedButton = buttons.size();
		buttons.add(b);
		b.setGroup(this);
	}

	public int getPushedButton() {
		return pushedButton;
	}

	public void push(RadioButton b) {
		int index = buttons.indexOf(b);
		if (index != pushedButton) {
			buttons.forEach(rb -> {
				if (!rb.equals(b))
					rb.setOn(false);
				else
					rb.setOn(true);
			});
			pushedButton = index;
			if (onPush != null)
				onPush.run();
		}
	}
}

package gui.elements;

import java.awt.AlphaComposite;
import java.awt.Canvas;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.KeyEvent;

import gui.Bounds;
import gui.Element;
import gui.HoverCalc;
import gui.Localization;
import gui.RepaintingObject;
import gui.Scene;
import gui.Theme;

public class TextField extends Element {
	private int caretCycleTime = 300, caretThickness = 2, focusTime = 500;
	private double defaultTextColorFraction = 0.4d; // fraction between theme.fg and bg to draw defaulttext

	private String text;
	private Point textPos;
	private Runnable r;
	private Font font;
	private float fontSize;
	private int caretPos;
	private Rectangle caretBounds;
	private long focusGained, stopRepainting;
	private HoverCalc focusCalc;
	private RepaintingObject caretRep;

	public TextField(Runnable r, Bounds b, Scene container) {
		setBounds(b);
		setContainer(container);
		this.r = r;
		text = "";
		fontSize = b.getHeight();
		font = Theme.interfaceFont.deriveFont(fontSize);
		focusCalc = new HoverCalc(focusTime, this, false);
		caretRep = new RepaintingObject(() -> repaintCaret());
		updTextPos();
		updCaretBounds();
	}

	public TextField(Runnable r, float fontSize, Bounds b, Scene container) {
		setBounds(b);
		setContainer(container);
		this.r = r;
		text = "";
		this.fontSize = fontSize;
		font = Theme.interfaceFont.deriveFont(fontSize);
		focusCalc = new HoverCalc(focusTime, this, false);
		caretRep = new RepaintingObject(() -> repaintCaret());
		updTextPos();
		updCaretBounds();
	}

	public void onSizeChange() {
		updTextPos();
		updCaretBounds();
	}

	private void updTextPos() {
		Rectangle r = getBoundsR();
		FontMetrics m = new Canvas().getFontMetrics(font);
		textPos = new Point(5, r.height / 2 + (m.getAscent() - m.getDescent()) / 2);
	}

	private void updCaretBounds() {
		FontMetrics m = new Canvas().getFontMetrics(font);
		caretBounds = new Rectangle(textPos.x + m.stringWidth(text.substring(0, caretPos)) - caretThickness / 2,
				(int) ((getBounds().getHeight() - fontSize) / 2), caretThickness, (int) fontSize);
	}

	public void keyTyped(KeyEvent e) {
		if (!isFocused() || e.getID() != KeyEvent.KEY_PRESSED)
			return; // return in case not focused or event is not on key press
		int k = e.getKeyCode();
		if (k == KeyEvent.VK_LEFT) { // move caret left
			caretPos = Math.max(caretPos - 1, 0);
			updCaretBounds();
		} else if (k == KeyEvent.VK_RIGHT) { // move caret right
			caretPos = Math.min(caretPos + 1, text.length());
			updCaretBounds();
		} else if (k == KeyEvent.VK_HOME) { // move caret to 0
			caretPos = 0;
			updCaretBounds();
		} else if (k == KeyEvent.VK_END) { // move caret to end
			caretPos = text.length();
			updCaretBounds();
		} else { // type key
			char ch = e.getKeyChar();
			if (ch == 65535) {
				return; // if not a character
			} else if (ch == 10) {
				onEnter(); // enter
			} else if (ch == 8) { // backspace
				int rem;
				if ((e.getModifiers() & 0b10) == 0b10) { // if ctrl held
					rem = text.lastIndexOf(' ', caretPos);
					while (rem > 0) {
						if (text.charAt(rem) == ' ')
							rem--;
						else
							break;
					}
					rem++;
				} else
					rem = caretPos - 1;
				rem = Math.max(rem, 0);
				text = text.substring(0, rem) + text.substring(caretPos);
				caretPos = rem;
				updCaretBounds();
			} else if (ch == 127) { // delete
				if ((e.getModifiers() & 0b10) == 0b10) { // if ctrl held
					int rem = text.indexOf(' ', caretPos);
					if (rem == -1)
						text = text.substring(0, caretPos);
					else {
						while (rem < text.length()) {
							if (text.charAt(rem) == ' ')
								rem++;
							else
								break;
						}
						text = text.substring(0, caretPos) + text.substring(rem);
					}
				} else if (text.length() > caretPos)
					text = text.substring(0, caretPos) + text.substring(caretPos + 1);
			} else if (ch == 27) { // escape
			} else {
				if (e.getModifiers() == 0b10 && e.getKeyCode() == KeyEvent.VK_V) {
					try {
						Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
						for (DataFlavor f : clipboard.getAvailableDataFlavors())
							if (f.equals(DataFlavor.stringFlavor)) {
								String data = (String) clipboard.getData(f);
								text = text.substring(0, caretPos) + data + text.substring(caretPos);
								caretPos += data.length();
								updCaretBounds();
								break;
							}
					} catch (Exception exception) {
						exception.printStackTrace();
					}
				} else if ((e.getModifiers() & 0b10) == 0) {
					text = text.substring(0, caretPos) + ch + text.substring(caretPos);
					caretPos++;
					updCaretBounds();
				}
			}
		}
		repaint();
	}

	private void onEnter() {
		if (r != null)
			r.run();
	}

	public String getText() {
		return text;
	}

	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D) g.create();
		Rectangle r = getBoundsR();
		g2d.setClip(r.x, r.y, r.width, r.height);
		g2d.setFont(font);

		boolean drawDefaultText = text.isEmpty();
		Composite comp = g2d.getComposite();
		float alpha;
		if (drawDefaultText) {
			alpha = (float) HoverCalc.sineSigmoid(1 - focusCalc.getPhase());
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
			g2d.setColor(Theme.interpolate(Theme.getFg(), Theme.getBg(), defaultTextColorFraction));
			g2d.drawString(Localization.getText(29), r.x + textPos.x, r.y + textPos.y);
			g2d.setComposite(comp);
		}
		g2d.setColor(Theme.getFg());
		g2d.drawString(text, r.x + textPos.x, r.y + textPos.y);

		if (caretRep.isActive()) {
			float t = (float) (System.currentTimeMillis() - focusGained) / caretCycleTime;
			alpha = t % 2;
			alpha = alpha - 2 * Math.max(alpha - 1, 0);
			alpha = (float) HoverCalc.sineSigmoid(alpha);

			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
			g2d.fillRect(r.x + caretBounds.x, r.y + caretBounds.y, caretBounds.width, caretBounds.height);
			g2d.setComposite(comp);
		}
	}

	public void onFocusChange(boolean newFocus) {
		if (newFocus) {
			if (stopRepainting == 0) {
				focusGained = System.currentTimeMillis();
				caretRep.setActive(true);
			} else
				stopRepainting = 0;
		}
		focusCalc.focusChanged(newFocus);
		repaint();
	}

	public void setText(String text) {
		this.text = text;
		caretPos = Math.min(caretPos, text.length());
		updCaretBounds();
		repaint();
	}

	public void setCaret(int caret) {
		if (caret <= 0)
			caretPos = 0;
		else
			caretPos = Math.min(caret, text.length());
		updCaretBounds();
		repaint();
	}

	private void repaintCaret() {
		Rectangle r = getBoundsR();
		if (caretBounds.x < r.width) {
			if (caretBounds.x + caretBounds.width < r.width)
				repaint(new Rectangle(r.x + caretBounds.x, r.y + caretBounds.y, caretBounds.width, caretBounds.height));
			else
				repaint(new Rectangle(r.x + caretBounds.x, r.y + caretBounds.y, r.width - r.x - caretBounds.x,
						caretBounds.height));
		}
		if (!isFocused() && stopRepainting == 0) {
			long passed = System.currentTimeMillis() - focusGained;
			stopRepainting = focusGained + passed - passed % (2 * caretCycleTime) + 2 * caretCycleTime;
		}
		if (stopRepainting != 0 && System.currentTimeMillis() > stopRepainting) {
			caretRep.setActive(false);
			stopRepainting = 0;
		}
	}

	public void onDisplay() {
		focusCalc.start();
		caretRep.add();
	}

	public void onShut() {
		focusCalc.stop();
		caretRep.remove();
		stopRepainting = 0;
	}
}

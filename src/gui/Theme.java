package gui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import gui.scenes.SettingsScene;
import main.Main;
import main.Settings;

public class Theme {
	public static int changeTime = 500;
	public static Color background = new Color(Settings.bg), foreground = new Color(Settings.fg);
	public static Color secondary = new Color(250, 30, 30);
	public static Font interfaceFont, japaneseFont;
	public static float fontSize = 20f, bgAlpha = 1 - (float) Settings.dim, oldBgAlpha;
	public static BufferedImage bgPic, oldBgPic;
	public static File picsFolder = new File(
			"C:/Users/" + System.getProperty("user.name") + "/AppData/Roaming/Kanji learning app by supernova/pics/");
	private static Color oldBg, oldFg;
	private static long bgChange, fgChange, bgAlphaChange, bgPicChange;
	private static RepaintingObject rep;
	private static Scene sceneToRepaint;
	private static boolean bgRepComplete, fgRepComplete, bgAlphaRepComplete, bgPicRepComplete;

	public static void init() {
		(rep = new RepaintingObject(() -> sceneToRepaint.repaint())).add();

		if (!picsFolder.exists())
			picsFolder.mkdir();

		try {
			if (Main.loadFromJar) {
				interfaceFont = Font.createFont(0,
						Theme.class.getResourceAsStream("/assets/ProximaNovaCond-Regular.ttf"));
				japaneseFont = Font.createFont(0, Theme.class.getResourceAsStream("/assets/UDDigiKyokashoN-R.ttc"));
			} else {
				interfaceFont = Font.createFont(0,
						new File(new File("").getAbsoluteFile(), "/assets/ProximaNovaCond-Regular.ttf"));
				japaneseFont = Font.createFont(0,
						new File(new File("").getAbsolutePath(), "/assets/UDDigiKyokashoN-R.ttc"));
			}
			File typedPic = SettingsScene.getTypedPictureFile(Settings.picFileString);
			boolean pre = SettingsScene.isPicturePreinstalled(Settings.picFileString);
			if (pre) {
				if (typedPic != null)
					bgPic = ImageIO.read(typedPic);
				else
					bgPic = SettingsScene.getPreinstalledPicture(Settings.picFileString);
			} else
				bgPic = typedPic == null ? null : ImageIO.read(typedPic);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static boolean repComplete() {
		return bgRepComplete && fgRepComplete && bgAlphaRepComplete && bgPicRepComplete;
	}

	public static Color getBg() {
		if (bgRepComplete)
			return background;
		double t = (double) (System.currentTimeMillis() - bgChange) / changeTime;
		if (t > 2) {
			bgRepComplete = true;
			if (repComplete())
				rep.setActive(false);
			return background;
		}
		if (t > 1)
			return background;
		t = HoverCalc.sineSigmoid(t);
		return interpolate(oldBg, background, t);
	}

	public static Color getFg() {
		if (fgRepComplete)
			return foreground;
		double t = (double) (System.currentTimeMillis() - fgChange) / changeTime;
		if (t > 2) {
			fgRepComplete = true;
			if (repComplete())
				rep.setActive(false);
			return foreground;
		}
		if (t > 1)
			return foreground;
		t = HoverCalc.sineSigmoid(t);
		return interpolate(oldFg, foreground, t);
	}

	public static float getBgAlpha() {
		if (bgAlphaRepComplete)
			return bgAlpha;
		double t = (double) (System.currentTimeMillis() - bgAlphaChange) / changeTime;
		if (t > 2) {
			bgAlphaRepComplete = true;
			if (repComplete())
				rep.setActive(false);
			return bgAlpha;
		}
		if (t > 1)
			return bgAlpha;
		t = HoverCalc.sineSigmoid(t);
		return (float) (oldBgAlpha * (1 - t) + bgAlpha * t);
	}

	private static void drawPic(BufferedImage img, Graphics2D g2d, Dimension d) {
		if (img == null) {
			g2d.setColor(getBg());
			g2d.fillRect(0, 0, d.width, d.height);
			return;
		}
		double scale = Math.max(d.getWidth() / img.getWidth(), d.getHeight() / img.getHeight());
		AffineTransform t = new AffineTransform();
		t.translate(d.getWidth() / 2 - scale * img.getWidth() / 2, d.getHeight() / 2 - scale * img.getHeight() / 2);
		t.scale(scale, scale);
		g2d.drawImage(img, t, null);
	}

	public static void drawBgPic(Graphics2D g, Dimension d) {
		if (bgPicRepComplete) {
			drawPic(bgPic, g, d);
			return;
		}
		double t = (double) (System.currentTimeMillis() - bgPicChange) / changeTime;
		if (t > 2) {
			bgPicRepComplete = true;
			if (repComplete())
				rep.setActive(false);
			drawPic(bgPic, g, d);
			return;
		}
		if (t > 1) {
			drawPic(bgPic, g, d);
			return;
		}
		t = HoverCalc.sineSigmoid(t);
		drawPic(oldBgPic, g, d);
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) t));
		drawPic(bgPic, g, d);
		g.setComposite(AlphaComposite.SrcOver);
	}

	public static void changeBg(Color bg, Scene s) {
		Settings.bg = bg.getRGB();
		oldBg = background;
		background = bg;
		bgChange = System.currentTimeMillis();
		bgRepComplete = false;
		sceneToRepaint = s;
		rep.setActive(true);
	}

	public static void changeFg(Color fg, Scene s) {
		Settings.fg = fg.getRGB();
		oldFg = foreground;
		foreground = fg;
		fgChange = System.currentTimeMillis();
		fgRepComplete = false;
		sceneToRepaint = s;
		rep.setActive(true);
	}

	public static void changeBgAlpha(float alpha, Scene s) {
		Settings.dim = 1 - alpha;
		oldBgAlpha = bgAlpha;
		bgAlpha = alpha;
		bgAlphaChange = System.currentTimeMillis();
		bgAlphaRepComplete = false;
		sceneToRepaint = s;
		rep.setActive(true);
	}

	public static void changeBgPic(BufferedImage img, Scene s) {
		oldBgPic = bgPic;
		bgPic = img;
		bgPicChange = System.currentTimeMillis();
		bgPicRepComplete = false;
		sceneToRepaint = s;
		rep.setActive(true);
	}

	public static void paintBackground(Graphics g, Dimension d) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(getBg());
		g2d.fillRect(0, 0, d.width + 1, d.height + 1);
		drawBgPic(g2d, d);
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1 - getBgAlpha()));
		g2d.setColor(getBg());
		g2d.fillRect(0, 0, d.width + 1, d.height + 1);
		g2d.setComposite(AlphaComposite.SrcOver);
	}

	public static Color interpolate(Color c1, Color c2, double f) {
		double r = c1.getRed() * (1 - f) + c2.getRed() * f, g = c1.getGreen() * (1 - f) + c2.getGreen() * f,
				b = c1.getBlue() * (1 - f) + c2.getBlue() * f;
		return new Color((int) r, (int) g, (int) b);
	}

	public static String getHEX(Color c) {
		String r = Integer.toHexString(c.getRed());
		String g = Integer.toHexString(c.getGreen());
		String b = Integer.toHexString(c.getBlue());
		if (r.length() == 1)
			r = '0' + r;
		if (g.length() == 1)
			g = '0' + g;
		if (b.length() == 1)
			b = '0' + b;
		return ('#' + r + g + b).toUpperCase();
	}
}

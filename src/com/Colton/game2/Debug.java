package com.Colton.game2;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;

public class Debug {
	private static UnicodeFont font;

	private String info = "";
	
	public void print(Object str) {
		info += str.toString();
	}
	
	public void println(Object str) {
		info += str.toString() + '\n';
	}
	
	public void render() {
		Debug.font.drawString(10, 10, info);
		info = "";
	}
	
	@SuppressWarnings("unchecked")
	public Debug() {
		java.awt.Font awtFont = new java.awt.Font("Terminus", java.awt.Font.BOLD, 18);
		Debug.font = new UnicodeFont(awtFont);
		Debug.font.getEffects().add(new ColorEffect(java.awt.Color.white));
		Debug.font.addAsciiGlyphs();
		try {
			Debug.font.loadGlyphs();
		} catch(SlickException e) {
			e.printStackTrace();
		}
	}
}

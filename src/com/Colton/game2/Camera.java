package com.Colton.game2;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector2f;

public class Camera {
	
	public void tick(TestSelf game) {
		boolean keyup = Keyboard.isKeyDown(Keyboard.KEY_W) || Keyboard.isKeyDown(Keyboard.KEY_UP);
		boolean keydown = Keyboard.isKeyDown(Keyboard.KEY_S) || Keyboard.isKeyDown(Keyboard.KEY_DOWN);
		boolean keyleft = Keyboard.isKeyDown(Keyboard.KEY_A) || Keyboard.isKeyDown(Keyboard.KEY_LEFT);
		boolean keyright = Keyboard.isKeyDown(Keyboard.KEY_D) || Keyboard.isKeyDown(Keyboard.KEY_RIGHT);
		boolean keyjump = Keyboard.isKeyDown(Keyboard.KEY_SPACE);
		boolean keyfall = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT);
		
		if(Mouse.isButtonDown(2))
		{
			if (!Mouse.isGrabbed())Mouse.setGrabbed(true);
			float mouseDX = Mouse.getDX() * game.mouseSpeed * 0.16f;
			//float mouseDY = Mouse.getDY() * mouseSpeed * 0.16f;
			if (game.rot.x + mouseDX >= 360)
				game.rot.x = game.rot.x + mouseDX - 360;
			else if (game.rot.x + mouseDX < 0) 
				game.rot.x = 360 - game.rot.x + mouseDX;
			else
				game.rot.x += mouseDX;
		}
		else if(Mouse.isGrabbed())Mouse.setGrabbed(false);
		
		Vector2f relmovement = new Vector2f(0,0);
		float angle = game.rot.x;
		if(keyleft) {
			relmovement.x += 0.006f;
		}
		if(keyright) {
			relmovement.x -= 0.006f;
		}
		if(keyup) {
			relmovement.y += 0.006f;
			game.pos.x -= 0.006f * (float)Math.sin(Math.toRadians(game.rot.x));
			game.pos.z += 0.006f * (float)Math.cos(Math.toRadians(game.rot.x));
		}
		if(keydown) {
			relmovement.y -= 0.006f;
			game.pos.x += 0.006f * (float)Math.sin(Math.toRadians(game.rot.x));
			game.pos.z -= 0.006f * (float)Math.cos(Math.toRadians(game.rot.x));
		}
		
		double distance = Math.sqrt(square(relmovement.x)+square(relmovement.y));
		game.debug.print("Distance: ");
		game.debug.println(distance);
		game.debug.print("Angle: ");
		game.debug.println(angle);/**/
		
		if(keyjump)  game.pos.y -= 0.003f;
		if(keyfall)  game.pos.y += 0.003f;
		//game.pos.x += relmovement.x;
		//game.pos.z += relmovement.y;
	}
	
	private double square(double num) {
		return num*num;
	}
	
}

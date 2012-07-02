package com.Colton.game;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.*;

public class CubeTest1 {
	public enum type{AIR, BLOCK}
	private type kind = type.BLOCK;
	private boolean[] render = new boolean[6];
	
	public void setNeighbors(CubeTest1[] cubes){ //27-1=26
		//top,bottom,left,front,right,back
		//front,left,back,right,top,bottom
		for(int i=0;i<6;i++)
			if(cubes[i].getType() == type.AIR) {
				render[i] = false;
			}
	}
	
	public CubeTest1(type t) {
		this.kind = t;
		for(int i=0;i<6;i++)render[i]=true;
	}
	public CubeTest1(){for(int i=0;i<6;i++)render[i]=true;}
	
	public type getType() {
		return this.kind;
	}

	public void render() {
		if(this.kind == type.AIR)
			return;

    	glBegin(GL_QUADS);
        glColor4f(1, 1, 0, 1f);
        //front
        if(render[0])
        {
        glVertex3f(1, 0, -4);
        glVertex3f(2, 0, -4);
        glVertex3f(2, 1, -4);
        glVertex3f(1, 1, -4);
        }
        //left
        if(render[1])
        {
        glVertex3f(1, 0, -4);
        glVertex3f(1, 0, -5);
        glVertex3f(1, 1, -5);
        glVertex3f(1, 1, -4);
        }
        //back
        if(render[2])
        {
        glVertex3f(1, 0, -5);
        glVertex3f(2, 0, -5);
        glVertex3f(2, 1, -5);
        glVertex3f(1, 1, -5);
        }
        //right
        if(render[3])
        {
        glVertex3f(2, 0, -5);
        glVertex3f(2, 0, -4);
        glVertex3f(2, 1, -4);
        glVertex3f(2, 1, -5);
        }
        //top
        if(render[4])
        {
        glVertex3f(1, 1, -5);
        glVertex3f(2, 1, -5);
        glVertex3f(2, 1, -4);
        glVertex3f(1, 1, -4);
	}
        //bottom
        if(render[5])
        {
        glVertex3f(1, 0, -5);
        glVertex3f(2, 0, -5);
        glVertex3f(2, 0, -4);
        glVertex3f(1, 0, -4);
        }
        glEnd();
	}
}

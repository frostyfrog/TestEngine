package com.Colton.game2.entities;

import static org.lwjgl.opengl.GL11.*;

public class Block {
	
	int list = glGenLists(1);
	private int x, y, z, size;
	private Boolean wire = true;
	
	private void init() {
		glNewList(list, GL_COMPILE);
		glColor3f(0.5f, 0.5f, 0.5f);
		glBegin(GL_QUADS);
			glColor3f(1f, 0.5f, 0.5f);
			glVertex3f(x,      y,      z);
			glVertex3f(x-size, y,      z);
			glVertex3f(x-size, y-size, z);
			glVertex3f(x,      y-size, z);

			glColor3f(0.5f, 1f, 0.5f);
			glVertex3f(x,      y,      z-size);
			glVertex3f(x-size, y,      z-size);
			glVertex3f(x-size, y,      z);
			glVertex3f(x,      y,      z);

			glColor3f(0.5f, 0.5f, 1f);
			glVertex3f(x,      y-size, z-size);
			glVertex3f(x-size, y-size, z-size);
			glVertex3f(x-size, y,      z-size);
			glVertex3f(x,      y,      z-size);

			glColor3f(0.5f, 0f, 0.5f);
			glVertex3f(x, y-size, z);
			glVertex3f(x, y-size, z-size);
			glVertex3f(x, y,      z-size);
			glVertex3f(x, y,      z);
			
			glColor3f(0.5f, 0.5f, 0f);
			glVertex3f(x-size, y,      z);
			glVertex3f(x-size, y,      z-size);
			glVertex3f(x-size, y-size, z-size);
			glVertex3f(x-size, y-size, z);
			
			glColor3f(0f, 0.5f, 0.5f);
			glVertex3f(x,      y-size,      z);
			glVertex3f(x-size, y-size,      z);
			glVertex3f(x-size, y-size,      z-size);
			glVertex3f(x,      y-size,      z-size);
		glEnd();
		glEndList();
		
	}
	
	public Block(int x, int y, int z, int size)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.size = size;
		init();
	}
	
	public void reset() {
		glDeleteLists(list, 1);
		init();
	}
	
	public void setWireframe(Boolean b) {
		wire = b;
	}
	
	public Boolean isWireframe() {
		return wire;
	}
	
	public void render() {
    	if(wire)glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
		glCallList(list);
    	if(wire)glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
	}

}

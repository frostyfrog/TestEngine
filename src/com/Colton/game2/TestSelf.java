package com.Colton.game2;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.*;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.util.vector.Vector3f;

import com.Colton.game2.entities.*;

public class TestSelf {
	
	public Vector3f rot = new Vector3f(0,0,0);
	public Vector3f pos = new Vector3f(0,-1,0);
	public float mouseSpeed = 2;
	private boolean mouseCS = false, mouseOS = false;
	private Camera camera;
	public Debug debug; 

	private static FloatBuffer perspectiveProjectionMatrix = reserveData(16);
	private static FloatBuffer orthographicProjectionMatrix = reserveData(16);

	public void start() {
		try {
			Display.setDisplayMode(new DisplayMode(800,600));
			Display.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
			System.exit(1);
		}
		printSupported();
		
		init();
		Block b1 = new Block(1,0,-5,1);
		Block b2 = new Block(1,0,-6,1);
		Block b3 = new Block(2,0,-6,1);
		Block b4 = new Block(2,0,-5,1);
		Block b5 = new Block(-3,0,-5,2);
		
		while (!Display.isCloseRequested()) {
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			glLoadIdentity();
			glRotatef(rot.y, 1,0,0);
			glRotatef(rot.x, 0,1,0);
			glRotatef(rot.z, 0,0,1);
			glTranslatef(pos.x, pos.y, pos.z);
            glEnable(GL_DEPTH_TEST);
        	glEnable(GL_CULL_FACE);
			b1.render();
			b2.render();
			b3.render();
			b4.render();
			b5.render();
			
			//rot.set(0, 0, 0);
			if(getRMB())
			{
				b1.reset();
				b1.setWireframe(!b1.isWireframe());
				b2.reset();
				b2.setWireframe(!b2.isWireframe());
				b3.reset();
				b3.setWireframe(!b3.isWireframe());
				b4.reset();
				b4.setWireframe(!b4.isWireframe());
				b5.reset();
				b5.setWireframe(!b5.isWireframe());
			}
			camera.tick(this);
			//System.out.println(pos.x + " - " + pos.y);
            while (Keyboard.next()) {
                if (Keyboard.isKeyDown(Keyboard.KEY_C)) {
                    pos = new Vector3f(0, 0, 0);
                    rot = new Vector3f(0, 0, 0);
                }
            }

    		glMatrixMode(GL_PROJECTION);
    		glLoadMatrix(orthographicProjectionMatrix);
    		glMatrixMode(GL_MODELVIEW);
    		glPushMatrix();
    		glLoadIdentity();
            debug.render();
    		//glEnable(GL_LIGHTING);
    		glPopMatrix();
    		glMatrixMode(GL_PROJECTION);
    		glLoadMatrix(perspectiveProjectionMatrix);
    		glMatrixMode(GL_MODELVIEW);
            
			Display.update();
		}
		Display.destroy();
	}

	private void printSupported() {
		System.out.print("OpenGL11 supported? ");
		System.out.println(GLContext.getCapabilities().OpenGL11);
		System.out.print("OpenGL12 supported? ");
		System.out.println(GLContext.getCapabilities().OpenGL12);
		System.out.print("OpenGL13 supported? ");
		System.out.println(GLContext.getCapabilities().OpenGL13);
		System.out.print("OpenGL14 supported? ");
		System.out.println(GLContext.getCapabilities().OpenGL14);
		System.out.print("OpenGL15 supported? ");
		System.out.println(GLContext.getCapabilities().OpenGL15);
		System.out.print("OpenGL20 supported? ");
		System.out.println(GLContext.getCapabilities().OpenGL20);
		System.out.print("OpenGL21 supported? ");
		System.out.println(GLContext.getCapabilities().OpenGL21);
		System.out.print("OpenGL30 supported? ");
		System.out.println(GLContext.getCapabilities().OpenGL30);
		System.out.print("OpenGL31 supported? ");
		System.out.println(GLContext.getCapabilities().OpenGL31);
		System.out.print("OpenGL32 supported? ");
		System.out.println(GLContext.getCapabilities().OpenGL32);
		System.out.print("OpenGL33 supported? ");
		System.out.println(GLContext.getCapabilities().OpenGL33);
		System.out.print("OpenGL40 supported? ");
		System.out.println(GLContext.getCapabilities().OpenGL40);
		System.out.print("OpenGL41 supported? ");
		System.out.println(GLContext.getCapabilities().OpenGL41);
		System.out.print("OpenGL42 supported? ");
		System.out.println(GLContext.getCapabilities().OpenGL42);
	}

	private static FloatBuffer reserveData(int size) {
		FloatBuffer data = BufferUtils.createFloatBuffer(size);
		return data;
	}

	private void init() {
		debug = new Debug();
		camera = new Camera();
		
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		//glOrtho(0, 800, 0, 600, 1, -1);
		gluPerspective(68, (float) Display.getWidth() / (float) Display.getHeight(), 0.3f, 20f);
		glMatrixMode(GL_MODELVIEW);
		glGetFloat(GL_PROJECTION_MATRIX, perspectiveProjectionMatrix);
		
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0, Display.getWidth(), Display.getHeight(), 0, 1, -1);
		glGetFloat(GL_PROJECTION_MATRIX, orthographicProjectionMatrix);
		
		glLoadMatrix(perspectiveProjectionMatrix);
		glMatrixMode(GL_MODELVIEW);
		
		glEnable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glEnable(GL_ALPHA_TEST);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
		glEnable(GL_CULL_FACE);
		glCullFace(GL_BACK);
	}

	private boolean getRMB() {
		boolean button = Mouse.isButtonDown(1);
		mouseCS = mouseOS;
		mouseOS = button;
		return mouseCS == false && mouseOS;
	}
	
	public static void main(String[] args) {
		TestSelf self = new TestSelf();
		self.start();
	}

}

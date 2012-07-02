package com.Colton.game;

import java.awt.Color;
import java.nio.FloatBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.opengl.GLContext;
import static org.lwjgl.util.glu.GLU.gluPerspective;
import org.lwjgl.util.vector.Vector3f;

public class Test1 {
	public static volatile boolean running = true;
	
	public static final boolean resizable = true;
	public static final boolean fullscreen = false;
	public static final boolean vsync = true;
	public static final int maxLookUp = 85;
	public static final int maxLookDown = -85;
	
	public static Vector3f position = new Vector3f(0, 0, 0);
	public static Vector3f rotation = new Vector3f(0, 0, 0);
	public static float zNear = 0.3f;
	public static float zFar = 20f;
	public static float fogNear = 9f;
	public static float fogFar = 13f;
	public static Color fogColor = Color.BLUE;
	public static int walkingSpeed = 10;
	public static int mouseSpeed = 2;
	public static boolean printFPS = true;
	public static int fov = 68;
	
	private static int fps;
	private static long lastFPS;
	private static long lastFrame;
	
	private static boolean test1 = false;
	
	private static long getTime() {
		return (Sys.getTime() *1000 / Sys.getTimerResolution());
	}
	
	private static int getDelta() {
		long currentTime = getTime();
		int delta = (int) (currentTime - lastFrame);
		lastFrame = getTime();
		return delta;
	}
	
	public static void updateFPS() {
		if(getTime() - lastFPS > 1000) {
			if(printFPS) 
				System.out.println("FPS: "+ fps);
			fps = 0;
			lastFPS += 1000;
		}
		fps++;
	}
	
	public static void main(String[] args) {
		try {
			if(fullscreen)
				Display.setDisplayModeAndFullscreen(Display.getDesktopDisplayMode());
			else {
				Display.setResizable(resizable);
				Display.setDisplayMode(new DisplayMode(800, 600));
			}
			Display.setTitle("Test #1");
			Display.setVSyncEnabled(vsync);
			Display.create();
		} catch (LWJGLException e) {
			System.err.println("Display Innitialization failed.");
			System.exit(1);
		}
		
		if(fullscreen)
			Mouse.setGrabbed(true);
		else
			Mouse.setGrabbed(false);
		
		if (!GLContext.getCapabilities().OpenGL11) {
			System.err.println("Your OpenGL version doesn't support the required functionality.");
			Display.destroy();
			System.exit(1);
		}
		
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		gluPerspective(fov, (float) Display.getWidth() / (float) Display.getHeight(), zNear, zFar);
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
		
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_TEXTURE_2D);
		glEnable(GL_BLEND);
		glEnable(GL_ALPHA_TEST);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
		glEnable(GL_CULL_FACE);
		glCullFace(GL_BACK);
		glEnable(GL_FOG);
		
		{
			FloatBuffer fogColors = BufferUtils.createFloatBuffer(4);
			fogColors.put(fogColor.getRGBComponents(null));
			glClearColor(fogColor.getRed(), fogColor.getGreen(), fogColor.getBlue(), fogColor.getAlpha());
			fogColors.flip();
			glFog(GL_FOG_COLOR, fogColors);
			glFogi(GL_FOG_MODE, GL_LINEAR);
			glHint(GL_FOG_HINT, GL_NICEST);
			glFogf(GL_FOG_START, fogNear);
			glFogf(GL_FOG_END, fogFar);
			glFogf(GL_FOG_DENSITY, 0.005f);
		}
		

		int floorHeight = -1;
		int gridSize = 10;
		float tileSize = 0.20f;
		int ceilingHeight = 10;
        int wallDisplayList = glGenLists(1);
        glNewList(wallDisplayList, GL_COMPILE);

        glBegin(GL_QUADS);

        // North wall

        glTexCoord2f(0, 0);
        glVertex3f(-gridSize, floorHeight, -gridSize);
        glTexCoord2f(0, gridSize * 10 * tileSize);
        glVertex3f(gridSize, floorHeight, -gridSize);
        glTexCoord2f(gridSize * 10 * tileSize, gridSize * 10 * tileSize);
        glVertex3f(gridSize, ceilingHeight, -gridSize);
        glTexCoord2f(gridSize * 10 * tileSize, 0);
        glVertex3f(-gridSize, ceilingHeight, -gridSize);

        // West wall

        glTexCoord2f(0, 0);
        glVertex3f(-gridSize, floorHeight, -gridSize);
        glTexCoord2f(gridSize * 10 * tileSize, 0);
        glVertex3f(-gridSize, ceilingHeight, -gridSize);
        glTexCoord2f(gridSize * 10 * tileSize, gridSize * 10 * tileSize);
        glVertex3f(-gridSize, ceilingHeight, +gridSize);
        glTexCoord2f(0, gridSize * 10 * tileSize);
        glVertex3f(-gridSize, floorHeight, +gridSize);

        // East wall

        glTexCoord2f(0, 0);
        glVertex3f(+gridSize, floorHeight, -gridSize);
        glTexCoord2f(gridSize * 10 * tileSize, 0);
        glVertex3f(+gridSize, floorHeight, +gridSize);
        glTexCoord2f(gridSize * 10 * tileSize, gridSize * 10 * tileSize);
        glVertex3f(+gridSize, ceilingHeight, +gridSize);
        glTexCoord2f(0, gridSize * 10 * tileSize);
        glVertex3f(+gridSize, ceilingHeight, -gridSize);

        // South wall

        glTexCoord2f(0, 0);
        glVertex3f(-gridSize, floorHeight, +gridSize);
        glTexCoord2f(gridSize * 10 * tileSize, 0);
        glVertex3f(-gridSize, ceilingHeight, +gridSize);
        glTexCoord2f(gridSize * 10 * tileSize, gridSize * 10 * tileSize);
        glVertex3f(+gridSize, ceilingHeight, +gridSize);
        glTexCoord2f(0, gridSize * 10 * tileSize);
        glVertex3f(+gridSize, floorHeight, +gridSize);

        glEnd();

        glEndList();

        int floorDisplayList = glGenLists(1);
        glNewList(floorDisplayList, GL_COMPILE);
        glBegin(GL_QUADS);
        glTexCoord2f(0, 0);
        glVertex3f(-gridSize, floorHeight, -gridSize);
        glTexCoord2f(0, gridSize * 10 * tileSize);
        glVertex3f(-gridSize, floorHeight, gridSize);
        glTexCoord2f(gridSize * 10 * tileSize, gridSize * 10 * tileSize);
        glVertex3f(gridSize, floorHeight, gridSize);
        glTexCoord2f(gridSize * 10 * tileSize, 0);
        glVertex3f(gridSize, floorHeight, -gridSize);
        
        glEnd();
        glEndList();
        
        int objectDisplayList = glGenLists(1);
        glNewList(objectDisplayList, GL_COMPILE);
        {
        	int[] b = new int[]{0,0,0,0,
        					    0,1,1,0,
        					    0,1,0,0,
        					    0,0,1,0,
        					    0,1,1,0,
        					    0,0,0,0};
        	CubeTest1[] blocks = new CubeTest1[b.length];
        	for(int i=0; i < 1; i++)
        	{
        		for(int i2 = 0; i2 < b.length; i2++)
        		{
        			if(b[i2] == 0)
        				blocks[i2] = new CubeTest1(CubeTest1.type.AIR);
        			else
        				blocks[i2] = new CubeTest1();
        		}
        		for(int i2 = 0; i2 < b.length; i2++)
        		{
        			blocks[i2].render();
        		}
        	}/**/
            double topPoint = 2.75;
            glBegin(GL_TRIANGLES);
            glColor4f(1, 1, 0, 1f);
            glVertex3d(0, topPoint, -5);
            glColor4f(0, 0, 1, 1f);
            glVertex3d(-1, -0.75, -4);
            glColor4f(0, 0, 1, 1f);
            glVertex3d(1, -.75, -4);

            glColor4f(1, 1, 0, 1f);
            glVertex3d(0, topPoint, -5);
            glColor4f(0, 0, 1, 1f);
            glVertex3d(1, -0.75, -4);
            glColor4f(0, 0, 1, 1f);
            glVertex3d(1, -0.75, -6);

            glColor4f(1, 1, 0, 1f);
            glVertex3d(0, topPoint, -5);
            glColor4f(0, 0, 1, 1f);
            glVertex3d(1, -0.75, -6);
            glColor4f(0, 0, 1, 1f);
            glVertex3d(-1, -.75, -6);
            
            glColor4f(1, 1, 0, 1f);
            glVertex3d(0, topPoint, -5);
            glColor4f(0, 0, 1, 1f);
            glVertex3d(-1, -0.75, -6);
            glColor4f(0, 0, 1, 1f);
            glVertex3d(-1, -.75, -4);
            
            glEnd();
            glColor4f(1, 1, 1, 1);
        }
        glEndList();
        
        getDelta();
        lastFPS = getTime();
        
        while(running) {
        	glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        	
        	int delta = getDelta();
        	
        	glEnable(GL_CULL_FACE);
        	glDisable(GL_DEPTH_TEST);
        	glCallList(floorDisplayList);
        	glCallList(wallDisplayList);
        	glEnable(GL_DEPTH_TEST);
        	glDisable(GL_CULL_FACE);
        	glBindTexture(GL_TEXTURE_2D, 0);
        	glCallList(objectDisplayList);
        	
        	glLoadIdentity();
        	glRotatef(rotation.x, 1, 0, 0);
        	glRotatef(rotation.y, 0, 1, 0);
        	glRotatef(rotation.z, 0, 0, 1);
        	glTranslatef(position.x, position.y, position.z);
        	// ... SNIP ...
        	float sppeed = (walkingSpeed * 0.0002f) * delta;
        	if(fps == 1)
        		test1 = !test1;
        	if(!test1)
        		position.x += sppeed;
        	else position.x -= sppeed;
        	// ... SNIP ...
        	if(printFPS)
        		updateFPS();
        	Display.update();
        	if (vsync)
        		Display.sync(60);
        	if(Display.isCloseRequested())
        		running = false;
        }
        glDeleteLists(floorDisplayList, 1);
        glDeleteLists(wallDisplayList, 1);
        glDeleteLists(objectDisplayList, 1);
        Display.destroy();
        System.exit(0);
	}

}

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

/**
* A LWJGL port of the awesome MineFront Pre-ALPHA 0.02 Controls: W/UP =
* forward; A/LEFT = strafe left; D/RIGHT = strafe right; S/DOWN = backward;
* SPACE = fly up; SHIFT = fly down; CONTROL = move faster; TAB = move slower; Q
* = increase walking speed; Z = decrease walking speed; O = increase mouse
* speed; L = decrease mouse speed; C = reset position
*
* @author Oskar Veerhoek, Yan Chernikov
*/
public class MineFront {
    public static final boolean resizable = true;
    public static volatile boolean running = true;
    public static Vector3f position = new Vector3f(0, 0, 0);
    public static Vector3f rotation = new Vector3f(0, 0, 0);
    public static float zNear = 0.3f;
    public static final int gridSize = 10;
    public static final float tileSize = 0.20f;
    public static float zFar = 20f;
    public static float fogNear = 9f;
    public static float fogFar = 13f;
    public static Color fogColor = Color.BLUE;
    public static final boolean fullscreen = false;
//* Defines the walking speed, where 10 is the standard.
    public static int walkingSpeed = 10;
    public static int mouseSpeed = 2;
    public static final boolean vsync = true;
    public static boolean printFPS = false;
    public static final int maxLookUp = 85;
    public static final int maxLookDown = -85;
    public static final float ceilingHeight = 10;
    public static final float floorHeight = -1;
    public static int fov = 68;
    
    private static int fps;
    private static long lastFPS;
    private static long lastFrame;

    private static long getTime() {
        return (Sys.getTime() * 1000) / Sys.getTimerResolution();
    }

    private static int getDelta() {
        long currentTime = getTime();
        int delta = (int) (currentTime - lastFrame);
        lastFrame = getTime();
        return delta;
    }

    public static void updateFPS() {
        if (getTime() - lastFPS > 1000) {
            if (printFPS) {
                System.out.println("FPS: " + fps);
            }
            fps = 0;
            lastFPS += 1000;
        }
        fps++;
    }

    public static void main(String[] args) {
        try {
            if (fullscreen) {
                Display.setDisplayModeAndFullscreen(Display.getDesktopDisplayMode());
            } else {
                Display.setResizable(resizable);
                Display.setDisplayMode(new DisplayMode(800, 600));
            }
            Display.setTitle("Minefront Pre-Alpha 0.02 LWJGL Port");
            Display.setVSyncEnabled(vsync);
            Display.create();
        } catch (LWJGLException ex) {
            System.err.println("Display initialization failed.");
            System.exit(1);
        }

        if (fullscreen) {
            Mouse.setGrabbed(true);
        } else {
            Mouse.setGrabbed(false);
        }

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
            FloatBuffer fogColours = BufferUtils.createFloatBuffer(
                    4);
            fogColours.put(new float[]{fogColor.getRed(), fogColor.getGreen(), fogColor.getBlue(), fogColor.getAlpha()});
            glClearColor(fogColor.getRed(), fogColor.getGreen(), fogColor.getBlue(), fogColor.getAlpha());
            fogColours.flip();
            glFog(GL_FOG_COLOR, fogColours);
            glFogi(GL_FOG_MODE, GL_LINEAR);
            glHint(GL_FOG_HINT, GL_NICEST);
            glFogf(GL_FOG_START, fogNear);
            glFogf(GL_FOG_END, fogFar);
            glFogf(GL_FOG_DENSITY, 0.005f);
        }

        int ceilingDisplayList = glGenLists(1);
        glNewList(ceilingDisplayList, GL_COMPILE);
        glBegin(GL_QUADS);
        glTexCoord2f(0, 0);
        glVertex3f(-gridSize, ceilingHeight, -gridSize);
        glTexCoord2f(gridSize * 10 * tileSize, 0);
        glVertex3f(gridSize, ceilingHeight, -gridSize);
        glTexCoord2f(gridSize * 10 * tileSize, gridSize * 10 * tileSize);
        glVertex3f(gridSize, ceilingHeight, gridSize);
        glTexCoord2f(0, gridSize * 10 * tileSize);
        glVertex3f(-gridSize, ceilingHeight, gridSize);
        glEnd();
        glEndList();

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
        	glBegin(GL_QUADS);
            glColor4f(1, 1, 0, 1f);
            //front
            glVertex3f(1, 0, -4);
            glVertex3f(2, 0, -4);
            glVertex3f(2, 1, -4);
            glVertex3f(1, 1, -4);
            //left
            glVertex3f(1, 0, -4);
            glVertex3f(1, 0, -5);
            glVertex3f(1, 1, -5);
            glVertex3f(1, 1, -4);
            //back
            glVertex3f(1, 0, -5);
            glVertex3f(2, 0, -5);
            glVertex3f(2, 1, -5);
            glVertex3f(1, 1, -5);
            //right
            glVertex3f(2, 0, -5);
            glVertex3f(2, 0, -4);
            glVertex3f(2, 1, -4);
            glVertex3f(2, 1, -5);
            //top
            glVertex3f(1, 1, -5);
            glVertex3f(2, 1, -5);
            glVertex3f(2, 1, -4);
            glVertex3f(1, 1, -4);
            //bottom
            glVertex3f(1, 0, -5);
            glVertex3f(2, 0, -5);
            glVertex3f(2, 0, -4);
            glVertex3f(1, 0, -4);/**/

            glColor4f(1, 0, 1, 1f);
            //front
            glVertex3f(1, 0, -5);
            glVertex3f(2, 0, -5);
            glVertex3f(2, 1, -5);
            glVertex3f(1, 1, -5);
            //left
            glVertex3f(1, 0, -5);
            glVertex3f(1, 0, -6);
            glVertex3f(1, 1, -6);
            glVertex3f(1, 1, -5);
            //back
            glVertex3f(1, 0, -6);
            glVertex3f(2, 0, -6);
            glVertex3f(2, 1, -6);
            glVertex3f(1, 1, -6);
            //right
            glVertex3f(2, 0, -6);
            glVertex3f(2, 0, -5);
            glVertex3f(2, 1, -5);
            glVertex3f(2, 1, -6);/**/
            //top
            glVertex3f(1, 1, -6);
            glVertex3f(2, 1, -6);
            glVertex3f(2, 1, -5);
            glVertex3f(1, 1, -5);
            //bottom
            glVertex3f(1, 0, -6);
            glVertex3f(2, 0, -6);
            glVertex3f(2, 0, -5);
            glVertex3f(1, 0, -5);
            glEnd();
            double topPoint = 0.75;
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
            // Here below if the dull old triangle, if you so desire.
// glBegin(GL_TRIANGLES);
// glColor4f(1, 0, 0, 0.9f);
// glVertex3f(-0.75f, -0.75f, -5);
// glColor4f(0, 1, 0, 0.9f);
// glVertex3f(0.75f, -0.75f, -5);
// glColor4f(0, 0, 1, 0.9f);
// glVertex3f(0.75f, 0.75f, -5.5f);
// glEnd();
            glColor4f(1, 1, 1, 1);
        }
        glEndList();

        getDelta();
        lastFPS = getTime();

        while (running) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            int delta = getDelta();

            glEnable(GL_CULL_FACE);
            glDisable(GL_DEPTH_TEST);
            glCallList(floorDisplayList);
            glCallList(ceilingDisplayList);
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

            if (Mouse.isGrabbed()) {
                float mouseDX = Mouse.getDX() * mouseSpeed * 0.16f;
                float mouseDY = Mouse.getDY() * mouseSpeed * 0.16f;
                if (rotation.y + mouseDX >= 360) {
                    rotation.y = rotation.y + mouseDX - 360;
                } else if (rotation.y + mouseDX < 0) {
                    rotation.y = 360 - rotation.y + mouseDX;
                } else {
                    rotation.y += mouseDX;
                }
                if (rotation.x - mouseDY >= maxLookDown && rotation.x - mouseDY <= maxLookUp) {
                    rotation.x += -mouseDY;
                } else if (rotation.x - mouseDY < maxLookDown) {
                    rotation.x = maxLookDown;
                } else if (rotation.x - mouseDY > maxLookUp) {
                    rotation.x = maxLookUp;
                }
            }

            boolean keyUp = Keyboard.isKeyDown(Keyboard.KEY_UP) || Keyboard.isKeyDown(Keyboard.KEY_W);
            boolean keyDown = Keyboard.isKeyDown(Keyboard.KEY_DOWN) || Keyboard.isKeyDown(Keyboard.KEY_S);
            boolean keyLeft = Keyboard.isKeyDown(Keyboard.KEY_LEFT) || Keyboard.isKeyDown(Keyboard.KEY_A);
            boolean keyRight = Keyboard.isKeyDown(Keyboard.KEY_RIGHT) || Keyboard.isKeyDown(Keyboard.KEY_D);
            boolean flyUp = Keyboard.isKeyDown(Keyboard.KEY_SPACE);
            boolean flyDown = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT);
            boolean moveFaster = Keyboard.isKeyDown(Keyboard.KEY_LCONTROL);
            boolean moveSlower = Keyboard.isKeyDown(Keyboard.KEY_TAB);

            if (moveFaster && !moveSlower) {
                walkingSpeed *= 4f;
            }
            if (moveSlower && !moveFaster) {
                walkingSpeed /= 10f;
            }

            if (keyUp && keyRight && !keyLeft && !keyDown) {
                float angle = rotation.y + 45;
                Vector3f newPosition = new Vector3f(position);
                float schuine = (walkingSpeed * 0.0002f) * delta;
                float aanliggende = schuine * (float) Math.cos(Math.toRadians(angle));
                float overstaande = (float) (Math.sin(Math.toRadians(angle)) * schuine);
                newPosition.z += aanliggende;
                newPosition.x -= overstaande;
                position.z = newPosition.z;
                position.x = newPosition.x;
            }
            if (keyUp && keyLeft && !keyRight && !keyDown) {
                float angle = rotation.y - 45;
                Vector3f newPosition = new Vector3f(position);
                float schuine = (walkingSpeed * 0.0002f) * delta;
                float aanliggende = schuine * (float) Math.cos(Math.toRadians(angle));
                float overstaande = (float) (Math.sin(Math.toRadians(angle)) * schuine);
                newPosition.z += aanliggende;
                newPosition.x -= overstaande;
                position.z = newPosition.z;
                position.x = newPosition.x;
            }
            if (keyUp && !keyLeft && !keyRight && !keyDown) {
                float angle = rotation.y;
                Vector3f newPosition = new Vector3f(position);
                float schuine = (walkingSpeed * 0.0002f) * delta;
                float aanliggende = schuine * (float) Math.cos(Math.toRadians(angle));
                float overstaande = (float) (Math.sin(Math.toRadians(angle)) * schuine);
                newPosition.z += aanliggende;
                newPosition.x -= overstaande;
                position.z = newPosition.z;
                position.x = newPosition.x;
            }
            if (keyDown && keyLeft && !keyRight && !keyUp) {
                float angle = rotation.y - 135;
                Vector3f newPosition = new Vector3f(position);
                float schuine = (walkingSpeed * 0.0002f) * delta;
                float aanliggende = schuine * (float) Math.cos(Math.toRadians(angle));
                float overstaande = (float) (Math.sin(Math.toRadians(angle)) * schuine);
                newPosition.z += aanliggende;
                newPosition.x -= overstaande;
                position.z = newPosition.z;
                position.x = newPosition.x;
            }
            if (keyDown && keyRight && !keyLeft && !keyUp) {
                float angle = rotation.y + 135;
                Vector3f newPosition = new Vector3f(position);
                float schuine = (walkingSpeed * 0.0002f) * delta;
                float aanliggende = schuine * (float) Math.cos(Math.toRadians(angle));
                float overstaande = (float) (Math.sin(Math.toRadians(angle)) * schuine);
                newPosition.z += aanliggende;
                newPosition.x -= overstaande;
                position.z = newPosition.z;
                position.x = newPosition.x;
            }
            if (keyDown && !keyUp && !keyLeft && !keyRight) {
                float angle = rotation.y;
                Vector3f newPosition = new Vector3f(position);
                float schuine = -(walkingSpeed * 0.0002f) * delta;
                float aanliggende = schuine * (float) Math.cos(Math.toRadians(angle));
                float overstaande = (float) (Math.sin(Math.toRadians(angle)) * schuine);
                newPosition.z += aanliggende;
                newPosition.x -= overstaande;
                position.z = newPosition.z;
                position.x = newPosition.x;
            }
            if (keyLeft && !keyRight && !keyUp && !keyDown) {
                float angle = rotation.y - 90;
                Vector3f newPosition = new Vector3f(position);
                float schuine = (walkingSpeed * 0.0002f) * delta;
                float aanliggende = schuine * (float) Math.cos(Math.toRadians(angle));
                float overstaande = (float) (Math.sin(Math.toRadians(angle)) * schuine);
                newPosition.z += aanliggende;
                newPosition.x -= overstaande;
                position.z = newPosition.z;
                position.x = newPosition.x;
            }
            if (keyRight && !keyLeft && !keyUp && !keyDown) {
                float angle = rotation.y + 90;
                Vector3f newPosition = new Vector3f(position);
                float schuine = (walkingSpeed * 0.0002f) * delta;
                float aanliggende = schuine * (float) Math.cos(Math.toRadians(angle));
                float overstaande = (float) (Math.sin(Math.toRadians(angle)) * schuine);
                newPosition.z += aanliggende;
                newPosition.x -= overstaande;
                position.z = newPosition.z;
                position.x = newPosition.x;
            }
            if (flyUp && !flyDown) {
                double newPositionY = (walkingSpeed * 0.0002) * delta;
                position.y -= newPositionY;
            }
            if (flyDown && !flyUp) {
                double newPositionY = (walkingSpeed * 0.0002) * delta;
                position.y += newPositionY;
            }
            if (moveFaster && !moveSlower) {
                walkingSpeed /= 4f;
            }
            if (moveSlower && !moveFaster) {
                walkingSpeed *= 10f;
            }
            while (Mouse.next()) {
                if (Mouse.isButtonDown(0)) {
                    Mouse.setGrabbed(true);
                }
                if (Mouse.isButtonDown(1)) {
                    Mouse.setGrabbed(false);
                }

            }
            while (Keyboard.next()) {
                if (Keyboard.isKeyDown(Keyboard.KEY_C)) {
                    position = new Vector3f(0, 0, 0);
                    rotation = new Vector3f(0, 0, 0);
                }
                if (Keyboard.isKeyDown(Keyboard.KEY_O)) {
                    mouseSpeed += 1;
                    System.out.println("Mouse speed changed to " + mouseSpeed + ".");
                }
                if (Keyboard.isKeyDown(Keyboard.KEY_L)) {
                    if (mouseSpeed - 1 > 0) {
                        mouseSpeed -= 1;
                        System.out.println("Mouse speed changed to " + mouseSpeed + ".");
                    }
                }
                if (Keyboard.isKeyDown(Keyboard.KEY_Q)) {
                    System.out.println("Walking speed changed to " + walkingSpeed + ".");
                    walkingSpeed += 1;
                }
                if (Keyboard.isKeyDown(Keyboard.KEY_Z)) {
                    System.out.println("Walking speed changed to " + walkingSpeed + ".");
                    walkingSpeed -= 1;
                }
                if (Keyboard.isKeyDown(Keyboard.KEY_F11)) {
                    try {
                        Display.setFullscreen(!Display.isFullscreen());
                        if (!Display.isFullscreen()) {
                            Display.setResizable(resizable);
                            Display.setDisplayMode(new DisplayMode(800, 600));
                            glViewport(0, 0, Display.getWidth(), Display.getHeight());
                            glMatrixMode(GL_PROJECTION);
                            glLoadIdentity();
                            gluPerspective(fov, (float) Display.getWidth() / (float) Display.getHeight(), zNear, zFar);
                            glMatrixMode(GL_MODELVIEW);
                            glLoadIdentity();
                        } else {
                            glViewport(0, 0, Display.getWidth(), Display.getHeight());
                            glMatrixMode(GL_PROJECTION);
                            glLoadIdentity();
                            gluPerspective(fov, (float) Display.getWidth() / (float) Display.getHeight(), zNear, zFar);
                            glMatrixMode(GL_MODELVIEW);
                            glLoadIdentity();
                        }
                    } catch (LWJGLException ex) {
                        Logger.getLogger(MineFront.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
                    if (!Mouse.isGrabbed() || Display.isFullscreen()) {
                        running = false;
                    } else {
                        Mouse.setGrabbed(false);
                    }
                }
            }
            if (resizable) {
                if (Display.wasResized()) {
                    glViewport(0, 0, Display.getWidth(), Display.getHeight());
                    glMatrixMode(GL_PROJECTION);
                    glLoadIdentity();
                    gluPerspective(fov, (float) Display.getWidth() / (float) Display.getHeight(), zNear, zFar);
                    glMatrixMode(GL_MODELVIEW);
                    glLoadIdentity();
                }
            }
            if (printFPS) {
                updateFPS();
            }
            Display.update();
            if (vsync) {
                Display.sync(60);
            }
            if (Display.isCloseRequested()) {
                running = false;
            }
        }
        glDeleteLists(floorDisplayList, 1);
        glDeleteLists(ceilingDisplayList, 1);
        glDeleteLists(wallDisplayList, 1);
        glDeleteLists(objectDisplayList, 1);
        Display.destroy();
        System.exit(0);
    }
}
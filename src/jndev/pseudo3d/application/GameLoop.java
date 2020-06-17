package jndev.pseudo3d.application;

import jndev.pseudo3d.renderer.Renderer;
import jndev.pseudo3d.scene.Scene;

import javax.swing.*;
import java.awt.*;

/**
 * game loop to update graphics and physics for the active scene
 */
public class GameLoop extends JPanel {
    
    /**
     * active scene to render and tick
     */
    private static Scene activeScene;
    
    /**
     * framerate at which the graphics updates
     */
    private static int graphicsFPS;
    
    /**
     * framerate at which the physics updates
     */
    private static int physicsFPS;
    
    /**
     * whether the game is paused or not
     */
    private static boolean paused;
    
    /**
     * time in milliseconds that the loop started
     */
    private long startTime;
    
    /**
     * create a new game loop and initialize values
     */
    GameLoop() {
        activeScene = null;
        graphicsFPS = 60;
        physicsFPS = 120;
        paused = true;
        startTime = System.currentTimeMillis();
        doLoop();
        setVisible(true);
        requestFocus();
    }
    
    /**
     * main game loop
     */
    private void doLoop() {
        Thread thread = new Thread(() -> {
            long prev = 0;
            while (true) {
                if (!paused) {
                    long time = System.currentTimeMillis() - startTime;
                    long graphicsTime = 1000 / graphicsFPS;
                    long physicsTime = 1000 / physicsFPS;
                    if (time != prev) {
                        if (time % graphicsTime == 0) updateGraphics();
                        if (time % physicsTime == 0) updatePhysics();
                    }
                    prev = time;
                }
            }
        });
        thread.start();
        thread.setName("Game Loop");
    }
    
    /**
     * start the loop
     */
    public static void start() {
        paused = false;
    }
    
    /**
     * pause the loop
     */
    public static void stop() {
        paused = true;
    }
    
    /**
     * set the FPS for graphics refreshing
     *
     * @param fps frames per second
     */
    public static void setGraphicsFPS(int fps) {
        graphicsFPS = Math.min(fps, physicsFPS);
    }
    
    /**
     * set the FPS for physics ticking
     *
     * @param fps frames per second
     */
    public static void setPhysicsFPS(int fps) {
        physicsFPS = Math.max(fps, graphicsFPS);
    }
    
    /**
     * tick physics once
     */
    private void updatePhysics() {
        activeScene.tick();
    }
    
    /**
     * cause a single frame to render
     */
    private void updateGraphics() {
        repaint();
        Toolkit.getDefaultToolkit().sync();
    }
    
    public static void setActiveScene(Scene scene) {
        activeScene = scene;
    }
    
    /**
     * render the active scene
     *
     * @param g
     */
    @Override
    public void paintComponent(Graphics g) {
        if (activeScene != null) Renderer.render(activeScene, this, g);
    }
    
}

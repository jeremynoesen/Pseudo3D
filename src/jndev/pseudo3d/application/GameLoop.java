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
    private static Scene activeScene = null;
    
    /**
     * rate at which the graphics updates
     */
    private static int graphicsFrequency = 60;
    
    /**
     * rate at which the physics updates
     */
    private static int physicsFrequency = 120;
    
    /**
     * whether the game is paused or not
     */
    private static boolean paused = true;
    
    /**
     * time in milliseconds that the loop started
     */
    private long startTime;
    
    /**
     * create a new game loop and initialize values
     */
    GameLoop() {
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
                    long graphicsTime = 1000 / graphicsFrequency;
                    long physicsTime = 1000 / physicsFrequency;
                    if (time > prev) {
                        if (time % physicsTime == 0) updatePhysics();
                        if (time % graphicsTime == 0) updateGraphics();
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
    public static void setGraphicsFrequency(int fps) {
        graphicsFrequency = Math.min(fps, physicsFrequency);
    }
    
    /**
     * set the FPS for physics ticking
     *
     * @param fps frames per second
     */
    public static void setPhysicsFrequency(int fps) {
        physicsFrequency = Math.max(fps, graphicsFrequency);
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
    
    /**
     * set the active scene that will be ticked and rendered by the game loop
     *
     * @param scene scene to set as active scene to tick and render
     */
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

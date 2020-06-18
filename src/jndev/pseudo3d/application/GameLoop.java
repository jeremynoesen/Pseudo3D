package jndev.pseudo3d.application;

import jndev.pseudo3d.renderer.Renderer;
import jndev.pseudo3d.scene.Scene;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;

/**
 * game loop to update graphics and physics for the active scene
 */
public class GameLoop extends JPanel {
    
    /**
     * active scene to render and tick
     */
    private Scene activeScene;
    
    /**
     * rate at which the graphics updates
     */
    private int graphicsFrequency;
    
    /**
     * rate at which the physics updates
     */
    private int physicsFrequency;
    
    /**
     * whether the game is paused or not, default true
     */
    private boolean paused;
    
    /**
     * whether the game loop has been stopped or not, default false
     */
    private boolean end;
    
    /**
     * runnables to be inserted into the game loop
     */
    private Set<Runnable> runnables;
    
    /**
     * create a new game loop with default values
     */
    GameLoop() {
        activeScene = null;
        graphicsFrequency = 60;
        physicsFrequency = 120;
        paused = true;
        end = false;
        runnables = new HashSet<>();
    }
    
    /**
     * start the loop
     */
    public void start() {
        paused = false;
        long startTime = System.currentTimeMillis();
        
        //start loop in new thread
        Thread thread = new Thread(() -> {
            long prev = 0;
            while (!end) {
                if (!paused) {
                    long time = System.currentTimeMillis() - startTime;
                    long graphicsDelta = 1000 / graphicsFrequency;
                    long physicsDelta = 1000 / physicsFrequency;
                    //get time intervals
                    if (time > prev) {
                        //only run when time changes
                        if (time % physicsDelta == 0) {
                            runnables.forEach(Runnable::run);
                            //run all runnables
                            updatePhysics();
                            //update scene physics
                        }
                        if (time % graphicsDelta == 0)
                            updateGraphics();
                        //update scene graphics
                    }
                    prev = time;
                }
            }
        });
        thread.start();
        //start thread
        
        setVisible(true);
        requestFocus();
        //set panel visible and focused
    }
    
    /**
     * stop the loop
     */
    public void stop() {
        end = true;
    }
    
    /**
     * pause the loop
     */
    public void pause() {
        paused = true;
    }
    
    /**
     * unpause the loop
     */
    public void resume() {
        paused = false;
    }
    
    /**
     * set the frequency for graphics rendering
     *
     * @param frequency renders per second (Hertz)
     */
    public void setGraphicsFrequency(int frequency) {
        graphicsFrequency = Math.min(frequency, physicsFrequency);
    }
    
    /**
     * set the frequency for physics calculations
     *
     * @param frequency calculations per second (Hertz)
     */
    public void setPhysicsFrequency(int frequency) {
        physicsFrequency = Math.max(frequency, graphicsFrequency);
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
    public void setActiveScene(Scene scene) {
        activeScene = scene;
    }
    
    /**
     * get the current active scene
     *
     * @return active scene
     */
    public Scene getActiveScene() {
        return activeScene;
    }
    
    /**
     * render the active scene
     *
     * @param g graphics
     */
    @Override
    public void paintComponent(Graphics g) {
        if (activeScene != null) Renderer.render(activeScene, this, g);
    }
    
    /**
     * add a runnable to the game loop to execute code that is not usually in the game loop. these execute at the same
     * frequency as physics updates
     *
     * @param runnable runnable
     */
    public void inject(Runnable runnable) {
        runnables.add(runnable);
    }
    
    /**
     * remove a runnable from the game loop
     *
     * @param runnable runnable
     */
    public void remove(Runnable runnable) {
        runnables.remove(runnable);
    }
}

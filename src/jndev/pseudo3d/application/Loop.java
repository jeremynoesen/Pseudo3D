package jndev.pseudo3d.application;

import jndev.pseudo3d.scene.Renderer;
import jndev.pseudo3d.scene.Scene;
import jndev.pseudo3d.sprites.AnimatedSprite;
import jndev.pseudo3d.sprites.CameraSprite;
import jndev.pseudo3d.sprites.Sprite;

import javax.swing.*;
import java.awt.*;

/**
 * game loop to update graphics and physics for the active scene
 *
 * @author JNDev (Jeremaster101)
 */
public class Loop extends JPanel {
    
    /**
     * active scene to render and tick
     */
    private Scene activeScene;
    
    /**
     * rate at which the graphics render
     */
    private int renderFrequency;
    
    /**
     * rate at which physics and other things tick
     */
    private int tickFrequency;
    
    /**
     * whether the game is paused or not, default true
     */
    private boolean paused;
    
    /**
     * whether the game loop has been stopped or not, default false
     */
    private boolean stopped;
    
    /**
     * create a new game loop with default values
     */
    Loop() {
        activeScene = null;
        renderFrequency = 60;
        tickFrequency = 120;
        paused = true;
        stopped = false;
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
            while (!stopped) {
                if (!paused) {
                    long time = System.currentTimeMillis() - startTime;
                    long graphicsDelta = 1000 / renderFrequency;
                    long physicsDelta = 1000 / tickFrequency;
                    //get time intervals
                    
                    if (time > prev) {
                        //only run when time changes
                        
                        if (time % physicsDelta == 0) {
                            activeScene.getRunnables().forEach(Runnable::run);
                            //run all runnables in scene
                            
                            activeScene.tick();
                            //tick scene objects
                        }
                        
                        if (time % graphicsDelta == 0) {
                            
                            for (int i = 0; i < activeScene.getObjects().size(); i++) {
                                if (activeScene.getObjects().get(i).getSprite() != null) {
                                    Sprite sprite = activeScene.getObjects().get(i).getSprite();
                                    
                                    if (sprite instanceof AnimatedSprite) {
                                        ((AnimatedSprite) sprite).update();
                                        //update animated sprites
                                        
                                    } else if (sprite instanceof CameraSprite) {
                                        ((CameraSprite) sprite).update();
                                        //update camera sprites
                                    }
                                }
                            }
                            
                            repaint();
                            Toolkit.getDefaultToolkit().sync();
                            //update scene graphics
                        }
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
        stopped = true;
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
    public void setRenderFrequency(int frequency) {
        renderFrequency = frequency;
    }
    
    /**
     * get the frequency of rendering
     *
     * @return renders per second
     */
    public int getRenderFrequency() {
        return renderFrequency;
    }
    
    /**
     * set the frequency for scene ticking
     *
     * @param frequency ticks per second (Hertz)
     */
    public void setTickFrequency(int frequency) {
        tickFrequency = frequency;
    }
    
    /**
     * get ticking frequency
     *
     * @return ticks per second
     */
    public int getTickFrequency() {
        return tickFrequency;
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
        Renderer.render(activeScene, activeScene.getCamera(), g);
    }
}

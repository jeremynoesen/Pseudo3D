package jndev.pseudo3d.application;

import jndev.pseudo3d.scene.Scene;
import jndev.pseudo3d.scene.SceneRenderer;
import jndev.pseudo3d.sceneobject.Renderable;
import jndev.pseudo3d.sprite.AnimatedSprite;
import jndev.pseudo3d.sprite.CameraSprite;
import jndev.pseudo3d.sprite.Sprite;

/**
 * game loop to update graphics and physics for the active scene
 *
 * @author JNDev (Jeremaster101)
 */
public class GameLoop {
    
    /**
     * active scene to render and tick
     */
    private Scene activeScene;
    
    /**
     * rate at which the graphics render
     */
    private float renderFrequency;
    
    /**
     * length of time for one render
     */
    private int renderDelta;
    
    /**
     * rate at which physics and other things tick
     */
    private float tickFrequency;
    
    /**
     * length of time for one tick
     */
    private int tickDelta;
    
    /**
     * whether the game is paused or not, default true
     */
    private boolean paused;
    
    /**
     * create a new game loop with default values
     */
    GameLoop() {
        activeScene = null;
        setRenderFrequency(60);
        setTickFrequency(120);
        paused = true;
    }
    
    /**
     * start the loop
     */
    public void start() {
        paused = false;
        
        //start tick loop in new thread
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(tickDelta);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //delay loop
                
                if (!paused) activeScene.tick();
                //tick scene
            }
        }).start();
        //start thread
        
        //start graphics loop in new thread
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(renderDelta);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //delay loop
                
                if (!paused) {
                    for (Renderable object : activeScene.getObjects()) {
                        if (object.getSprite() != null) {
                            Sprite sprite = object.getSprite();
                            
                            if (sprite instanceof AnimatedSprite) {
                                ((AnimatedSprite) sprite).update();
                                //update animated sprites
                                
                            } else if (sprite instanceof CameraSprite) {
                                ((CameraSprite) sprite).update();
                                //update camera sprites
                            }
                        }
                    }
                    
                    SceneRenderer.render(activeScene, activeScene.getCamera(),
                            Pseudo3D.getCanvas().getGraphicsContext2D());
                    //update scene graphics
                }
            }
        }).start();
        //start thread
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
    public void setRenderFrequency(float frequency) {
        renderFrequency = frequency;
        renderDelta = (int) Math.floor(1000 / renderFrequency);
    }
    
    /**
     * get the frequency of rendering
     *
     * @return renders per second
     */
    public float getRenderFrequency() {
        return renderFrequency;
    }
    
    /**
     * set the frequency for scene ticking
     *
     * @param frequency ticks per second (Hertz)
     */
    public void setTickFrequency(float frequency) {
        tickFrequency = frequency;
        tickDelta = (int) Math.floor(1000 / tickFrequency);
    }
    
    /**
     * get ticking frequency
     *
     * @return ticks per second
     */
    public float getTickFrequency() {
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
}

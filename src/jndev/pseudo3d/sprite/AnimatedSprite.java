package jndev.pseudo3d.sprite;

import jndev.pseudo3d.application.Game;

import java.awt.*;
import java.util.ArrayList;

/**
 * series of images combined to create a single animated sprite
 *
 * @author JNDev (Jeremaster101)
 */
public class AnimatedSprite implements Sprite {
    
    /**
     * current image of sprite
     */
    private Image image;
    
    /**
     * current frame number
     */
    private double currentFrame;
    
    /**
     * all images of the animated sprite
     */
    private final ArrayList<Image> images;
    
    /**
     * time between frames
     */
    private final double frameStep;
    
    /**
     * create a new animated sprite with a list of images and frame rate
     *
     * @param images    all images of the animated sprite
     * @param frameRate frames per second of the sprite
     */
    public AnimatedSprite(ArrayList<Image> images, double frameRate) {
        this.frameStep = frameRate / 1000.0;
        currentFrame = 0;
        this.images = images;
        image = images.get(0);
    }
    
    /**
     * copy constructor for animated sprite
     *
     * @param animatedSprite animated sprite to copy
     */
    public AnimatedSprite(AnimatedSprite animatedSprite) {
        frameStep = animatedSprite.frameStep;
        currentFrame = animatedSprite.currentFrame;
        images = animatedSprite.images;
        image = animatedSprite.image;
    }
    
    /**
     * set the current frame to the next available frame based on elapsed time
     */
    public void update() {
        double renderStep = Game.getInstance().getLoop().getRenderFrequency() / 1000.0;
        currentFrame = currentFrame + (frameStep / renderStep) < images.size() ? currentFrame + (frameStep / renderStep) : 0;
        image = images.get((int) Math.floor(currentFrame));
    }
    
    /**
     * get the current frame as an image
     *
     * @return image of current frame
     */
    @Override
    public Image getImage() {
        return image;
    }
}

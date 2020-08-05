package jndev.pseudo3d.sprite;

import java.awt.*;
import java.util.ArrayList;

/**
 * series of images combined to create a single animated sprite
 */
public class AnimatedSprite implements Sprite {
    
    /**
     * current frame number
     */
    private int currentFrame;
    
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
     * @param images all images of the animated sprite
     * @param frameRate frames per second of the sprite
     */
    public AnimatedSprite(ArrayList<Image> images, double frameRate) {
        this.frameStep = frameRate;
        currentFrame = 0;
        this.images = images;
    }
    
    /**
     * set the current frame to the next available frame based on elapsed time
     */
    public void update() {
        currentFrame = (currentFrame < images.size()) ? currentFrame + 1 : 0;
    }
    
    /**
     * get the current frame as an image
     *
     * @return image of current frame
     */
    @Override
    public Image getImage() {
        return images.get(currentFrame);
    }
}

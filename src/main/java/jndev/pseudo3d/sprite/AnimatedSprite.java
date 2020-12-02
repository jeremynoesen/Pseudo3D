package jndev.pseudo3d.sprite;

import javafx.scene.image.Image;
import jndev.pseudo3d.application.Pseudo3D;
import jndev.pseudo3d.util.FastMath;

import java.util.ArrayList;
import java.util.Objects;

/**
 * series of images combined to create a single animated sprite
 *
 * @author JNDev (Jeremaster101)
 */
public class AnimatedSprite extends Sprite {
    
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
    public AnimatedSprite(ArrayList<Image> images, float frameRate) {
        super(images.get(0));
        this.frameStep = frameRate / 1000.0f;
        currentFrame = 0;
        this.images = images;
    }
    
    /**
     * copy constructor for animated sprite
     *
     * @param animatedSprite animated sprite to copy
     */
    public AnimatedSprite(AnimatedSprite animatedSprite) {
        super(animatedSprite);
        frameStep = animatedSprite.frameStep;
        currentFrame = animatedSprite.currentFrame;
        images = animatedSprite.images;
    }
    
    /**
     * set the current frame to the next available frame based on elapsed time
     */
    public void update() {
        double renderStep = Pseudo3D.getRenderFrequency() / 1000.0;
        currentFrame = currentFrame + (frameStep / renderStep) <
                images.size() ? currentFrame + (frameStep / renderStep) : 0;
        image = images.get(FastMath.floor((float) currentFrame));
    }
    
    /**
     * check that the animated sprite in similar to another
     *
     * @param o object to check
     * @return true if sprites are similar
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        AnimatedSprite that = (AnimatedSprite) o;
        return Double.compare(that.currentFrame, currentFrame) == 0 &&
                Double.compare(that.frameStep, frameStep) == 0 &&
                Objects.equals(images, that.images) &&
                super.equals(that);
    }
}

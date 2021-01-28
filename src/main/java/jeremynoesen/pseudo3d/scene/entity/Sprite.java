package jeremynoesen.pseudo3d.scene.entity;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import jeremynoesen.pseudo3d.Pseudo3D;

import java.util.ArrayList;
import java.util.Objects;

/**
 * sprites to be rendered in game
 *
 * @author Jeremy Noesen
 */
public class Sprite {
    
    /**
     * image used for sprite
     */
    private Image image;
    
    /**
     * width of the sprite in grid units
     */
    private float width;
    
    /**
     * height of the sprite in grid units
     */
    private float height;
    
    /**
     * counter-clock-wise rotation of sprite in radians
     */
    private float rotation;
    
    /**
     * current frame number
     */
    private float currentFrame;
    
    /**
     * all images of the animated sprite
     */
    private ArrayList<Image> images;
    
    /**
     * time between frames
     */
    private float frameStep;
    
    /**
     * create a new image sprite
     *
     * @param width  sprite width
     * @param height sprite height
     */
    public Sprite(float width, float height, Image image) {
        this.image = image;
        this.width = width;
        this.height = height;
        this.rotation = 0;
    }
    
    /**
     * create a new color sprite with specified dimensions and color
     *
     * @param width  sprite width in grid units
     * @param height sprite height in grid units
     * @param color  sprite color
     */
    public Sprite(float width, float height, Color color) {
        this(width, height, new WritableImage(1, 1));
        this.width = width;
        this.height = height;
        ((WritableImage) image).getPixelWriter().setColor(0, 0, color);
    }
    
    /**
     * create a new animated sprite with a list of images and frame rate
     *
     * @param images    all images of the animated sprite
     * @param frameRate frames per second of the sprite
     * @param width  sprite width in grid units
     * @param height sprite height in grid units
     */
    public Sprite(float width, float height, ArrayList<Image> images, float frameRate) {
        this(width, height, images.get(0));
        this.frameStep = frameRate / 1000.0f;
        this.currentFrame = 0;
        this.images = images;
    }
    
    /**
     * copy constructor for sprites
     *
     * @param sprite sprite to copy
     */
    public Sprite(Sprite sprite) {
        image = sprite.image;
        width = sprite.getWidth();
        height = sprite.getHeight();
        rotation = sprite.getRotation();
        if (sprite.images != null) images = new ArrayList<>(sprite.images);
        frameStep = sprite.frameStep;
        currentFrame = sprite.currentFrame;
    }
    
    /**
     * get the sprite image
     *
     * @return sprite image
     */
    public Image getImage() {
        return image;
    }
    
    /**
     * set the dimensions of the sprite
     *
     * @param width  width of sprite in grid units
     * @param height height of sprite in grid units
     */
    public void setDimensions(float width, float height) {
        setWidth(width);
        setHeight(height);
    }
    
    /**
     * get the width of the sprite image in grid units
     *
     * @return width of sprite
     */
    public float getWidth() {
        return width;
    }
    
    /**
     * get the height of the sprite image in grid units
     *
     * @return height of sprite
     */
    public float getHeight() {
        return height;
    }
    
    /**
     * set the width of the sprite, which the image will stretch to fit
     *
     * @param width new sprite width in grid units
     */
    public void setWidth(float width) {
        this.width = width;
    }
    
    /**
     * set the height of the sprite, which the image will stretch to fit
     *
     * @param height new sprite height in grid units
     */
    public void setHeight(float height) {
        this.height = height;
    }
    
    /**
     * set the rotation of the sprite
     *
     * @param rotation rotation of sprite in radians counter-clock-wise
     */
    public void setRotation(float rotation) {
        this.rotation = rotation;
    }
    
    /**
     * get the rotation of the sprite
     *
     * @return rotation of sprite in radians counter-clock-wise
     */
    public float getRotation() {
        return rotation;
    }
    
    /**
     * get the framerate of the sprite
     *
     * @return framerate of sprite
     */
    public float getFramerate() {
        return frameStep * 1000;
    }
    
    /**
     * set the framerate for the sprite
     *
     * @param framerate frames per second
     */
    public void setFramerate(float framerate) {
        frameStep = framerate / 1000f;
    }
    
    /**
     * set the current frame to the next available frame based on elapsed time
     */
    public void update() {
        if (images != null && !images.isEmpty()) {
            float renderStep = Pseudo3D.getDeltaTime();
            currentFrame = currentFrame + (frameStep / renderStep) <
                    images.size() ? currentFrame + (frameStep / renderStep) : 0;
            image = images.get((int) Math.floor(currentFrame));
        }
    }
    
    /**
     * check if two sprites are similar to each other
     *
     * @param o object to check
     * @return true if the two sprites are equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sprite sprite = (Sprite) o;
        return Float.compare(sprite.width, width) == 0 &&
                Float.compare(sprite.height, height) == 0 &&
                Float.compare(sprite.rotation, rotation) == 0 &&
                Double.compare(sprite.currentFrame, currentFrame) == 0 &&
                Double.compare(sprite.frameStep, frameStep) == 0 &&
                Objects.equals(image, sprite.image) &&
                Objects.equals(images, sprite.images);
    }
}

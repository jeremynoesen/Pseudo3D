package xyz.jeremynoesen.pseudo3d.scene.entity;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import xyz.jeremynoesen.pseudo3d.scene.util.Vector;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
     * counter-clock-wise rotation of sprite in degrees
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
     * whether the sprite animation should loop or not
     */
    private boolean loop;

    /**
     * whether the sprite animation is paused or not
     */
    private boolean paused;

    /**
     * create a new image sprite
     *
     * @param width  sprite width in grid units
     * @param height sprite height in grid units
     * @param src    path to image to use for sprite
     */
    public Sprite(float width, float height, String src) throws FileNotFoundException {
        this.image = new Image(new FileInputStream(src));
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
        this.image = new WritableImage(1, 1);
        this.width = width;
        this.height = height;
        this.rotation = 0;
        ((WritableImage) image).getPixelWriter().setColor(0, 0, color);
    }

    /**
     * create a new animated sprite with a list of images and frame rate
     *
     * @param src       path to all images of the animated sprite
     * @param frameRate frames per second of the sprite
     * @param width     sprite width in grid units
     * @param height    sprite height in grid units
     * @param loop      true to allow sprite to loop
     */
    public Sprite(float width, float height, float frameRate, boolean loop, String[] src) throws FileNotFoundException {
        ArrayList<Image> images = new ArrayList<>();
        for (String s : src) {
            images.add(new Image(new FileInputStream(s)));
        }
        this.images = images;
        this.image = images.get(0);
        this.width = width;
        this.height = height;
        this.rotation = 0;
        this.frameStep = 1 / frameRate;
        this.currentFrame = 0;
        this.loop = loop;
    }

    /**
     * copy constructor for sprites
     *
     * @param sprite sprite to copy
     */
    public Sprite(Sprite sprite) {
        image = sprite.image;
        width = sprite.width;
        height = sprite.height;
        rotation = sprite.rotation;
        if (sprite.images != null) images = new ArrayList<>(sprite.images);
        frameStep = sprite.frameStep;
        currentFrame = sprite.currentFrame;
        loop = sprite.loop;
        paused = sprite.paused;
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
     * @param dimensions 2D vector for dimensions
     */
    public Sprite setDimensions(Vector dimensions) {
        setWidth(dimensions.getX());
        setHeight(dimensions.getY());
        return this;
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
    public Sprite setWidth(float width) {
        this.width = width;
        return this;
    }

    /**
     * set the height of the sprite, which the image will stretch to fit
     *
     * @param height new sprite height in grid units
     */
    public Sprite setHeight(float height) {
        this.height = height;
        return this;
    }

    /**
     * set the rotation of the sprite
     *
     * @param rotation rotation of sprite in degrees counter-clock-wise
     */
    public Sprite setRotation(float rotation) {
        this.rotation = rotation;
        return this;
    }

    /**
     * get the rotation of the sprite
     *
     * @return rotation of sprite in degrees counter-clock-wise
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
        return 1 / frameStep;
    }

    /**
     * set the framerate for the sprite
     *
     * @param framerate frames per second
     */
    public Sprite setFramerate(float framerate) {
        frameStep = 1 / framerate;
        return this;
    }

    /**
     * set the sprite to loop or not
     *
     * @param loop true to allow animation loop
     */
    public Sprite setLoop(boolean loop) {
        this.loop = loop;
        return this;
    }

    /**
     * check if the sprite can loop animation
     *
     * @return true if the animation can loop
     */
    public boolean canLoop() {
        return loop;
    }

    /**
     * set the animation to be paused
     *
     * @param paused true to pause the animation
     */
    public Sprite setPaused(boolean paused) {
        this.paused = paused;
        return this;
    }

    /**
     * check if the animation is paused
     *
     * @return true if paused
     */
    public boolean isPaused() {
        return paused;
    }

    /**
     * set the current frame to the next available frame based on elapsed time, usually called by the renderer
     *
     * @param deltaTime time elapsed for the render frame
     */
    public void update(float deltaTime) {
        if (!paused && images != null && !images.isEmpty() && frameStep != 0) {
            try {
                currentFrame = currentFrame + (deltaTime / frameStep);
                image = images.get(getFrame());
            } catch (IndexOutOfBoundsException e) {
                if (loop) {
                    currentFrame = frameStep > 0 ? (currentFrame - images.size()) : (currentFrame + images.size());
                    image = images.get(getFrame());
                } else {
                    currentFrame = frameStep > 0 ? 0 : images.size();
                    paused = true;
                }
            }
        }
    }

    /**
     * set the current frame to the next frame
     */
    public Sprite nextFrame() {
        boolean paused = this.paused;
        setPaused(false);
        update(Math.abs(frameStep));
        setPaused(paused);
        return this;
    }

    /**
     * set the current frame to the previous frame
     */
    public Sprite previousFrame() {
        boolean paused = this.paused;
        float frameStep = this.frameStep;
        setPaused(false);
        this.frameStep = -this.frameStep;
        update(Math.abs(frameStep));
        setPaused(paused);
        this.frameStep = frameStep;
        return this;
    }

    /**
     * get the current frame of the animation
     *
     * @return current frame of the animation
     */
    public int getFrame() {
        return frameStep > 0 ? (int) Math.floor(currentFrame) : (int) Math.ceil(currentFrame);
    }

    /**
     * set the current frame in the animation
     *
     * @param frame frame number
     */
    public Sprite setFrame(int frame) {
        currentFrame = frame;
        image = images.get(frame);
        return this;
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
                Objects.equals(images, sprite.images) &&
                Objects.equals(loop, sprite.loop) &&
                Objects.equals(paused, sprite.paused);
    }
}

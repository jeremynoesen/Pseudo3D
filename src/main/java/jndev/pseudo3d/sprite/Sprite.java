package jndev.pseudo3d.sprite;

import javafx.scene.image.Image;

import java.util.Objects;

/**
 * sprites to be rendered in game
 *
 * @author JNDev (Jeremaster101)
 */
public abstract class Sprite {
    
    /**
     * image used for sprite in pixels
     */
    protected Image image;
    
    /**
     * width of the sprite
     */
    protected double width;
    
    /**
     * height of the sprite in pixels
     */
    protected double height;
    
    /**
     * counter-clock-wise rotation of sprite in radians
     */
    protected float rotation;
    
    /**
     * construct new sprite only when subclassed
     */
    protected Sprite(Image image) {
        this.image = image;
        this.width = image.getWidth();
        this.height = image.getHeight();
        this.rotation = 0;
    }
    
    /**
     * copy constructor for sprites
     *
     * @param sprite sprite to copy
     */
    protected Sprite(Sprite sprite) {
        image = sprite.image;
        width = sprite.getWidth();
        height = sprite.getHeight();
        rotation = sprite.getRotation();
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
     * get the width of the sprite image
     *
     * @return width of sprite
     */
    public double getWidth() {
        return width;
    }
    
    /**
     * get the height of the sprite image
     *
     * @return height of sprite
     */
    public double getHeight() {
        return height;
    }
    
    /**
     * set the width of the sprite, which the image will stretch to fit
     *
     * @param width new sprite width
     */
    public void setWidth(double width) {
        this.width = width;
    }
    
    /**
     * set the height of the sprite, which the image will stretch to fit
     *
     * @param height new sprite height
     */
    public void setHeight(double height) {
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
     * check if two sprites are similar to each other
     *
     * @param o object to check
     * @return true if the two sprites are equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sprite that = (Sprite) o;
        return width == that.width &&
                height == that.height &&
                Float.compare(that.rotation, rotation) == 0 &&
                Objects.equals(image, that.image);
    }
}

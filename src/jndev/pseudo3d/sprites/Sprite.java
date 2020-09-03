package jndev.pseudo3d.sprites;

import java.awt.image.BufferedImage;

/**
 * sprites to be rendered in game
 *
 * @author JNDev (Jeremaster101)
 */
public abstract class Sprite {
    
    /**
     * image used for sprite
     */
    protected BufferedImage image;
    
    /**
     * width of the sprite
     */
    protected int width;
    
    /**
     * height of the sprite
     */
    protected int height;
    
    /**
     * get the sprite image
     *
     * @return sprite image
     */
    public BufferedImage getImage() {
        return image;
    }
    
    /**
     * set the image used for the sprite, helper method that also sets the image width and height
     *
     * @param image image to set
     */
    protected void setImage(BufferedImage image) {
        this.image = image;
        this.width = image.getWidth();
        this.height = image.getHeight();
    }
    
    /**
     * get the width of the sprite image
     *
     * @return width of sprite
     */
    public int getWidth() {
        return width;
    }
    
    /**
     * get the height of the sprite image
     *
     * @return height of sprite
     */
    public int getHeight() {
        return height;
    }
    
    /**
     * set the width of the sprite, which the image will stretch to fit
     *
     * @param width new sprite width
     */
    public void setWidth(int width) {
        this.width = width;
    }
    
    /**
     * set the height of the sprite, which the image will stretch to fit
     *
     * @param height new sprite height
     */
    public void setHeight(int height) {
        this.height = height;
    }
}

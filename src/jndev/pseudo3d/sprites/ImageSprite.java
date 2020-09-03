package jndev.pseudo3d.sprites;

import java.awt.image.BufferedImage;

/**
 * sprite made up of a single image
 *
 * @author JNDev (Jeremaster101)
 */
public class ImageSprite extends Sprite {
    
    /**
     * create new image sprite
     *
     * @param image image to represent sprite
     */
    public ImageSprite(BufferedImage image) {
        setImage(image);
    }
}

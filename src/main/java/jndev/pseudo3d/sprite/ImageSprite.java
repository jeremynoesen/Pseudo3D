package jndev.pseudo3d.sprite;

import javafx.scene.image.Image;

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
    public ImageSprite(Image image) {
        super(image);
    }
    
    /**
     * copy constructor for image sprite
     *
     * @param sprite image sprite to copy
     */
    public ImageSprite(ImageSprite sprite) {
        super(sprite);
    }
}

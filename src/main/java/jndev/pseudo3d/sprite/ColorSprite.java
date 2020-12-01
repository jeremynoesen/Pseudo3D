package jndev.pseudo3d.sprite;

import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

/**
 * sprite consisting of a single color
 *
 * @author JNDev (Jeremaster101)
 */
public class ColorSprite extends Sprite {
    
    /**
     * create a new color sprite with specified dimensions and color
     *
     * @param width  sprite width
     * @param height sprite height
     * @param color  sprite color
     */
    public ColorSprite(int width, int height, Color color) {
        super(new WritableImage(1, 1));
        this.width = width;
        this.height = height;
        ((WritableImage) image).getPixelWriter().setColor(0, 0, color);
    }
    
    /**
     * copy constructor for color sprites
     *
     * @param sprite color sprite to copy
     */
    public ColorSprite(ColorSprite sprite) {
        super(sprite);
    }
}

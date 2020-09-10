package jndev.pseudo3d.sprites;

import java.awt.*;
import java.awt.image.BufferedImage;

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
        super(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB));
        this.width = width;
        this.height = height;
        Graphics2D g2d = image.createGraphics();
        g2d.setPaint(color);
        g2d.fillRect(0, 0, 1, 1);
        g2d.dispose();
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

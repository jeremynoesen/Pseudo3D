package jndev.pseudo3d.sprites;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * sprite consisting of a single color
 *
 * @author JNDev (Jeremaster101)
 */
public class ColorSprite implements Sprite {
    
    /**
     * image to represent sprite
     */
    private final Image image;
    
    /**
     * create a new color sprite with specified dimensions and color
     *
     * @param width  sprite width
     * @param height sprite height
     * @param color  sprite color
     */
    public ColorSprite(int width, int height, Color color) {
        BufferedImage i = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = i.createGraphics();
        g2d.setPaint(color);
        g2d.fillRect(0, 0, width, height);
        image = i;
        g2d.dispose();
    }
    
    /**
     * get the sprite image
     *
     * @return sprite image
     */
    @Override
    public Image getImage() {
        return image;
    }
}

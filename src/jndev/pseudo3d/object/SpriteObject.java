package jndev.pseudo3d.object;

import jndev.pseudo3d.scene.Renderable;
import jndev.pseudo3d.scene.Scene;
import jndev.pseudo3d.util.Vector;

import java.awt.*;

/**
 * sprite placed in 3D space with no physics
 */
public class SpriteObject implements Renderable {
    
    /**
     * sprite image of object
     */
    private Image sprite;
    
    /**
     * position of object in 3D space
     */
    private Vector position;
    
    /**
     * scene object is in
     */
    private Scene scene;
    
    /**
     * create a new sprite object
     */
    public SpriteObject() {
        sprite = null;
        position = new Vector();
        scene = null;
    }
    
    /**
     * copyu constructor for sprite objects
     *
     * @param spriteObject sprite object to copy
     */
    public SpriteObject(SpriteObject spriteObject) {
        sprite = spriteObject.sprite;
        position = spriteObject.position;
        scene = spriteObject.scene;
    }
    
    /**
     * get the sprite image for the object
     *
     * @return sprite image
     */
    @Override
    public Image getSprite() {
        return sprite;
    }
    
    /**
     * set sprite for object
     *
     * @param sprite image to set as sprite
     */
    @Override
    public void setSprite(Image sprite) {
        this.sprite = sprite;
    }
    
    /**
     * get the position of the object
     *
     * @return object position
     */
    @Override
    public Vector getPosition() {
        return position;
    }
    
    /**
     * set the position of the object
     *
     * @param position position vector
     */
    @Override
    public void setPosition(Vector position) {
        this.position = position;
    }
}

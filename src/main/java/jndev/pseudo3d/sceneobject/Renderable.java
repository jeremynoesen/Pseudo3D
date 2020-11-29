package jndev.pseudo3d.sceneobject;

import jndev.pseudo3d.sprite.Sprite;
import jndev.pseudo3d.util.Vector;

/**
 * interface used to make an object renderable in a scene
 *
 * @author JNDev (Jeremaster101)
 */
public interface Renderable {
    
    /**
     * get the image used for the object's sprite
     *
     * @return image object sprite
     */
    Sprite getSprite();
    
    /**
     * get the object's position
     *
     * @return position vector
     */
    Vector getPosition();
}

package jndev.pseudo3d.scene;

import jndev.pseudo3d.util.Vector;

import java.awt.*;

/**
 * renderable object added to a scene
 *
 * @author JNDev (Jeremaster101)
 */
public interface Renderable {
    
    /**
     * get the image used for the object's sprite
     *
     * @return image object sprite
     */
    Image getSprite();
    
    /**
     * set the object's sprite
     *
     * @param sprite image to set as sprite
     */
    void setSprite(Image sprite);
    
    /**
     * get the object's position
     *
     * @return position vector
     */
    Vector getPosition();
    
    /**
     * set the object's position
     *
     * @param position position vector
     */
    void setPosition(Vector position);
    
    /**
     * get the scene the object is in
     *
     * @return scene object is in
     */
    Scene getScene();
    
    /**
     * set the scene the object is in
     *
     * @param scene scene to place object in
     */
    void setScene(Scene scene);
}

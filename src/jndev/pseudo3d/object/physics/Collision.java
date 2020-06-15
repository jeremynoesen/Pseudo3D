package jndev.pseudo3d.object.physics;

import jndev.pseudo3d.scene.Scene;
import jndev.pseudo3d.util.Box;
import jndev.pseudo3d.util.Side;

import java.util.HashSet;
import java.util.Set;

/**
 * collision data for an object
 *
 * @author JNDev (Jeremaster101)
 */
public abstract class Collision extends Motion {
    
    /**
     * board the object is colliding on
     */
    private Scene scene;
    
    /**
     * if an object can collide with others
     */
    private boolean collidable;
    
    /**
     * sides the object is colliding with
     */
    private final Set<Side> collidingSides;
    
    /**
     * list of objects this one is colliding with
     */
    private final Set<Collision> collidingObjects;
    
    /**
     * initializes all booleans to false and initializes array lists
     */
    protected Collision() {
        super();
        scene = null;
        collidable = true;
        collidingSides = new HashSet<>();
        collidingObjects = new HashSet<>();
    }
    
    /**
     * copy constructor for collision data
     *
     * @param collision collision data to copy
     */
    protected Collision(Collision collision) {
        super(collision);
        scene = collision.getScene();
        collidable = collision.isCollidable();
        collidingSides = new HashSet<>(collision.collidingSides);
        collidingObjects = new HashSet<>(collision.collidingObjects);
    }
    
    /**
     * check for collisions and update position based on collisions
     */
    @Override
    public void tick() {
        super.tick();
        checkCollisions();
    }
    
    /**
     * check if an object has collided with another object
     */
    private void checkCollisions() {
        collidingSides.clear();
        collidingObjects.clear();
        
        for (Collision object : scene.getObjects()) { //loop through all objects in scene
            if (object == this) continue; //ignore self
            if (super.overlaps(object)) { //check for an overlap
                if (object.isCollidable() && collidable) { //if this and other object can collide
                    //do the collision calculations
                    doCollision(object);
                }
            }
        }
    }
    
    /**
     * fix the position of this object to make a collision occur
     *
     * @param object object colliding with this object
     */
    private void doCollision(Collision object) {
        collidingObjects.add(object);
        //set object to colliding
        
        double[] overlaps = new double[6];
        overlaps[0] = Math.abs(getMinimum().getX() - object.getMaximum().getX()); //left
        overlaps[1] = Math.abs(getMaximum().getX() - object.getMinimum().getX()); //right
        overlaps[2] = Math.abs(getMinimum().getY() - object.getMaximum().getY()); //bottom
        overlaps[3] = Math.abs(getMaximum().getY() - object.getMinimum().getY()); //top
        overlaps[4] = Math.abs(getMinimum().getZ() - object.getMaximum().getZ()); //back
        overlaps[5] = Math.abs(getMaximum().getZ() - object.getMinimum().getZ()); //front
        //get overlap distances
        
        double distance = overlaps[0];
        for (int i = 0; i <= 5; i++) {
            if (overlaps[i] < distance) distance = overlaps[i];
        }
        //find min overlap
        
        if (distance == overlaps[0]) {
            // check if this object is moving in the direction of the collising side
            if (getVelocity().getX() < 0) {
                // check if the object is faster
                if (Math.abs(getVelocity().getX()) >= Math.abs(object.getVelocity().getX())) {
                    setPosition(getPosition().setX(getPosition().getX() + distance));
                    //fix object position so it is not overlapping
                    setVelocity(getVelocity().setX(0));
                    //set object velocity to 0 in the same direction
                } else {
                    setPosition(getPosition().setX(getPosition().getX() - getVelocity().getX()));
                    //if not the faster object, cancel its velocity to prevent drifting
                }
            }
            collidingSides.add(Side.LEFT);
            //add side to list of colliding sides
            
        } else if (distance == overlaps[1]) {
            if (getVelocity().getX() > 0) {
                if (Math.abs(getVelocity().getX()) >= Math.abs(object.getVelocity().getX())) {
                    setPosition(getPosition().setX(getPosition().getX() - distance));
                    setVelocity(getVelocity().setX(0));
                } else {
                    setPosition(getPosition().setX(getPosition().getX() - getVelocity().getX()));
                }
            }
            collidingSides.add(Side.RIGHT);
            
        } else if (distance == overlaps[2]) {
            if (getVelocity().getY() < 0) {
                if (Math.abs(getVelocity().getY()) >= Math.abs(object.getVelocity().getY())) {
                    setPosition(getPosition().setY(getPosition().getY() + distance));
                    setVelocity(getVelocity().setY(0));
                } else {
                    setPosition(getPosition().setY(getPosition().getY() - getVelocity().getY()));
                }
            }
            collidingSides.add(Side.BOTTOM);
            
        } else if (distance == overlaps[3]) {
            if (getVelocity().getY() > 0) {
                if (Math.abs(getVelocity().getY()) >= Math.abs(object.getVelocity().getY())) {
                    setPosition(getPosition().setY(getPosition().getY() - distance));
                    setVelocity(getVelocity().setY(0));
                } else {
                    setPosition(getPosition().setY(getPosition().getY() - getVelocity().getY()));
                }
            }
            collidingSides.add(Side.TOP);
            
        } else if (distance == overlaps[4]) {
            if (getVelocity().getZ() < 0) {
                if (Math.abs(getVelocity().getZ()) >= Math.abs(object.getVelocity().getZ())) {
                    setPosition(getPosition().setZ(getPosition().getZ() + distance));
                    setVelocity(getVelocity().setZ(0));
                } else {
                    setPosition(getPosition().setZ(getPosition().getZ() - getVelocity().getZ()));
                }
            }
            collidingSides.add(Side.BACK);
            
        } else if (distance == overlaps[5]) {
            if (getVelocity().getZ() > 0) {
                if (Math.abs(getVelocity().getZ()) >= Math.abs(object.getVelocity().getZ())) {
                    setPosition(getPosition().setZ(getPosition().getZ() - distance));
                    setVelocity(getVelocity().setZ(0));
                } else {
                    setPosition(getPosition().setZ(getPosition().getZ() - getVelocity().getZ()));
                }
            }
            collidingSides.add(Side.FRONT);
        }
    }
    
    /**
     * set the board this object is on
     *
     * @param scene board for collisions
     */
    public void setScene(Scene scene) {
        this.scene = scene;
    }
    
    /**
     * get the board the collisions are set to occur in
     *
     * @return board for collisions
     */
    public Scene getScene() {
        return scene;
    }
    
    /**
     * check if an object can be collided with
     *
     * @return true if collidable
     */
    public boolean isCollidable() {
        return collidable;
    }
    
    /**
     * enable or disable collisions for the object
     *
     * @param collidable set to true to allow collisions
     */
    public void setCollidable(boolean collidable) {
        this.collidable = collidable;
    }
    
    /**
     * check if the object is currently colliding with another object
     *
     * @return true if the object has collided with another object
     */
    public boolean isColliding() {
        return !collidingObjects.isEmpty();
    }
    
    /**
     * check if an object(s) collides with this object
     *
     * @param object object(s) to check if colliding with
     * @return true if this object collides with the other object(s)
     */
    public boolean collidesWith(Collision... object) {
        for (Collision o : object) {
            if (!collidingObjects.contains(o)) return false;
        }
        return true;
    }
    
    /**
     * check if this object is colliding on the specified side(s)
     *
     * @param side side(s) of the object
     * @return true if the object is colliding on the side(s)
     */
    public boolean collidesOn(Side... side) {
        for (Side s : side) {
            if (!collidingSides.contains(s)) return false;
        }
        return true;
    }
    
    /**
     * check if an object overlaps another. will return false if this object is colliding
     *
     * @param box object to check for overlaps
     * @return true if the object overlaps the other, false if not or if this object is colliding
     */
    @Override
    public boolean overlaps(Box box) {
        if (box instanceof Collision && collidesWith((Collision) box)) return false;
        return super.overlaps(box);
    }
}
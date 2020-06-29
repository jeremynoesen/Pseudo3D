package jndev.pseudo3d.object.physics;

import jndev.pseudo3d.scene.Scene;
import jndev.pseudo3d.util.Box;
import jndev.pseudo3d.util.Side;

import java.util.HashSet;
import java.util.Objects;
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
     * true if the object is colliding
     */
    private boolean colliding;
    
    /**
     * true if object is overlapping
     */
    private boolean overlapping;
    
    /**
     * initializes all booleans to false and initializes array lists
     */
    public Collision() {
        super();
        scene = null;
        collidable = true;
        collidingSides = new HashSet<>();
        collidingObjects = new HashSet<>();
        overlapping = false;
    }
    
    /**
     * copy constructor for collision data
     *
     * @param collision collision data to copy
     */
    public Collision(Collision collision) {
        super(collision);
        scene = collision.scene;
        collidable = collision.collidable;
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
        fixMotion();
        //fix motion to prevent clipping
        
        colliding = false;
        overlapping = false;
        collidingSides.clear();
        collidingObjects.clear();
        //reset all collision data
        
        for (int i = 0; i < scene.getObjects().size(); i++) {
            Collision object = scene.getObjects().get(i);
            //loop through all objects in scene
            
            if (object == this) continue;
            //ignore self
            
            if (super.overlaps(object)) {
                //check for an overlap
                if (object.isCollidable() && collidable) {
                    //if this and other object can collide
                    doCollision(object);
                    //do the collision calculations
                } else {
                    overlapping = true;
                    //set overlapping
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
        double[] overlaps = new double[6];
        overlaps[0] = Math.abs(getMinimum().getX() - object.getMaximum().getX()); //left
        overlaps[1] = Math.abs(getMaximum().getX() - object.getMinimum().getX()); //right
        overlaps[2] = Math.abs(getMinimum().getY() - object.getMaximum().getY()); //bottom
        overlaps[3] = Math.abs(getMaximum().getY() - object.getMinimum().getY()); //top
        overlaps[4] = Math.abs(getMinimum().getZ() - object.getMaximum().getZ()); //back
        overlaps[5] = Math.abs(getMaximum().getZ() - object.getMinimum().getZ()); //front
        //get overlap distances
        
        int zeros = 0;
        double distance = overlaps[0];
        for (int i = 0; i < 6; i++) {
            if (overlaps[i] < distance) distance = overlaps[i];
            if (Double.compare(overlaps[i], 0) == 0) zeros++;
        }
        //find min overlap and amount of 0 overlaps
        
        if (zeros > 1) return;
        //if object has more than 1 0 overlaps, it is technically not touching, so stop collision
        
        colliding = true;
        collidingObjects.add(object);
        //set object to colliding
        
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
     * fix motion vectors to prevent object clipping
     */
    private void fixMotion() {
        if ((collidesOn(Side.LEFT) && getVelocity().getX() < 0) || (collidesOn(Side.RIGHT) && getVelocity().getX() > 0)) {
            setPosition(getPosition().setX(getPosition().getX() - getVelocity().getX()));
            setVelocity(getVelocity().setX(0));
        }
        if ((collidesOn(Side.BOTTOM) && getVelocity().getY() < 0) || (collidesOn(Side.TOP) && getVelocity().getY() > 0)) {
            setPosition(getPosition().setY(getPosition().getY() - getVelocity().getY()));
            setVelocity(getVelocity().setY(0));
        }
        if ((collidesOn(Side.BACK) && getVelocity().getZ() < 0) || (collidesOn(Side.FRONT) && getVelocity().getZ() > 0)) {
            setPosition(getPosition().setZ(getPosition().getZ() - getVelocity().getZ()));
            setVelocity(getVelocity().setZ(0));
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
        return colliding;
    }
    
    /**
     * check if an object collides with this object
     *
     * @param object object to check if colliding with
     * @return true if this object collides with the other object
     */
    public boolean collidesWith(Collision object) {
        return collidingObjects.contains(object);
    }
    
    /**
     * check if this object is colliding on the specified side
     *
     * @param side side of the object
     * @return true if the object is colliding on the side
     */
    public boolean collidesOn(Side side) {
        return collidingSides.contains(side);
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
    
    /**
     * see if this object is overlapping any object
     *
     * @return true if overlapping
     */
    public boolean isOverlapping() {
        return overlapping;
    }
    
    /**
     * check if the collision data is equal to another set of collision data
     *
     * @param o object to check
     * @return true if the collision datas are equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Collision collision = (Collision) o;
        return collidable == collision.collidable &&
                colliding == collision.colliding &&
                overlapping == collision.overlapping &&
                scene.equals(collision.scene) &&
                Objects.equals(collidingSides, collision.collidingSides) &&
                Objects.equals(collidingObjects, collision.collidingObjects);
    }
}
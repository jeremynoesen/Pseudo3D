package jndev.pseudo3d.object;

import jndev.pseudo3d.scene.Scene;
import jndev.pseudo3d.util.Box;
import jndev.pseudo3d.util.Side;
import jndev.pseudo3d.util.Vector;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * axis-aligned bounding box rigid body physics
 */
public abstract class AABBPhysics {
    
    /**
     * gravity constant used for physics simulation. (pixels / tick ^ 2))
     */
    private final double GRAVITY = 0.0981;
    
    /**
     * position of rigid body (pixels)
     */
    private Vector position;
    
    /**
     * velocity of rigid body, or rate of change of position (pixels / tick)
     */
    private Vector velocity;
    
    /**
     * acceleration of rigid body, or rate of change of velocity (pixels / tick ^ 2)
     */
    private Vector acceleration;
    
    /**
     * jerk of rigid body, or rate of change of acceleration (pixels / tick ^ 3)
     */
    private Vector jerk;
    
    /**
     * vertical terminal velocity of rigid body (pixels / tick)
     */
    private double terminalVelocity;
    
    /**
     * drag applied to the rigid body, used to slow down per direction. for simplicity, this is a negative acceleration
     * vector (pixels / tick ^ 2)
     */
    private Vector drag;
    
    /**
     * value used to scale gravity constant
     */
    private double gravityScale;
    
    /**
     * scene the rigid body is colliding in
     */
    private Scene scene;
    
    /**
     * if an object can collide with others
     */
    private boolean collidable;
    
    /**
     * sides the rigid body is colliding on
     */
    private final Set<Side> collidingSides;
    
    /**
     * list of rigid bodies this one is colliding with
     */
    private final Set<AABBPhysics> collidingObjects;
    
    /**
     * rigid body's collision status
     */
    private boolean colliding;
    
    /**
     * rigid body's overlapping status
     */
    private boolean overlapping;
    
    /**
     * bounding box for collisions
     */
    private Box box;
    
    /**
     * create a new aabb rigid body with default values
     */
    public AABBPhysics() {
        position = new Vector();
        velocity = new Vector();
        acceleration = new Vector();
        jerk = new Vector();
        terminalVelocity = 10;
        drag = new Vector(0.005, 0.005, 0.005);
        gravityScale = 1.0;
        scene = null;
        collidable = true;
        collidingSides = new HashSet<>();
        collidingObjects = new HashSet<>();
        overlapping = false;
        box = new Box();
    }
    
    /**
     * copy constructor for aabb rigid bodies
     *
     * @param aabbPhysics aabb rigid body to copy
     */
    public AABBPhysics(AABBPhysics aabbPhysics) {
        position = aabbPhysics.position;
        velocity = aabbPhysics.velocity;
        terminalVelocity = aabbPhysics.terminalVelocity;
        acceleration = aabbPhysics.acceleration;
        jerk = aabbPhysics.jerk;
        drag = aabbPhysics.drag;
        gravityScale = aabbPhysics.gravityScale;
        scene = aabbPhysics.scene;
        collidable = aabbPhysics.collidable;
        collidingSides = new HashSet<>(aabbPhysics.collidingSides);
        collidingObjects = new HashSet<>(aabbPhysics.collidingObjects);
        box = new Box(aabbPhysics.box);
    }
    
    /**
     * calculates the next frame of motion in the x, y, and z axes
     */
    public void tick() {
        updateMotion();
        checkCollisions();
    }
    
    /**
     * update the motion of the rigid body in 3D space using jerk, acceleration, velocity, and position
     */
    private void updateMotion() {
        acceleration = new Vector(acceleration.getX() + jerk.getX(),
                acceleration.getY() + jerk.getY(),
                acceleration.getZ() + jerk.getZ());
        //update acceleration based on jerk
        
        double vx = velocity.getX() + acceleration.getX();
        double vy = velocity.getY() + acceleration.getY() - (GRAVITY * gravityScale - drag.getY());
        double vz = velocity.getZ() + acceleration.getZ();
        //update velocity based on acceleration and gravity
        
        if (vx < 0) vx = Math.min(vx + drag.getX(), 0);
        if (vx > 0) vx = Math.max(vx - drag.getX(), 0);
        if (vy < 0 && vy < -terminalVelocity) vy = Math.min(vy + drag.getY(), -terminalVelocity);
        if (vy > 0) vy = Math.max(vy - drag.getY(), 0);
        if (vz < 0) vz = Math.min(vz + drag.getZ(), 0);
        if (vz > 0) vz = Math.max(vz - drag.getZ(), 0);
        //modify velocity based on drag and terminal velocity
        
        velocity = new Vector(vx, vy, vz);
        //set new velocity
        
        position = new Vector(position.getX() + velocity.getX(),
                position.getY() + velocity.getY(),
                position.getZ() + velocity.getZ());
        //update position based on velocity
        
        box.setPosition(position);
        //update bounding box position
    }
    
    /**
     * check if a rigid body has collided with this rigid body
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
            Renderable object = scene.getObjects().get(i);
            //loop through all renderable objects in scene
    
            if (object instanceof AABBPhysics) {
        
                AABBPhysics aabbRigidBody = (AABBPhysics) object;
                
                if (aabbRigidBody == this) continue;
                //ignore self
        
                if (box.overlaps(aabbRigidBody.getBoundingBox())) {
                    //check for an overlap
                    if (aabbRigidBody.isCollidable() && collidable) {
                        //if this and other object can collide
                        doCollision(aabbRigidBody);
                        //do the collision calculations
                    } else {
                        overlapping = true;
                        //set overlapping
                    }
                }
            }
        }
    }
    
    /**
     * fix the position of this rigid body to make a collision occur
     *
     * @param aabbPhysics rigid body colliding with this rigid body
     */
    private void doCollision(AABBPhysics aabbPhysics) {
        double[] overlaps = new double[6];
        overlaps[0] = Math.abs(box.getMinimum().getX() - aabbPhysics.getBoundingBox().getMaximum().getX()); //left
        overlaps[1] = Math.abs(box.getMaximum().getX() - aabbPhysics.getBoundingBox().getMinimum().getX()); //right
        overlaps[2] = Math.abs(box.getMinimum().getY() - aabbPhysics.getBoundingBox().getMaximum().getY()); //bottom
        overlaps[3] = Math.abs(box.getMaximum().getY() - aabbPhysics.getBoundingBox().getMinimum().getY()); //top
        overlaps[4] = Math.abs(box.getMinimum().getZ() - aabbPhysics.getBoundingBox().getMaximum().getZ()); //back
        overlaps[5] = Math.abs(box.getMaximum().getZ() - aabbPhysics.getBoundingBox().getMinimum().getZ()); //front
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
        collidingObjects.add(aabbPhysics);
        //set object to colliding
        
        if (distance == overlaps[0]) {
            // check if this object is moving in the direction of the collising side
            if (getVelocity().getX() < 0) {
                // check if the object is faster
                if (Math.abs(getVelocity().getX()) >= Math.abs(aabbPhysics.getVelocity().getX())) {
                    setPosition(getPosition().setX(getPosition().getX() + distance));
                    //fix object position so it is not overlapping
                } else {
                    setPosition(getPosition().setX(getPosition().getX() - getVelocity().getX()));
                    //if not the faster object, cancel its velocity to prevent drifting
                }
                setVelocity(getVelocity().setX(0));
                //set object velocity to 0 in the same direction
            }
            collidingSides.add(Side.LEFT);
            //add side to list of colliding sides
            
        } else if (distance == overlaps[1]) {
            if (getVelocity().getX() > 0) {
                if (Math.abs(getVelocity().getX()) >= Math.abs(aabbPhysics.getVelocity().getX())) {
                    setPosition(getPosition().setX(getPosition().getX() - distance));
                } else {
                    setPosition(getPosition().setX(getPosition().getX() - getVelocity().getX()));
                }
                setVelocity(getVelocity().setX(0));
            }
            collidingSides.add(Side.RIGHT);
            
        } else if (distance == overlaps[2]) {
            if (getVelocity().getY() < 0) {
                if (Math.abs(getVelocity().getY()) >= Math.abs(aabbPhysics.getVelocity().getY())) {
                    setPosition(getPosition().setY(getPosition().getY() + distance));
                } else {
                    setPosition(getPosition().setY(getPosition().getY() - getVelocity().getY()));
                }
                setVelocity(getVelocity().setY(0));
            }
            collidingSides.add(Side.BOTTOM);
            
        } else if (distance == overlaps[3]) {
            if (getVelocity().getY() > 0) {
                if (Math.abs(getVelocity().getY()) >= Math.abs(aabbPhysics.getVelocity().getY())) {
                    setPosition(getPosition().setY(getPosition().getY() - distance));
                } else {
                    setPosition(getPosition().setY(getPosition().getY() - getVelocity().getY()));
                }
                setVelocity(getVelocity().setY(0));
            }
            collidingSides.add(Side.TOP);
            
        } else if (distance == overlaps[4]) {
            if (getVelocity().getZ() < 0) {
                if (Math.abs(getVelocity().getZ()) >= Math.abs(aabbPhysics.getVelocity().getZ())) {
                    setPosition(getPosition().setZ(getPosition().getZ() + distance));
                } else {
                    setPosition(getPosition().setZ(getPosition().getZ() - getVelocity().getZ()));
                }
                setVelocity(getVelocity().setZ(0));
            }
            collidingSides.add(Side.BACK);
            
        } else if (distance == overlaps[5]) {
            if (getVelocity().getZ() > 0) {
                if (Math.abs(getVelocity().getZ()) >= Math.abs(aabbPhysics.getVelocity().getZ())) {
                    setPosition(getPosition().setZ(getPosition().getZ() - distance));
                } else {
                    setPosition(getPosition().setZ(getPosition().getZ() - getVelocity().getZ()));
                }
                setVelocity(getVelocity().setZ(0));
            }
            collidingSides.add(Side.FRONT);
        }
    }
    
    /**
     * fix motion vectors to prevent rigid body clipping
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
     * get the position vector of the rigid body
     *
     * @return position vector of rigid body
     */
    public Vector getPosition() {
        return position;
    }
    
    /**
     * set the position of the rigid body
     *
     * @param position position vector
     */
    public void setPosition(Vector position) {
        this.position = position;
        box.setPosition(position);
    }
    
    /**
     * get the velocity vector of the rigid body
     *
     * @return velocity vector of an rigid body
     */
    public Vector getVelocity() {
        return velocity;
    }
    
    /**
     * set the velocity of the rigid body
     *
     * @param velocity velocity vector
     */
    public void setVelocity(Vector velocity) {
        this.velocity = velocity;
    }
    
    /**
     * get the acceleration vector of the rigid body
     *
     * @return acceleration vector of an rigid body
     */
    public Vector getAcceleration() {
        return acceleration.setY(acceleration.getY() - (GRAVITY * gravityScale));
    }
    
    /**
     * set the acceleration of the rigid body
     *
     * @param acceleration acceleration vector
     */
    public void setAcceleration(Vector acceleration) {
        this.acceleration = acceleration;
    }
    
    /**
     * get the jerk vector of the rigid body
     *
     * @return jerk of the rigid body
     */
    public Vector getJerk() {
        return jerk;
    }
    
    /**
     * set the jerk of the rigid body
     *
     * @param jerk jerk vector
     */
    public void setJerk(Vector jerk) {
        this.jerk = jerk;
    }
    
    /**
     * get the scale gravity. default is 1.0
     *
     * @return gravity scale
     */
    public double getGravityScale() {
        return gravityScale;
    }
    
    /**
     * set the gravity scale
     *
     * @param scale gravity scale
     */
    public void setGravityScale(double scale) {
        gravityScale = scale;
    }
    
    /**
     * set terminal velocity for this rigid body
     *
     * @return terminal velocity
     */
    public double getTerminalVelocity() {
        return terminalVelocity;
    }
    
    /**
     * set the terminal velocity for this rigid body
     *
     * @param terminalVelocity terminal velocity
     */
    public void setTerminalVelocity(double terminalVelocity) {
        this.terminalVelocity = Math.abs(terminalVelocity);
    }
    
    /**
     * get the drag of the rigid body
     *
     * @return drag vector of an rigid body
     */
    public Vector getDrag() {
        return drag;
    }
    
    /**
     * set the drag for the rigid body
     *
     * @param drag drag of rigid body
     */
    public void setDrag(Vector drag) {
        this.drag = drag;
    }
    
    /**
     * set the scene this rigid body is in
     *
     * @param scene scene for collisions
     */
    public void setScene(Scene scene) {
        this.scene = scene;
    }
    
    /**
     * get the scene the collisions are set to occur in
     *
     * @return scene for collisions
     */
    public Scene getScene() {
        return scene;
    }
    
    /**
     * check if an rigid body can be collided with
     *
     * @return true if collidable
     */
    public boolean isCollidable() {
        return collidable;
    }
    
    /**
     * enable or disable collisions for the rigid body
     *
     * @param collidable true to allow collisions
     */
    public void setCollidable(boolean collidable) {
        this.collidable = collidable;
    }
    
    /**
     * check if the rigid body is currently colliding with another rigid body
     *
     * @return true if the rigid body is colliding with another rigid body
     */
    public boolean isColliding() {
        return colliding;
    }
    
    /**
     * check if an= rigid body collides with this rigid body
     *
     * @param aabbPhysics rigid body to check if colliding with
     * @return true if this rigid body collides with the other rigid body
     */
    public boolean collidesWith(AABBPhysics aabbPhysics) {
        return collidingObjects.contains(aabbPhysics);
    }
    
    /**
     * check if this rigid body is colliding on the specified side
     *
     * @param side side of the rigid body
     * @return true if the rigid body is colliding on the side
     */
    public boolean collidesOn(Side side) {
        return collidingSides.contains(side);
    }
    
    /**
     * see if this rigid body is overlapping any rigid body
     *
     * @return true if overlapping
     */
    public boolean isOverlapping() {
        return overlapping;
    }
    
    /**
     * set the bounding box for this rigid body
     *
     * @return rigid body's bounding box
     */
    public Box getBoundingBox() {
        return box;
    }
    
    /**
     * set the rigid body's bounding box centered at the object's current position
     *
     * @param box bounding box to set
     */
    public void setBoundingBox(Box box) {
        Box newBox = new Box(box);
        newBox.setPosition(position);
        this.box = newBox;
    }
    
    /**
     * check if another aabb rigid body is equal to this one
     *
     * @param o object to check for equality
     * @return true if the AABBRigidBody is equivalent to this one
     */
    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AABBPhysics that = (AABBPhysics) o;
        return Double.compare(that.terminalVelocity, terminalVelocity) == 0 &&
                Double.compare(that.gravityScale, gravityScale) == 0 &&
                collidable == that.collidable &&
                colliding == that.colliding &&
                overlapping == that.overlapping &&
                Objects.equals(position, that.position) &&
                Objects.equals(velocity, that.velocity) &&
                Objects.equals(acceleration, that.acceleration) &&
                Objects.equals(jerk, that.jerk) &&
                Objects.equals(drag, that.drag) &&
                Objects.equals(scene, that.scene) &&
                Objects.equals(collidingSides, that.collidingSides) &&
                Objects.equals(collidingObjects, that.collidingObjects) &&
                Objects.equals(box, that.box);
    }
}

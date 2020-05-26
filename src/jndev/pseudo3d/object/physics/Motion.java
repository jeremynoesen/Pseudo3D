package jndev.pseudo3d.object.physics;

import jndev.pseudo3d.util.Box;
import jndev.pseudo3d.util.Vector;

/**
 * abstract class to handle simple 2D motion for objects
 */
public abstract class Motion extends Box {
    
    /**
     * gravity constant used for physics simulation. unit is pixels per tick
     */
    private final double GRAVITY = 0.0981;
    
    /**
     * position of object
     */
    private Vector position;
    
    /**
     * velocity of object
     */
    private Vector velocity;
    
    /**
     * acceleration of object
     */
    private Vector acceleration;
    
    /**
     * value to multiply gravity constant by
     */
    private double gravityMultiplier;
    
    /**
     * initialize all values
     */
    protected Motion() {
        super();
        position = new Vector(0, 0, 0);
        velocity = new Vector(0, 0, 0);
        acceleration = new Vector(0, 0, 0);
        gravityMultiplier = 1.0;
    }
    
    /**
     * copy constructor for motion
     *
     * @param motion motion to copy
     */
    protected Motion(Motion motion) {
        super(motion);
        position = motion.getPosition();
        velocity = motion.getVelocity();
        acceleration = motion.getAcceleration();
        gravityMultiplier = motion.getGravity();
    }
    
    /**
     * calculates the next frame of motion in the x and y axis
     */
    public void tick() {
        updateMotion();
        super.setPosition(position);
    }
    
    /**
     * update the motion of an object in 2D space using acceleration, velocity, and position
     */
    private void updateMotion() {
        velocity = new Vector(velocity.getX() + acceleration.getX(),
                velocity.getY() + acceleration.getY() - (GRAVITY * gravityMultiplier),
                velocity.getZ() + acceleration.getZ());
        position = new Vector(position.getX() + velocity.getX(),
                position.getY() + velocity.getY(),
                position.getZ() + velocity.getZ());
    }
    
    /**
     * get the position vector of the object a new instance is returned so the vector cannot modify the actual position
     * of the object
     *
     * @return position vector of object
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
        super.setPosition(position);
    }
    
    /**
     * get the velocity vector of the object a new instance is returned so the vector cannot modify the actual velocity
     * of the object
     *
     * @return velocity vector of an object
     */
    public Vector getVelocity() {
        return velocity;
    }
    
    /**
     * set the velocity of the object
     *
     * @param velocity velocity vector
     */
    public void setVelocity(Vector velocity) {
        this.velocity = velocity;
    }
    
    /**
     * get the acceleration vector of the object a new instance is returned so the vector cannot modify the actual
     * acceleration of the object
     *
     * @return acceleration vector of an object
     */
    public Vector getAcceleration() {
        return acceleration;
    }
    
    /**
     * get the acceleration of the object
     *
     * @param acceleration acceleration vector
     */
    public void setAcceleration(Vector acceleration) {
        this.acceleration = acceleration;
    }
    
    /**
     * gets the multiplier of gravity. default is 1.0
     *
     * @return gravity multiplier
     */
    public double getGravity() {
        return gravityMultiplier;
    }
    
    /**
     * set a different gravity multiplier
     *
     * @param multiplier gravity multiplier
     */
    public void setGravity(double multiplier) {
        gravityMultiplier = multiplier;
    }
}

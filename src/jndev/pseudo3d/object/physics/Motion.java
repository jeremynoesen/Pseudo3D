package jndev.pseudo3d.object.physics;

import jndev.pseudo3d.util.Box;
import jndev.pseudo3d.util.Vector;

/**
 * motion data for an object
 *
 * @author JNDev (Jeremaster101)
 */
public abstract class Motion extends Box {
    
    /**
     * gravity constant used for physics simulation. unit is pixels per tick
     */
    private final double GRAVITY = 0.0981;
    
    /**
     * position of object (units)
     */
    private Vector position;
    
    /**
     * velocity of object, or rate of change of position (units / tick)
     */
    private Vector velocity;
    
    /**
     * acceleration of object, or rate of change of velocity (units / tick ^ 2)
     */
    private Vector acceleration;
    
    /**
     * jerk of an object, or rate of change of acceleration (units / tick ^ 3)
     */
    private Vector jerk;
    
    /**
     * value to multiply gravity constant by
     */
    private double gravityMultiplier;
    
    /**
     * initialize all values
     */
    protected Motion() {
        super();
        position = new Vector();
        velocity = new Vector();
        acceleration = new Vector();
        jerk = new Vector();
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
        jerk = motion.getJerk();
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
     * update the motion of an object in 2D space using jerk, acceleration, velocity, and position
     */
    private void updateMotion() {
        acceleration = new Vector(acceleration.getX() + jerk.getX(),
                acceleration.getY() + jerk.getY(),
                acceleration.getZ() + jerk.getZ());
        velocity = new Vector(velocity.getX() + acceleration.getX(),
                velocity.getY() + acceleration.getY() - (GRAVITY * gravityMultiplier),
                velocity.getZ() + acceleration.getZ());
        position = new Vector(position.getX() + velocity.getX(),
                position.getY() + velocity.getY(),
                position.getZ() + velocity.getZ());
    }
    
    /**
     * get the position vector of the object
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
     * get the velocity vector of the object
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
     * get the acceleration vector of the object
     *
     * @return acceleration vector of an object
     */
    public Vector getAcceleration() {
        return acceleration;
    }
    
    /**
     * set the acceleration of the object
     *
     * @param acceleration acceleration vector
     */
    public void setAcceleration(Vector acceleration) {
        this.acceleration = acceleration;
    }
    
    /**
     * get the jerk vector of the object
     *
     * @return jerk of the object
     */
    public Vector getJerk() {
        return jerk;
    }
    
    /**
     * set the jerk of the object
     *
     * @param jerk jerk vector
     */
    public void setJerk(Vector jerk) {
        this.jerk = jerk;
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
    
    /**
     * check if this motion data is equal to another
     *
     * @param o object to check
     * @return true if the motion datas are equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Motion motion = (Motion) o;
        return Double.compare(motion.gravityMultiplier, gravityMultiplier) == 0 &&
                position.equals(motion.position) &&
                velocity.equals(motion.velocity) &&
                acceleration.equals(motion.acceleration) &&
                jerk.equals(motion.jerk);
    }
}

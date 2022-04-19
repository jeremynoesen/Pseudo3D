package xyz.jeremynoesen.pseudo3d.scene.entity;

import xyz.jeremynoesen.pseudo3d.scene.util.Axis;
import xyz.jeremynoesen.pseudo3d.scene.util.Box;
import xyz.jeremynoesen.pseudo3d.scene.util.Side;
import xyz.jeremynoesen.pseudo3d.scene.util.Vector;

import java.util.*;

/**
 * Axis-aligned bounding box physics object
 *
 * @author Jeremy Noesen
 */
public abstract class Physics extends Box {

    /**
     * Objects this object is in a Scene with
     */
    private LinkedList<Physics> otherObjects;

    /**
     * Gravity applied to the object (meters / second ^ 2)
     */
    private Vector gravity;

    /**
     * Position of the object (meters)
     */
    private Vector position;

    /**
     * Velocity of the object (meters / second)
     */
    private Vector velocity;

    /**
     * Terminal velocity of the object (meters / second)
     * <p>
     * This will limit how fast the object can go based on gravity and acceleration
     */
    private Vector terminalVelocity;

    /**
     * Acceleration of the object (meters / second ^ 2)
     */
    private Vector acceleration;

    /**
     * Coefficient of drag per axis
     */
    private Vector drag;

    /**
     * Roughness of the object per axis, used for friction
     */
    private Vector roughness;

    /**
     * Set of collide-able sides
     */
    private final HashSet<Side> collidableSides;

    /**
     * Set of objects colliding with this object per Side
     */
    private final HashMap<Side, HashSet<Physics>> collidingObjects;

    /**
     * Set of objects overlapping this one
     */
    private final HashSet<Physics> overlappingObjects;

    /**
     * Set of kinematic Axes
     */
    private final HashSet<Axis> kinematicAxes;

    /**
     * Set of pushable Axes
     */
    private final HashSet<Axis> pushableAxes;

    /**
     * Mass of the object
     */
    private float mass;

    /**
     * Whether the object can update or not
     */
    private boolean updatable;

    /**
     * Time elapsed in the previous tick
     */
    private float deltaTime;

    /**
     * Temporary Set used in special cases of momentum
     */
    private final HashSet<Axis> skipMomentum;

    /**
     * Temporary Set used in special cases of collisions
     */
    private final HashSet<Physics> specialCollisions;

    /**
     * Create new default Physics object
     */
    public Physics() {
        super();
        gravity = new Vector(0, -9.81f, 0);
        position = new Vector();
        velocity = new Vector();
        acceleration = new Vector();
        terminalVelocity = new Vector(20, 20, 20);
        drag = new Vector(0.5f, 0.5f, 0.5f);
        roughness = new Vector(5, 5, 5);
        otherObjects = null;
        collidableSides = new HashSet<>(Arrays.asList(Side.values()));
        kinematicAxes = new HashSet<>(Arrays.asList(Axis.values()));
        pushableAxes = new HashSet<>(Arrays.asList(Axis.values()));
        skipMomentum = new HashSet<>();
        specialCollisions = new HashSet<>();
        updatable = true;
        deltaTime = 0;
        mass = 1;
        collidingObjects = new HashMap<>();
        overlappingObjects = new HashSet<>();
        for (Side s : Side.values()) collidingObjects.put(s, new HashSet<>());
    }

    /**
     * Copy constructor for Physics objects
     *
     * @param physics Physics object to copy
     */
    public Physics(Physics physics) {
        super(physics);
        gravity = physics.gravity;
        position = physics.position;
        velocity = physics.velocity;
        acceleration = physics.acceleration;
        terminalVelocity = physics.terminalVelocity;
        drag = physics.drag;
        roughness = physics.roughness;
        mass = physics.mass;
        collidableSides = new HashSet<>(physics.collidableSides);
        kinematicAxes = new HashSet<>(physics.kinematicAxes);
        pushableAxes = new HashSet<>(physics.pushableAxes);
        updatable = physics.updatable;
        deltaTime = physics.deltaTime;
        skipMomentum = new HashSet<>();
        specialCollisions = new HashSet<>();
        collidingObjects = new HashMap<>();
        overlappingObjects = new HashSet<>(physics.overlappingObjects);
        otherObjects = physics.otherObjects;
        for (Side s : Side.values()) collidingObjects.put(s, new HashSet<>(physics.collidingObjects.get(s)));
    }

    /**
     * Update the motion of the object
     *
     * @param deltaTime Time elapsed in the previous tick
     */
    public void tickMotion(float deltaTime) {
        if (!updatable || !isKinematic()) return;
        this.deltaTime = deltaTime;
        applyAcceleration();
        applyFriction();
        applyDrag();
        applyMomentum();
        updatePosition();
    }

    /**
     * Apply acceleration and gravity to the velocity
     */
    private void applyAcceleration() {
        for (Axis axis : Axis.values()) {
            if (!isKinematicOn(axis)) continue;

            float v = velocity.get(axis);
            float a = acceleration.get(axis) + gravity.get(axis);
            float vt = terminalVelocity.get(axis);

            if (v > -vt && a < 0)
                velocity = velocity.set(axis, Math.max(v + (a * deltaTime), -vt));
            else if (v < vt && a > 0)
                velocity = velocity.set(axis, Math.min(v + (a * deltaTime), vt));
        }
    }

    /**
     * Apply the effect of friction to the velocity
     */
    private void applyFriction() {
        Vector friction = calculateFriction();

        float vx = velocity.getX(), vy = velocity.getY(), vz = velocity.getZ();
        float fx = friction.getX(), fy = friction.getY(), fz = friction.getZ();

        if (isKinematicOn(Axis.X)) {
            if (vx < 0) vx = Math.min(vx + fy + fz, 0);
            else if (vx > 0) vx = Math.max(vx - fy - fz, 0);
        }
        if (isKinematicOn(Axis.Y)) {
            if (vy < 0) vy = Math.min(vy + fx + fz, 0);
            else if (vy > 0) vy = Math.max(vy - fx - fz, 0);
        }
        if (isKinematicOn(Axis.Z)) {
            if (vz < 0) vz = Math.min(vz + fx + fy, 0);
            else if (vz > 0) vz = Math.max(vz - fx - fy, 0);
        }

        velocity = new Vector(vx, vy, vz);
    }

    /**
     * Get the friction Vector for the object
     *
     * @return Friction Vector
     */
    private Vector calculateFriction() {
        Vector output = new Vector();
        for (Axis axis : Axis.values()) {
            if (!isKinematicOn(axis)) continue;

            float v = velocity.get(axis);

            if (collidesOn(axis)) {
                float f = 0;
                int count = 0;
                Side side = Side.getSide(axis, v);
                float stackedMass = calculateStackedMass(v, axis);

                if (side != null && collidesOn(side)) {
                    for (Physics physics : collidingObjects.get(side)) {
                        if (physics.updatable) {
                            f += physics.roughness.get(axis) * Math.abs(v - physics.getVelocity().get(axis));
                            count++;
                        }
                    }
                }

                if (count > 0) f = ((f + roughness.get(axis)) / (count + 1)) * stackedMass * deltaTime;
                output = output.set(axis, f);

                f = 0;
                count = 0;

                Side opposite = Side.getSide(axis, -v);
                if (opposite != null && collidesOn(opposite)) {
                    for (Physics physics : collidingObjects.get(opposite)) {
                        if (physics.updatable && Math.signum(physics.getVelocity().get(axis)) == Math.signum(v)) {
                            f += physics.roughness.get(axis) * Math.abs(physics.getVelocity().get(axis) - v);
                            count++;
                        }
                    }
                }

                if (count > 0) f = ((f + roughness.get(axis)) / (count + 1)) * (stackedMass - mass) * deltaTime;
                output = output.set(axis, output.get(axis) + f);
            }
        }
        return output;
    }

    /**
     * Sum the masses of stacked objects
     *
     * @param velocity Input velocity
     * @param axis     Axis of motion
     * @return Sum of masses
     */
    private float calculateStackedMass(float velocity, Axis axis) {
        float totalMass = 0;
        Queue<Physics> current = new ArrayDeque<>();
        Set<Physics> visited = new HashSet<>();
        current.add(this);

        while (!current.isEmpty()) {
            Physics physics = current.poll();

            if (!visited.contains(physics)) {
                totalMass += physics.mass;
                Side side = Side.getSide(axis, -velocity);
                if (side != null) {
                    for (Physics colliding : physics.getCollidingObjects(side))
                        if (colliding.updatable && colliding.isKinematicOn(axis)) current.add(colliding);
                }
                visited.add(physics);
            }
        }
        return totalMass;
    }

    /**
     * Apply the effect of drag to the velocity
     */
    private void applyDrag() {
        Vector drag = calculateDrag();

        float vx = velocity.getX(), vy = velocity.getY(), vz = velocity.getZ();
        float dx = drag.getX(), dy = drag.getY(), dz = drag.getZ();

        if (isKinematicOn(Axis.X)) {
            if (vx < 0) vx = Math.min(vx + dx, 0);
            else if (vx > 0) vx = Math.max(vx - dx, 0);
        }
        if (isKinematicOn(Axis.Y)) {
            if (vy < 0) vy = Math.min(vy + dy, 0);
            else if (vy > 0) vy = Math.max(vy - dy, 0);
        }
        if (isKinematicOn(Axis.Z)) {
            if (vz < 0) vz = Math.min(vz + dz, 0);
            else if (vz > 0) vz = Math.max(vz - dz, 0);
        }

        velocity = new Vector(vx, vy, vz);
    }

    /**
     * Get the drag Vector based on velocity and dimensions
     *
     * @return Drag Vector
     */
    private Vector calculateDrag() {
        Vector output = new Vector();
        for (Axis axis : Axis.values()) {
            if (isKinematicOn(axis))
                output = output.set(axis, drag.get(axis) * getFaceArea(axis) * deltaTime * Math.abs(velocity.get(axis)));
        }
        return output;
    }

    /**
     * Apply the effects of momentum to the velocity
     */
    private void applyMomentum() {
        skipMomentum.clear();
        for (Axis axis : Axis.values()) {
            if (!isKinematicOn(axis)) continue;

            float v = velocity.get(axis);

            if (collidesOn(axis)) {
                Side side = Side.getSide(axis, v);
                if (side != null && collidesOn(side)) {
                    for (Physics physics : collidingObjects.get(side)) {

                        if (physics.updatable) {
                            if (physics.isKinematicOn(axis) && physics.isPushableOn(axis)) {

                                float sum = mass + physics.mass;
                                float diff = mass - physics.mass;
                                float v1 = v;
                                float v2 = physics.velocity.get(axis);
                                v = ((diff / sum) * v1) + ((2 * physics.mass / sum) * v2);
                                if (!physics.skipMomentum.contains(axis) || velocity.get(axis) != 0)
                                    physics.velocity =
                                            physics.velocity.set(axis, ((-diff / sum) * v2) + ((2 * mass / sum) * v1));

                            } else {
                                v = 0;
                                skipMomentum.add(axis);
                            }
                        }
                    }
                }
            }
            velocity = velocity.set(axis, v);
        }
    }

    /**
     * Update the position of the object based on the velocity
     */
    private void updatePosition() {
        for (Axis axis : Axis.values()) {
            if (isKinematicOn(axis))
                setPosition(position.set(axis, position.get(axis) + velocity.multiply(deltaTime).get(axis)));
        }
    }

    /**
     * Check if an object has collided with this object
     */
    public void tickCollisions() {
        if (!updatable || otherObjects == null) return;
        resetCollisions();
        for (Physics physics : otherObjects) {
            if (physics != this && physics.updatable && super.overlaps(physics)) {
                if (isCollideable()) {
                    collideWith(physics);
                } else {
                    overlapWith(physics);
                }
            }
        }
    }

    /**
     * Reset all collision data
     */
    private void resetCollisions() {
        collidingObjects.values().forEach(HashSet::clear);
        overlappingObjects.clear();
        specialCollisions.clear();
    }

    /**
     * Fix the position of this object to make a collision occur
     *
     * @param physics Object colliding with this object
     */
    private void collideWith(Physics physics) {
        float[] overlaps = new float[6];
        overlaps[0] = Math.abs(getMinimum().getX() - physics.getMaximum().getX()); //left overlap
        overlaps[1] = Math.abs(getMaximum().getX() - physics.getMinimum().getX()); //right overlap
        overlaps[2] = Math.abs(getMinimum().getY() - physics.getMaximum().getY()); //bottom overlap
        overlaps[3] = Math.abs(getMaximum().getY() - physics.getMinimum().getY()); //top overlap
        overlaps[4] = Math.abs(getMinimum().getZ() - physics.getMaximum().getZ()); //back overlap
        overlaps[5] = Math.abs(getMaximum().getZ() - physics.getMinimum().getZ()); //front overlap

        Axis axis = Axis.X;
        byte dir = -1;
        byte zeros = 0;
        float distance = overlaps[0];

        for (int i = 0; i < 6; i++) {
            if (overlaps[i] < distance) {
                distance = overlaps[i];
                dir = (byte) -Math.pow(-1, i);
                switch (i) {
                    case 0, 1 -> axis = Axis.X;
                    case 2, 3 -> axis = Axis.Y;
                    case 4, 5 -> axis = Axis.Z;
                }
            }
            if (overlaps[i] == 0) zeros++;
        }
        if (zeros > 1) return;

        Side side = Side.getSide(axis, dir);

        if (collidableSides.contains(side) && physics.collidableSides.contains(Side.getSide(axis, -dir))) {
            if (isKinematicOn(axis) && velocity.get(axis) * dir > 0) {
                if (Math.signum(velocity.get(axis)) == -Math.signum(physics.velocity.get(axis))
                        && !physics.specialCollisions.contains(this)) {
                    distance *= velocity.get(axis) / (velocity.get(axis) - physics.velocity.get(axis));
                    specialCollisions.add(physics);
                }
                setPosition(position.set(axis, position.get(axis) - (distance * dir)));
            }
            collidingObjects.get(side).add(physics);
        } else {
            overlapWith(physics);
        }
    }

    /**
     * Set this object as overlapping another
     *
     * @param physics Object to overlap with
     */
    private void overlapWith(Physics physics) {
        overlappingObjects.add(physics);
    }

    /**
     * Get the position of the object
     *
     * @return Position Vector of the object
     */
    public Vector getPosition() {
        return position;
    }

    /**
     * Set the position of the object
     *
     * @param position Position Vector
     * @return This Physics object
     */
    public Physics setPosition(Vector position) {
        this.position = position;
        super.setPosition(position);
        return this;
    }

    /**
     * Get the velocity of the object
     *
     * @return Velocity Vector of the object
     */
    public Vector getVelocity() {
        return velocity;
    }

    /**
     * Set the velocity of the object
     *
     * @param velocity Velocity Vector
     * @return This Physics object
     */
    public Physics setVelocity(Vector velocity) {
        this.velocity = velocity;
        return this;
    }

    /**
     * Get the acceleration of the object
     *
     * @return Acceleration Vector of the object
     */
    public Vector getAcceleration() {
        return acceleration;
    }

    /**
     * Set the acceleration of the object
     *
     * @param acceleration Acceleration Vector
     * @return This Physics object
     */
    public Physics setAcceleration(Vector acceleration) {
        this.acceleration = acceleration;
        return this;
    }

    /**
     * Get the gravity applied to the object
     *
     * @return Gravity Vector
     */
    public Vector getGravity() {
        return gravity;
    }

    /**
     * Set the gravity applied to the object
     *
     * @param gravity Gravity Vector
     * @return This Physics object
     */
    public Physics setGravity(Vector gravity) {
        this.gravity = gravity;
        return this;
    }

    /**
     * Get the terminal velocity for this object
     *
     * @return Terminal velocity Vector
     */
    public Vector getTerminalVelocity() {
        return terminalVelocity;
    }

    /**
     * Set the terminal velocity for this object
     *
     * @param terminalVelocity Terminal velocity Vector
     * @return This Physics object
     */
    public Physics setTerminalVelocity(Vector terminalVelocity) {
        this.terminalVelocity = terminalVelocity;
        return this;
    }

    /**
     * Get the drag coefficient of the object
     *
     * @return Drag coefficient Vector of the object
     */
    public Vector getDrag() {
        return drag;
    }

    /**
     * Set the drag coefficient for the object
     *
     * @param drag Drag coefficient Vector
     * @return This Physics object
     */
    public Physics setDrag(Vector drag) {
        this.drag = drag;
        return this;
    }

    /**
     * Get the roughness of the object
     *
     * @return Roughness per axis as a Vector
     */
    public Vector getRoughness() {
        return roughness;
    }

    /**
     * Set the roughness of the object
     *
     * @param roughness Roughness per axis as a Vector
     * @return This Physics object
     */
    public Physics setRoughness(Vector roughness) {
        this.roughness = roughness;
        return this;
    }

    /**
     * Check if an object can be collided with on any side
     *
     * @return True if able to be collided with on any side
     */
    public boolean isCollideable() {
        return !collidableSides.isEmpty();
    }

    /**
     * Check if an object can be collided with on a specific Side
     *
     * @return True if able to be collided with on a specific Side
     */
    public boolean isCollideableOn(Side side) {
        return collidableSides.contains(side);
    }

    /**
     * Check if an object can be collided with on a specific Axis
     *
     * @return True if able to be collided with on a specific Axis
     */
    public boolean isCollideableOn(Axis axis) {
        return switch (axis) {
            case X -> isCollideableOn(Side.LEFT) || isCollideableOn(Side.RIGHT);
            case Y -> isCollideableOn(Side.BOTTOM) || isCollideableOn(Side.TOP);
            case Z -> isCollideableOn(Side.BACK) || isCollideableOn(Side.FRONT);
        };
    }

    /**
     * Check if an object can be collided with on all sides
     *
     * @return True if able to be collided with on all sides
     */
    public boolean isFullyCollideable() {
        return collidableSides.size() == 6;
    }

    /**
     * Get the Sides the object can collide on
     *
     * @return HashSet of Sides the Object can collide on
     */
    public HashSet<Side> getCollideableSides() {
        return collidableSides;
    }

    /**
     * Set the object to be collideable on all sides or no sides
     *
     * @param collideable True to allow colliding on all sides, false for no sides
     * @return This Physics object
     */
    public Physics setFullyCollideable(boolean collideable) {
        collidableSides.clear();
        if (collideable) collidableSides.addAll(Arrays.asList(Side.values()));
        return this;
    }

    /**
     * Set the object to be collideable per Side
     *
     * @param sides Variable amount of Sides
     * @return This Physics object
     */
    public Physics setCollidableOn(Side... sides) {
        collidableSides.clear();
        collidableSides.addAll(Arrays.asList(sides));
        return this;
    }

    /**
     * Set the object to be collideable per Axis
     *
     * @param axes Variable amount of Axes
     * @return This Physics object
     */
    public Physics setCollidableOn(Axis... axes) {
        collidableSides.clear();
        for (Axis axis : axes) {
            collidableSides.add(Side.getSide(axis, 1));
            collidableSides.add(Side.getSide(axis, -1));
        }
        return this;
    }

    /**
     * Set the object to be collideable per Side
     *
     * @param sides HashSet of Sides
     * @return This Physics object
     */
    public Physics setCollidableSides(HashSet<Side> sides) {
        collidableSides.clear();
        collidableSides.addAll(sides);
        return this;
    }

    /**
     * Check if the object is currently colliding with another object
     *
     * @return True if the Object is colliding with another object
     */
    public boolean isColliding() {
        for (Side side : Side.values()) {
            if (collidesOn(side)) return true;
        }
        return false;
    }

    /**
     * Check if an object collides with this object
     *
     * @param physics Object to check if colliding with
     * @return True if this object collides with the other object
     */
    public boolean collidesWith(Physics physics) {
        for (HashSet<Physics> list : collidingObjects.values()) {
            if (list.contains(physics)) return true;
        }
        return false;
    }

    /**
     * Check if this object is colliding on the specified Side
     *
     * @param side Side of the object
     * @return True if the Object is colliding on the Side
     */
    public boolean collidesOn(Side side) {
        return !collidingObjects.get(side).isEmpty();
    }

    /**
     * Check if this object is colliding on the specified Sides perpendicular to the Axis
     *
     * @param axis Axis of collision
     * @return True if the object is colliding on the Axis
     */
    public boolean collidesOn(Axis axis) {
        return switch (axis) {
            case X -> collidesOn(Side.LEFT) || collidesOn(Side.RIGHT);
            case Y -> collidesOn(Side.BOTTOM) || collidesOn(Side.TOP);
            case Z -> collidesOn(Side.BACK) || collidesOn(Side.FRONT);
        };
    }

    /**
     * Check if an object collides with this one on a specific Side
     *
     * @param physics Object to check if colliding with
     * @param side    Side of object
     * @return True if the object is colliding with the other object on the specified Side
     */
    public boolean collidesWithOn(Physics physics, Side side) {
        return collidingObjects.get(side).contains(physics);
    }

    /**
     * Check if an object collides with this one on a specific Axis
     *
     * @param physics Object to check if colliding with
     * @param axis    Axis of the grid
     * @return True if the object is colliding with the other object on the specified Axis
     */
    public boolean collidesWithOn(Physics physics, Axis axis) {
        return switch (axis) {
            case X -> collidesWithOn(physics, Side.LEFT) || collidesWithOn(physics, Side.RIGHT);
            case Y -> collidesWithOn(physics, Side.BOTTOM) || collidesWithOn(physics, Side.TOP);
            case Z -> collidesWithOn(physics, Side.BACK) || collidesWithOn(physics, Side.FRONT);
        };
    }

    /**
     * Get all objects colliding on the specified Side
     *
     * @param side Side to get colliding objects of
     * @return Set of all objects colliding on the Side
     */
    public HashSet<Physics> getCollidingObjects(Side side) {
        return collidingObjects.get(side);
    }

    /**
     * Get all objects colliding on the specified Axis
     *
     * @param axis Axis to get colliding objects of
     * @return Set of all objects colliding on the Axis
     */
    public HashSet<Physics> getCollidingObjects(Axis axis) {
        HashSet<Physics> colliding = new HashSet<>();
        switch (axis) {
            case X -> {
                colliding.addAll(getCollidingObjects(Side.LEFT));
                colliding.addAll(getCollidingObjects(Side.RIGHT));
            }
            case Y -> {
                colliding.addAll(getCollidingObjects(Side.BOTTOM));
                colliding.addAll(getCollidingObjects(Side.TOP));
            }
            case Z -> {
                colliding.addAll(getCollidingObjects(Side.BACK));
                colliding.addAll(getCollidingObjects(Side.FRONT));
            }
        }
        return colliding;
    }

    /**
     * Get a Set of all objects colliding with this one
     *
     * @return Set of all colliding objects
     */
    public HashSet<Physics> getCollidingObjects() {
        HashSet<Physics> allObjects = new HashSet<>();
        for (Side side : Side.values()) allObjects.addAll(collidingObjects.get(side));
        return allObjects;
    }

    /**
     * Get all the Sides the object is colliding on
     *
     * @return Set of all colliding Sides
     */
    public HashSet<Side> getCollidingSides() {
        HashSet<Side> sides = new HashSet<>();
        for (Side side : Side.values()) if (collidesOn(side)) sides.add(side);
        return sides;
    }

    /**
     * Get all the axes the object is colliding on
     *
     * @return Set of all colliding axes
     */
    public HashSet<Axis> getCollidingAxes() {
        HashSet<Axis> axes = new HashSet<>();
        for (Axis axis : Axis.values()) if (collidesOn(axis)) axes.add(axis);
        return axes;
    }

    /**
     * See if this object is overlapping any object
     *
     * @return True if overlapping
     */
    public boolean isOverlapping() {
        return !overlappingObjects.isEmpty();
    }

    /**
     * Check if this object overlaps another
     *
     * @param physics Object to check with
     * @return True if this Object overlaps the specified Object
     */
    public boolean overlaps(Physics physics) {
        return overlappingObjects.contains(physics);
    }

    /**
     * Get the set of all objects overlapping this one
     *
     * @return Set of overlapping objects
     */
    public HashSet<Physics> getOverlappingObjects() {
        return overlappingObjects;
    }

    /**
     * Check if the object is kinematic on any axis
     *
     * @return True if the object is kinematic on any axis
     */
    public boolean isKinematic() {
        return !kinematicAxes.isEmpty();
    }

    /**
     * Check if the object is kinematic on every axis
     *
     * @return True if the object is kinematic on any axis
     */
    public boolean isFullyKinematic() {
        return kinematicAxes.size() == 3;
    }

    /**
     * Check if the object is kinematic on the specified Axis
     *
     * @return True if the object is kinematic on the specified Axis
     */
    public boolean isKinematicOn(Axis axis) {
        return kinematicAxes.contains(axis);
    }

    /**
     * Get the Axes the object is kinematic on
     *
     * @return HashSet of Axes the object is kinematic on
     */
    public HashSet<Axis> getKinematicAxes() {
        return kinematicAxes;
    }

    /**
     * Set whether the object can move or not per Axis
     *
     * @param axes Axes to set kinematic
     * @return This Physics object
     */
    public Physics setKinematicOn(Axis... axes) {
        kinematicAxes.clear();
        kinematicAxes.addAll(Arrays.asList(axes));
        return this;
    }

    /**
     * Set whether the object can move or not for all axes
     *
     * @param kinematic True to allow motion on all axes
     * @return This Physics object
     */
    public Physics setFullyKinematic(boolean kinematic) {
        kinematicAxes.clear();
        if (kinematic) kinematicAxes.addAll(Arrays.asList(Axis.values()));
        return this;
    }

    /**
     * Set the kinematic axes for the object
     *
     * @param axes HashSet of Axes to set kinematic
     * @return This Physics object
     */
    public Physics setKinematicAxes(HashSet<Axis> axes) {
        kinematicAxes.clear();
        kinematicAxes.addAll(axes);
        return this;
    }

    /**
     * Check if the object is pushable on any axis
     *
     * @return True if the object is pushable on any axis
     */
    public boolean isPushable() {
        return !pushableAxes.isEmpty();
    }

    /**
     * Check if the object is pushable on every axis
     *
     * @return True if the object is pushable on every axis
     */
    public boolean isFullyPushable() {
        return pushableAxes.size() == 3;
    }

    /**
     * Check if the object is pushable on the specified Axis
     *
     * @return True if the object is pushable on the specified Axis
     */
    public boolean isPushableOn(Axis axis) {
        return pushableAxes.contains(axis);
    }

    /**
     * Get the Axes the object is pushable on
     *
     * @return HashSet of Axes the object is pushable on
     */
    public HashSet<Axis> getPushableAxes() {
        return pushableAxes;
    }

    /**
     * Set whether the object can be pushed or not per Axis
     *
     * @param axes Axes to set pushable
     * @return This Physics object
     */
    public Physics setPushableOn(Axis... axes) {
        pushableAxes.clear();
        pushableAxes.addAll(Arrays.asList(axes));
        return this;
    }

    /**
     * Set whether the object can be pushed or not for all axes
     *
     * @param pushable True to allow being pushed on all axes
     * @return This Physics object
     */
    public Physics setFullyPushable(boolean pushable) {
        pushableAxes.clear();
        if (pushable) pushableAxes.addAll(Arrays.asList(Axis.values()));
        return this;
    }

    /**
     * Set the pushable axes for the object
     *
     * @param axes HashSet of Axes to set pushable
     * @return This Physics object
     */
    public Physics setPushableAxes(HashSet<Axis> axes) {
        pushableAxes.clear();
        pushableAxes.addAll(axes);
        return this;
    }

    /**
     * Get the mass of the object
     *
     * @return Mass of the object
     */
    public float getMass() {
        return mass;
    }

    /**
     * Set the mass of the object
     *
     * @param mass New mass for the object
     * @return This Physics object
     */
    public Physics setMass(float mass) {
        this.mass = mass;
        return this;
    }

    /**
     * Set the object this object is in a Scene with
     * <p>
     * This is only able to be called by the parent class
     *
     * @param objects List of Physics objects
     */
    @SuppressWarnings("unchecked")
    protected void setOtherObjects(LinkedList<? extends Physics> objects) {
        this.otherObjects = (LinkedList<Physics>) objects;
    }

    /**
     * Set if the object can update
     * <p>
     * This is only able to be called by the parent class
     *
     * @param updatable True to allow updating
     */
    protected void setUpdatable(boolean updatable) {
        this.updatable = updatable;
    }

    /**
     * Check if the object can update
     * <p>
     * This is only able to be called by the parent class
     *
     * @return True if Object can update
     */
    protected boolean isUpdatable() {
        return updatable;
    }

    /**
     * Check if another set of Physics data is equal to this one
     *
     * @param o Physics to check for equality
     * @return True if other Physics data is equivalent to this
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Physics physics = (Physics) o;
        return Float.compare(physics.mass, mass) == 0 &&
                updatable == physics.updatable &&
                Objects.equals(gravity, physics.gravity) &&
                Objects.equals(position, physics.position) &&
                Objects.equals(velocity, physics.velocity) &&
                Objects.equals(terminalVelocity, physics.terminalVelocity) &&
                Objects.equals(acceleration, physics.acceleration) &&
                Objects.equals(drag, physics.drag) &&
                Objects.equals(roughness, physics.roughness) &&
                Objects.equals(collidingObjects, physics.collidingObjects) &&
                Objects.equals(overlappingObjects, physics.overlappingObjects) &&
                Objects.equals(kinematicAxes, physics.kinematicAxes) &&
                Objects.equals(pushableAxes, physics.pushableAxes) &&
                Objects.equals(collidableSides, physics.collidableSides);
    }
}

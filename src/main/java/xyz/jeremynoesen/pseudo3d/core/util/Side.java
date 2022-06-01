package xyz.jeremynoesen.pseudo3d.core.util;

/**
 * Sides of the Box
 *
 * @author Jeremy Noesen
 */
public enum Side {
    LEFT, RIGHT, BOTTOM, TOP, BACK, FRONT;

    /**
     * Get a Side whose normal has specified direction on an Axis
     *
     * @param axis Axis of normal
     * @param dir  Direction of normal
     * @return Side based on normal direction and Axis
     */
    public static Side getFromNormal(Axis axis, float dir) {
        if (dir == 0) return null;
        return switch (axis) {
            case X -> dir < 0 ? LEFT : RIGHT;
            case Y -> dir < 0 ? BOTTOM : TOP;
            case Z -> dir < 0 ? BACK : FRONT;
        };
    }

    /**
     * Get the Side opposite of the specified Side of a box
     *
     * @param side Side to get the opposite of
     * @return Opposoite Side
     */
    public static Side getOpposite(Side side) {
        return switch (side) {
            case LEFT -> RIGHT;
            case RIGHT -> LEFT;
            case BOTTOM -> TOP;
            case TOP -> BOTTOM;
            case BACK -> FRONT;
            case FRONT -> BACK;
        };
    }

    /**
     * Get the Vector normal to a side
     *
     * @param side Side to get normal of
     * @return Normal Vector
     */
    public static Vector getNormalVector(Side side) {
        return switch (side) {
            case LEFT -> new Vector(-1, 0, 0);
            case RIGHT -> new Vector(1, 0, 0);
            case BOTTOM -> new Vector(0, -1, 0);
            case TOP -> new Vector(0, 1, 0);
            case BACK -> new Vector(0, 0, -1);
            case FRONT -> new Vector(0, 0, 1);
        };
    }

    /**
     * Get Axis of normal for a Side
     *
     * @param side Side of object
     * @return Axis of normal for the Side
     */
    public static Axis getNormalAxis(Side side) {
        return switch (side) {
            case LEFT, RIGHT -> Axis.X;
            case BOTTOM, TOP -> Axis.Y;
            case BACK, FRONT -> Axis.Z;
        };
    }
}

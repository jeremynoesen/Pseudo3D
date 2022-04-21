package xyz.jeremynoesen.pseudo3d.scene.util;

/**
 * Sides of the Box
 *
 * @author Jeremy Noesen
 */
public enum Side {
    LEFT, RIGHT, TOP, BOTTOM, BACK, FRONT;

    /**
     * Get a Side whose normal has specified direction on an Axis
     *
     * @param axis Axis of normal
     * @param dir  Direction of normal
     * @return Side based on normal direction and Axis
     */
    public static Side getSide(Axis axis, float dir) {
        if (dir == 0) return null;
        return switch (axis) {
            case X -> dir < 0 ? Side.LEFT : Side.RIGHT;
            case Y -> dir < 0 ? Side.BOTTOM : Side.TOP;
            case Z -> dir < 0 ? Side.BACK : Side.FRONT;
        };
    }
}

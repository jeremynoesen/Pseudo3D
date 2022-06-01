package xyz.jeremynoesen.pseudo3d.core.util;

/**
 * Box and related mathematical operators
 *
 * @author Jeremy Noesen
 */
public class Box {

    /**
     * Width of the Box
     */
    private float width;

    /**
     * Height of the Box
     */
    private float height;

    /**
     * Depth of the Box
     */
    private float depth;

    /**
     * Minimum point of the Box
     */
    private Vector min;

    /**
     * Maximum point of the Box
     */
    private Vector max;

    /**
     * Position of the center of the Box
     */
    private Vector position;

    /**
     * Create a new default Box
     */
    public Box() {
        width = 0;
        height = 0;
        depth = 0;
        position = new Vector();
        min = new Vector();
        max = new Vector();
    }

    /**
     * Create a new 3D Box with specified width, height, depth, and location
     *
     * @param width    Width of the Box
     * @param height   Height of the Box
     * @param depth    Depth of the Box
     * @param position Location of the Box's center
     */
    public Box(float width, float height, float depth, Vector position) {
        this.width = Math.abs(width);
        this.height = Math.abs(height);
        this.depth = Math.abs(depth);
        this.position = position;
        min = new Vector(position.getX() - (width / 2.0f),
                position.getY() - (height / 2.0f), position.getZ() - (depth / 2.0f));
        max = new Vector(position.getX() + (width / 2.0f),
                position.getY() + (height / 2.0f), position.getZ() + (depth / 2.0f));
    }

    /**
     * Create a new 2D Box with specified width, height, and location
     *
     * @param width    Width of the Box
     * @param height   Height of the Box
     * @param position Location of the Box's center
     */
    public Box(float width, float height, Vector position) {
        this.width = Math.abs(width);
        this.height = Math.abs(height);
        this.depth = 0;
        this.position = position;
        min = new Vector(position.getX() - (width / 2.0f),
                position.getY() - (height / 2.0f), position.getZ() - (depth / 2.0f));
        max = new Vector(position.getX() + (width / 2.0f),
                position.getY() + (height / 2.0f), position.getZ() + (depth / 2.0f));
    }

    /**
     * Copy constructor for Box
     *
     * @param box Box to copy
     */
    public Box(Box box) {
        this.width = box.width;
        this.height = box.height;
        this.depth = box.depth;
        this.position = box.position;
        this.min = box.min;
        this.max = box.max;
    }

    /**
     * Set the dimensions of the Box
     *
     * @param dimensions Dimensions of the Box
     * @return This Box
     */
    public Box setDimensions(Vector dimensions) {
        setWidth(dimensions.getX());
        setHeight(dimensions.getY());
        setDepth(dimensions.getZ());
        return this;
    }

    /**
     * Set the dimensions of the Box for a specific Axis
     *
     * @param axis      Axis to set dimension for
     * @param dimension Dimension in grid units
     * @return This Box
     */
    public Box setDimensions(Axis axis, float dimension) {
        switch (axis) {
            case X -> setWidth(dimension);
            case Y -> setHeight(dimension);
            case Z -> setDepth(dimension);
        }
        return this;
    }

    /**
     * Get the dimensions of the Box for a specific Axis
     *
     * @param axis Axis to get dimension for
     * @return Dimension of the specified Axis
     */
    public float getDimensions(Axis axis) {
        return switch (axis) {
            case X -> getWidth();
            case Y -> getHeight();
            case Z -> getDepth();
        };
    }

    /**
     * Get the width of the Box
     *
     * @return Width of the Box
     */
    public float getWidth() {
        return width;
    }

    /**
     * Set a new width of the Box and recalculate its min and max in the x axis
     *
     * @param width New width of Box
     * @return This Box
     */
    public Box setWidth(float width) {
        this.width = Math.abs(width);
        max = max.setX(position.getX() + (width / 2.0f));
        min = min.setX(position.getX() - (width / 2.0f));
        return this;
    }

    /**
     * Get the height of the Box
     *
     * @return Height of the Box
     */
    public float getHeight() {
        return height;
    }

    /**
     * Set a new height of the Box and recalculate its min and max in the y axis
     *
     * @param height New height of the Box
     * @return This Box
     */
    public Box setHeight(float height) {
        this.height = Math.abs(height);
        max = max.setY(position.getY() + (height / 2.0f));
        min = min.setY(position.getY() - (height / 2.0f));
        return this;
    }

    /**
     * Get the depth of the Box
     *
     * @return Depth of Box
     */
    public float getDepth() {
        return depth;
    }

    /**
     * Set a new depth of the Box and recalculate its min and max in the z axis
     *
     * @param depth New depth of the Box
     * @return This Box
     */
    public Box setDepth(float depth) {
        this.depth = depth;
        max = max.setZ(position.getZ() + (depth / 2.0f));
        min = min.setZ(position.getZ() - (depth / 2.0f));
        return this;
    }

    /**
     * Get the position of the Box
     *
     * @return Box position
     */
    public Vector getPosition() {
        return position;
    }

    /**
     * Set the position of the Box and recalculate its min and max
     *
     * @param position New position
     * @return This Box
     */
    public Box setPosition(Vector position) {
        this.position = position;
        max = new Vector(position.getX() + (width / 2.0f),
                position.getY() + (height / 2.0f), position.getZ() + (depth / 2.0f));
        min = new Vector(position.getX() - (width / 2.0f),
                position.getY() - (height / 2.0f), position.getZ() - (depth / 2.0f));
        return this;
    }

    /**
     * Get the minimum of the Box
     *
     * @return Minimum location
     */
    public Vector getMinimum() {
        return min;
    }

    /**
     * Get the maximum of the Box
     *
     * @return Maximum location
     */
    public Vector getMaximum() {
        return max;
    }

    /**
     * Check if Boxes overlap this Box
     *
     * @param box Boxes to check for overlap
     * @return True if the Boxes overlap this Box
     */
    public boolean overlaps(Box... box) {
        for (Box b : box) {
            if (!(min.getX() <= b.getMaximum().getX() && max.getX() >= b.getMinimum().getX() &&
                    min.getY() <= b.getMaximum().getY() && max.getY() >= b.getMinimum().getY() &&
                    min.getZ() <= b.getMaximum().getZ() && max.getZ() >= b.getMinimum().getZ())) return false;
        }
        return true;
    }

    /**
     * Check if Boxes are inside this Box
     *
     * @param box Boxes to check
     * @return True if the Boxes are inside this Box
     */
    public boolean contains(Box... box) {
        for (Box b : box) {
            if (!(min.getX() <= b.getMinimum().getX() && max.getX() >= b.getMaximum().getX() &&
                    min.getY() <= b.getMinimum().getY() && max.getY() >= b.getMaximum().getY() &&
                    min.getZ() <= b.getMinimum().getZ() && max.getZ() >= b.getMaximum().getZ())) return false;
        }
        return true;
    }

    /**
     * Check if this Box contains positions
     *
     * @param position Positions to check
     * @return True of the Box contains these positions
     */
    public boolean contains(Vector... position) {
        for (Vector v : position) {
            if (!(min.getX() <= v.getX() && max.getX() >= v.getX() &&
                    min.getY() <= v.getY() && max.getY() >= v.getY() &&
                    min.getZ() <= v.getZ() && max.getZ() >= v.getZ())) return false;
        }
        return true;
    }

    /**
     * Get the volume of the Box
     *
     * @return Volume of the Box
     */
    public float getVolume() {
        return width * height * depth;
    }

    /**
     * Get the surface area of the Box
     *
     * @return Surface area of the Box
     */
    public float getSurfaceArea() {
        return (2 * width * height) + (2 * height * depth) + (2 * width * depth);
    }

    /**
     * Get the face area for a specified Side
     *
     * @param side Side to get face area for
     * @return Face area of the Side
     */
    public float getFaceArea(Side side) {
        return switch (side) {
            case LEFT, RIGHT -> height * depth;
            case BOTTOM, TOP -> width * depth;
            case BACK, FRONT -> width * height;
        };
    }

    /**
     * Get the Box represented as a formatted String
     *
     * @return Box in string format
     */
    @Override
    public String toString() {
        return "[" + width + ", " + height + ", " + depth + ", " + position.toString() + "]";
    }

    /**
     * Check if another Box is equal to this Box
     *
     * @param box Box to check for equality
     * @return True if the two Boxes are equal
     */
    @Override
    public boolean equals(Object box) {
        if (this == box) return true;
        if (box == null || getClass() != box.getClass()) return false;
        Box that = (Box) box;
        return Float.compare(that.width, width) == 0 &&
                Float.compare(that.height, height) == 0 &&
                Float.compare(that.depth, depth) == 0 &&
                that.position.equals(position);
    }
}

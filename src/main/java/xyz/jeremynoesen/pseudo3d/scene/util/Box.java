package xyz.jeremynoesen.pseudo3d.scene.util;

/**
 * simple box with some checks and mathematical operators
 *
 * @author Jeremy Noesen
 */
public class Box {
    
    /**
     * sides of box
     */
    public enum Side {LEFT, RIGHT, TOP, BOTTOM, BACK, FRONT}
    
    /**
     * width of the box
     */
    private float width;
    
    /**
     * height of the box
     */
    private float height;
    
    /**
     * depth of the box
     */
    private float depth;
    
    /**
     * minimum point of box
     */
    private Vector min;
    
    /**
     * maximum point of box
     */
    private Vector max;
    
    /**
     * position of the center of the box
     */
    private Vector position;
    
    /**
     * creates a new box with default 0 values
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
     * create a new 3D box at location with specified width and height and depth
     *
     * @param width    width of box
     * @param height   height of box
     * @param depth    depth of box
     * @param position location of the box's center
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
     * create a new 2D box at location with specified width and height
     *
     * @param width    width of box
     * @param height   height of box
     * @param position location of the box's center
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
     * copy constructor for box
     *
     * @param box box to copy
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
     * set the dimensions of the box
     *
     * @param width  width of box
     * @param height height of box
     * @param depth  depth of box
     */
    public Box setDimensions(float width, float height, float depth) {
        setWidth(width);
        setHeight(height);
        setDepth(depth);
        return this;
    }

    /**
     * set the dimensions of the box
     *
     * @param width  width of box
     * @param height height of box
     */
    public Box setDimensions(float width, float height) {
        setWidth(width);
        setHeight(height);
        setDepth(0);
        return this;
    }
    
    /**
     * get the width of the box
     *
     * @return width of box
     */
    public float getWidth() {
        return width;
    }
    
    /**
     * set a new width of the box and recalculate its actual min and max coords in the x axis
     *
     * @param width width of box
     */
    public Box setWidth(float width) {
        this.width = Math.abs(width);
        max = max.setX(position.getX() + (width / 2.0f));
        min = min.setX(position.getX() - (width / 2.0f));
        return this;
    }
    
    /**
     * get the height of the box
     *
     * @return height of box
     */
    public float getHeight() {
        return height;
    }
    
    /**
     * set the height of the box and recalculate its actual min and max coords in the y axis
     *
     * @param height height of box
     */
    public Box setHeight(float height) {
        this.height = Math.abs(height);
        max = max.setY(position.getY() + (height / 2.0f));
        min = min.setY(position.getY() - (height / 2.0f));
        return this;
    }
    
    /**
     * get the depth of the box
     *
     * @return depth of box
     */
    public float getDepth() {
        return depth;
    }
    
    /**
     * set a new depth of the box and recalculate its actual min and max coords in the z axis
     *
     * @param depth depth of box
     */
    public Box setDepth(float depth) {
        this.depth = depth;
        max = max.setZ(position.getZ() + (depth / 2.0f));
        min = min.setZ(position.getZ() - (depth / 2.0f));
        return this;
    }
    
    /**
     * get a copy of the position vector for the box
     *
     * @return copy of position
     */
    public Vector getPosition() {
        return position;
    }
    
    /**
     * set the position of the box and recalculate its actual min and max coords
     *
     * @param position new position
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
     * get the minimum location of the box
     *
     * @return minimum location
     */
    public Vector getMinimum() {
        return min;
    }
    
    /**
     * get the maximum location of the box
     *
     * @return maximum location
     */
    public Vector getMaximum() {
        return max;
    }
    
    /**
     * checks if a box overlaps this box
     *
     * @param box box to check for overlaps
     * @return true if the box overlaps this box
     */
    public boolean overlaps(Box box) {
        return min.getX() <= box.getMaximum().getX() && max.getX() >= box.getMinimum().getX() &&
                min.getY() <= box.getMaximum().getY() && max.getY() >= box.getMinimum().getY() &&
                min.getZ() <= box.getMaximum().getZ() && max.getZ() >= box.getMinimum().getZ();
    }
    
    /**
     * check if a box is inside this box
     *
     * @param box box to check
     * @return true if the box is inside this box
     */
    public boolean contains(Box box) {
        return min.getX() <= box.getMinimum().getX() && max.getX() >= box.getMaximum().getX() &&
                min.getY() <= box.getMinimum().getY() && max.getY() >= box.getMaximum().getY() &&
                min.getZ() <= box.getMinimum().getZ() && max.getZ() >= box.getMaximum().getZ();
    }
    
    /**
     * check if this box contains a position
     *
     * @param position position to check
     * @return true of the box contains this position
     */
    public boolean contains(Vector position) {
        return min.getX() <= position.getX() && max.getX() >= position.getX() &&
                min.getY() <= position.getY() && max.getY() >= position.getY() &&
                min.getZ() <= position.getZ() && max.getZ() >= position.getZ();
    }
    
    /**
     * get the volume of the box
     *
     * @return volume of the box
     */
    public float getVolume() {
        return width * height * depth;
    }
    
    /**
     * get the surface area of the box
     *
     * @return surface area of box
     */
    public float getSurfaceArea() {
        return (2 * width * height) + (2 * height * depth) + (2 * width * depth);
    }
    
    /**
     * put the box width, height, depth, and position into a string
     *
     * @return box in string format
     */
    @Override
    public String toString() {
        return "[" + width + ", " + height + ", " + depth + ", " + position.toString() + "]";
    }
    
    /**
     * check if another box has the same width, height, depth, and position as this box
     *
     * @param box box to check for equality
     * @return true if the two boxes are equal
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

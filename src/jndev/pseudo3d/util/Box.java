package jndev.pseudo3d.util;

/**
 * simple 3-dimensional box
 *
 * @author JNDev (Jeremaster101)
 */
public class Box {
    
    /**
     * width of the box
     */
    private double width;
    
    /**
     * height of the box
     */
    private double height;
    
    /**
     * depth of the box
     */
    private double depth;
    
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
        position = new Vector(0, 0, 0);
        min = new Vector(0, 0, 0);
        max = new Vector(0, 0, 0);
    }
    
    /**
     * create a new box at location with specified width and height
     *
     * @param width    width of box
     * @param height   height of box
     * @param depth    depth of box
     * @param position location of the box's center
     */
    public Box(double width, double height, double depth, Vector position) {
        this.width = Math.abs(width);
        this.height = Math.abs(height);
        this.depth = Math.abs(depth);
        this.position = position;
        min = new Vector(position.getX() - (width / 2.0), position.getY() - (height / 2.0), position.getZ() - (depth / 2.0));
        max = new Vector(position.getX() + (width / 2.0), position.getY() + (height / 2.0), position.getZ() + (depth / 2.0));
    }
    
    /**
     * copy constructor for box
     *
     * @param box box to copy
     */
    public Box(Box box) {
        this.width = box.getWidth();
        this.height = box.getHeight();
        this.depth = box.getDepth();
        this.position = box.getPosition();
        this.min = box.getMinimum();
        this.max = box.getMaximum();
    }
    
    /**
     * get the height of the box
     *
     * @return heigh of box
     */
    public double getHeight() {
        return height;
    }
    
    /**
     * set the height of the box and recalculate its actual min and max coords in the y axis
     *
     * @param height height of box
     */
    public void setHeight(double height) {
        this.height = Math.abs(height);
        max = max.setY(position.getY() + (height / 2.0));
        min = min.setY(position.getY() - (height / 2.0));
    }
    
    /**
     * get the width of the box
     *
     * @return width ofbox
     */
    public double getWidth() {
        return width;
    }
    
    /**
     * set a new width of the box and recalculate its actual min and max coords in the x axis
     *
     * @param width width of box
     */
    public void setWidth(double width) {
        this.width = Math.abs(width);
        max = max.setX(position.getX() + (width / 2.0));
        min = min.setX(position.getX() - (width / 2.0));
    }
    
    /**
     * get the depth of the box
     *
     * @return depth ofbox
     */
    public double getDepth() {
        return depth;
    }
    
    /**
     * set a new depth of the box and recalculate its actual min and max coords in the z axis
     *
     * @param depth depth of box
     */
    public void setDepth(double depth) {
        this.depth = depth;
        max = max.setZ(position.getZ() + (depth / 2.0));
        min = min.setZ(position.getZ() - (depth / 2.0));
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
    public void setPosition(Vector position) {
        this.position = position;
        max = new Vector(position.getX() + (width / 2.0), position.getY() + (height / 2.0), position.getZ() + (depth / 2.0));
        min = new Vector(position.getX() - (width / 2.0), position.getY() - (height / 2.0), position.getZ() - (depth / 2.0));
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
        return min.getX() < box.getMaximum().getX() && max.getX() > box.getMinimum().getX() &&
                min.getY() < box.getMaximum().getY() && max.getY() > box.getMinimum().getY() &&
                min.getZ() < box.getMaximum().getZ() && max.getZ() > box.getMinimum().getZ();
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
    public double getVolume() {
        return width * height * depth;
    }
    
    /**
     * get the surface area of the box
     *
     * @return surface area of box
     */
    public double getSurfaceArea() {
        return (2 * width * height) + (2 * height * depth) + (2 * width * depth);
    }
    
    /**
     * calculate the length of a segment from one corner of the box to the opposite corner
     *
     * @return length of diagonal in box
     */
    public double getDiagonal() {
        return min.distance(max);
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
        Box b = (Box) box;
        return Double.compare(b.getWidth(), width) == 0 &&
                Double.compare(b.getHeight(), height) == 0 &&
                Double.compare(b.getDepth(), depth) == 0 &&
                b.getPosition().equals(position);
    }
}

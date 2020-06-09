package jndev.pseudo3d.util;

import java.util.Objects;

/**
 * 3D box, used for bounding boxes and other operations
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
     * distance of overlap, 0 if not overlapping
     */
    private double overlapDistance;
    
    /**
     * overlapped side NONE if not overlapping
     */
    private Side overlapSide;
    
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
        overlapDistance = 0;
        overlapSide = Side.NONE;
        min = new Vector(0, 0, 0);
        max = new Vector(0, 0, 0);
    }
    
    /**
     * create a new box at location with specified width and height. Overlapped data is set to default values. the
     * actual locations of the min and max points of the bounds of the box are calculated as well.
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
        overlapDistance = 0;
        overlapSide = Side.NONE;
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
        this.overlapDistance = box.getOverlappingDistance();
        this.overlapSide = box.getOverlappingSide();
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
     * checks if a box overlaps this box. If they do, the distance of the overlap is calculated and the side in which
     * the overlap occurs is noted
     *
     * @param box box to check for overlaps
     * @return true if the box overlaps this box
     */
    public boolean overlaps(Box box) {
        
        if (min.getX() <= box.getMaximum().getX() && max.getX() >= box.getMinimum().getX() &&
                min.getY() <= box.getMaximum().getY() && max.getY() >= box.getMinimum().getY() &&
                min.getZ() <= box.getMaximum().getZ() && max.getZ() >= box.getMinimum().getZ()) {
            double left = Math.abs(min.getX() - box.getMaximum().getX());
            double right = Math.abs(max.getX() - box.getMinimum().getX());
            double bottom = Math.abs(min.getY() - box.getMaximum().getY());
            double top = Math.abs(max.getY() - box.getMinimum().getY());
            double back = Math.abs(min.getZ() - box.getMaximum().getZ());
            double front = Math.abs(max.getZ() - box.getMinimum().getZ());
            overlapDistance = Math.min(Math.min(Math.min(left, right), Math.min(top, bottom)), Math.min(front, back));
            if (overlapDistance == left) {
                overlapSide = Side.LEFT;
            } else if (overlapDistance == right) {
                overlapSide = Side.RIGHT;
            } else if (overlapDistance == top) {
                overlapSide = Side.TOP;
            } else if (overlapDistance == bottom) {
                overlapSide = Side.BOTTOM;
            } else if (overlapDistance == back) {
                overlapSide = Side.BACK;
            } else if (overlapDistance == front) {
                overlapSide = Side.FRONT;
            }
            return true;
        }
        overlapSide = Side.NONE;
        overlapDistance = 0;
        return false;
    }
    
    /**
     * get the side the overlap occurs on. NONE if there are no overlaps
     *
     * @return overlap side
     */
    public Side getOverlappingSide() {
        return overlapSide;
    }
    
    /**
     * get the distance of the overlap
     *
     * @return distance of overlap
     */
    public double getOverlappingDistance() {
        return overlapDistance;
    }
    
    /**
     * check if this box contains a position
     *
     * @param position position to check
     * @return true of the box contains this position
     */
    public boolean containsPosition(Vector position) {
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
    
    /**
     * get the hash code of the box
     *
     * @return hash code of box
     */
    @Override
    public int hashCode() {
        return Objects.hash(width, height, depth, position);
    }
}

package jndev.pseudo3d.util;

/**
 * 3D vector and related mathematical operators
 *
 * @author JNDev (Jeremaster101)
 */
public class Vector {
    
    /**
     * x component of vector
     */
    private final double x;
    
    /**
     * y component of vector
     */
    private final double y;
    
    /**
     * z component of vector
     */
    private final double z;
    
    /**
     * create a new zero vector
     */
    public Vector() {
        x = 0;
        y = 0;
        z = 0;
    }
    
    /**
     * create a new 3D vector with components x, y, and z
     *
     * @param x x compnent of vector
     * @param y y component of vector
     * @param z z component of vector
     */
    public Vector(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    /**
     * create a new 2D vector with components x and y
     *
     * @param x x compnent of vector
     * @param y y component of vector
     */
    public Vector(double x, double y) {
        this.x = x;
        this.y = y;
        this.z = 0;
    }
    
    /**
     * copy constructor for vector
     *
     * @param vector vector to copy
     */
    public Vector(Vector vector) {
        x = vector.x;
        y = vector.y;
        z = vector.z;
    }
    
    /**
     * get the x component of the vector
     *
     * @return x component
     */
    public double getX() {
        return x;
    }
    
    /**
     * set the x component of the vector
     *
     * @param x x component
     * @return vector with modified x component
     */
    public Vector setX(double x) {
        return new Vector(x, y, z);
    }
    
    /**
     * get the y component of the vector
     *
     * @return y component
     */
    public double getY() {
        return y;
    }
    
    /**
     * set the y component of the vector
     *
     * @param y y component
     * @return vector with modified y component
     */
    public Vector setY(double y) {
        return new Vector(x, y, z);
    }
    
    /**
     * get the z component of the vector
     *
     * @return z component
     */
    public double getZ() {
        return z;
    }
    
    /**
     * set the z component of the vector
     *
     * @param z z component
     * @return vector with modified y component
     */
    public Vector setZ(double z) {
        return new Vector(x, y, z);
    }
    
    /**
     * get the magnitude (length) of the vector
     *
     * @return magnitude of vector
     */
    public double getMagnitude() {
        return Math.sqrt((x * x) + (y * y) + (z * z));
    }
    
    /**
     * add together this vector and another vector
     *
     * @param vector vector to add
     * @return vector from the sum of two vectors
     */
    public Vector add(Vector vector) {
        return new Vector(x + vector.getX(), y + vector.getY(), z + vector.getZ());
    }
    
    /**
     * subtract vector from this vector
     *
     * @param vector vector to subtract
     * @return vector from subtraction of two vectors
     */
    public Vector subtract(Vector vector) {
        return new Vector(x - vector.getX(), y - vector.getY(), z - vector.getZ());
    }
    
    /**
     * calculate the dot product of two vectors
     *
     * @param vector vector to dot product
     * @return value of the dot product
     */
    public double dot(Vector vector) {
        return (x * vector.getX()) + (y * vector.getY()) + (z * vector.getZ());
    }
    
    /**
     * cross multiply two vectors
     *
     * @param vector vector to cross multiply
     * @return vector calculated from cross multiplication
     */
    public Vector cross(Vector vector) {
        double i = (y * vector.getZ()) - (z * vector.getY());
        double j = -((x * vector.getZ()) - (z * vector.getX()));
        double k = (x * vector.getY()) - (y * vector.getX());
        return new Vector(i, j, k);
    }
    
    /**
     * scale a vector by a scalar
     *
     * @param scalar what to scale vector by
     * @return scaled vector
     */
    public Vector scale(double scalar) {
        return new Vector(x * scalar, y * scalar, z * scalar);
    }
    
    /**
     * calculate the distance between the ends of two vectors
     *
     * @param vector vector to calulcate distance to
     * @return distance to location
     */
    public double distance(Vector vector) {
        return Math.sqrt(Math.pow(x - vector.getX(), 2) + Math.pow(y - vector.getY(), 2) + Math.pow(z - vector.getZ(), 2));
    }
    
    /**
     * normalize the vector (vector of magnitude 1)
     *
     * @return normalized vector
     */
    public Vector normalize() {
        double mag = getMagnitude();
        return new Vector(x / mag, y / mag, z / mag);
    }
    
    /**
     * get the angle between two vectors in degrees
     *
     * @param vector vector to get angle between
     * @return angle between two vectors
     */
    public double angleBetween(Vector vector) {
        return Math.acos(dot(vector) / (getMagnitude() * vector.getMagnitude())) * (180 / Math.PI);
    }
    
    /**
     * makes the vector into a string
     *
     * @return vector form of vector
     */
    @Override
    public String toString() {
        return "<" + x + "," + y + "," + z + ">";
    }
    
    /**
     * check if two vectors are equal
     *
     * @param vector vector to compare to this vector
     * @return true if vectors are equal
     */
    @Override
    public boolean equals(Object vector) {
        if (this == vector) return true;
        if (vector == null || getClass() != vector.getClass()) return false;
        Vector v = (Vector) vector;
        return Double.compare(v.getX(), x) == 0 &&
                Double.compare(v.getY(), y) == 0 &&
                Double.compare(v.getZ(), z) == 0;
    }
}

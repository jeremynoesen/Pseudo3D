package xyz.jeremynoesen.pseudo3d.scene.util;

/**
 * immutable vector with vector mathematical operators
 *
 * @author Jeremy Noesen
 */
public class Vector {
    
    /**
     * x component of vector
     */
    private final float x;
    
    /**
     * y component of vector
     */
    private final float y;
    
    /**
     * z component of vector
     */
    private final float z;
    
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
     * @param x x component of vector
     * @param y y component of vector
     * @param z z component of vector
     */
    public Vector(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    /**
     * create a new 2D vector with components x and y
     *
     * @param x x component of vector
     * @param y y component of vector
     */
    public Vector(float x, float y) {
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
    public float getX() {
        return x;
    }
    
    /**
     * set the x component of the vector
     *
     * @param x x component
     * @return vector with modified x component
     */
    public Vector setX(float x) {
        return new Vector(x, y, z);
    }
    
    /**
     * get the y component of the vector
     *
     * @return y component
     */
    public float getY() {
        return y;
    }
    
    /**
     * set the y component of the vector
     *
     * @param y y component
     * @return vector with modified y component
     */
    public Vector setY(float y) {
        return new Vector(x, y, z);
    }
    
    /**
     * get the z component of the vector
     *
     * @return z component
     */
    public float getZ() {
        return z;
    }
    
    /**
     * set the z component of the vector
     *
     * @param z z component
     * @return vector with modified y component
     */
    public Vector setZ(float z) {
        return new Vector(x, y, z);
    }
    
    /**
     * get the magnitude (length) of the vector
     *
     * @return magnitude of vector
     */
    public float getMagnitude() {
        return (float) Math.sqrt((x * x) + (y * y) + (z * z));
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
     * multiply vector components by another vector's components
     *
     * @param vector vector to multiply
     * @return vector from multiplication of two vectors
     */
    public Vector multiply(Vector vector) {
        return new Vector(x * vector.getX(), y * vector.getY(), z * vector.getZ());
    }
    
    /**
     * scale a vector's magnitude by a scalar, which multiplies each component by the scalar
     *
     * @param scale what to scale vector by
     * @return scaled vector
     */
    public Vector multiply(float scale) {
        return new Vector(x * scale, y * scale, z * scale);
    }
    
    /**
     * calculate the dot product of two vectors
     *
     * @param vector vector to dot product
     * @return value of the dot product
     */
    public float dot(Vector vector) {
        return (x * vector.getX()) + (y * vector.getY()) + (z * vector.getZ());
    }
    
    /**
     * cross multiply two vectors
     *
     * @param vector vector to cross multiply
     * @return vector calculated from cross multiplication
     */
    public Vector cross(Vector vector) {
        float i = (y * vector.getZ()) - (z * vector.getY());
        float j = -((x * vector.getZ()) - (z * vector.getX()));
        float k = (x * vector.getY()) - (y * vector.getX());
        return new Vector(i, j, k);
    }
    
    /**
     * divide vector components by another vector's components
     *
     * @param vector vector to divide
     * @return vector from division of two vectors
     */
    public Vector divide(Vector vector) {
        return new Vector(x / vector.getX(), y / vector.getY(), z / vector.getZ());
    }
    
    /**
     * scale a vector's magnitude by a scalar, which divides each component by the scalar
     *
     * @param scale what to scale vector by
     * @return scaled vector
     */
    public Vector divide(float scale) {
        return new Vector(x / scale, y / scale, z / scale);
    }
    
    /**
     * calculate the distance between the ends of two vectors
     *
     * @param vector vector to calculate distance to
     * @return distance to location
     */
    public float distance(Vector vector) {
        return (float) Math.sqrt(Math.pow(x - vector.getX(), 2) + Math.pow(y - vector.getY(), 2) + Math.pow(z - vector.getZ(), 2));
    }
    
    /**
     * normalize the vector (vector of magnitude 1)
     *
     * @return normalized vector
     */
    public Vector normalize() {
        float mag = getMagnitude();
        return new Vector(x / mag, y / mag, z / mag);
    }
    
    /**
     * get the angle between two vectors in degrees
     *
     * @param vector vector to get angle between
     * @return angle between two vectors
     */
    public float angleBetween(Vector vector) {
        return (float) (Math.acos(dot(vector) / (getMagnitude() * vector.getMagnitude())) * (180 / Math.PI));
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
        Vector that = (Vector) vector;
        return Float.compare(that.x, x) == 0 &&
                Float.compare(that.y, y) == 0 &&
                Float.compare(that.z, z) == 0;
    }
}

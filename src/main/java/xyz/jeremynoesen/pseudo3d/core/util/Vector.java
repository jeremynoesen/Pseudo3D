package xyz.jeremynoesen.pseudo3d.core.util;

/**
 * Immutable Vector with vector mathematical operators
 *
 * @author Jeremy Noesen
 */
public class Vector {

    /**
     * X component of Vector
     */
    private final float x;

    /**
     * Y component of Vector
     */
    private final float y;

    /**
     * Z component of Vector
     */
    private final float z;

    /**
     * Create a new Vector
     */
    public Vector() {
        x = 0;
        y = 0;
        z = 0;
    }

    /**
     * Create a new 3D Vector with components x, y, and z
     *
     * @param x X component of Vector
     * @param y Y component of Vector
     * @param z Z component of Vector
     */
    public Vector(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Create a new 2D Vector with components x and y
     *
     * @param x X component of Vector
     * @param y Y component of Vector
     */
    public Vector(float x, float y) {
        this.x = x;
        this.y = y;
        this.z = 0;
    }

    /**
     * Copy constructor for Vector
     *
     * @param vector Vector to copy
     */
    public Vector(Vector vector) {
        x = vector.x;
        y = vector.y;
        z = vector.z;
    }

    /**
     * Get the x component of the Vector
     *
     * @return X component
     */
    public float getX() {
        return x;
    }

    /**
     * Set the x component of the Vector
     *
     * @param x X component
     * @return Vector with modified x component
     */
    public Vector setX(float x) {
        return new Vector(x, y, z);
    }

    /**
     * Get the y component of the Vector
     *
     * @return Y component
     */
    public float getY() {
        return y;
    }

    /**
     * Set the y component of the Vector
     *
     * @param y Y component
     * @return Vector with modified y component
     */
    public Vector setY(float y) {
        return new Vector(x, y, z);
    }

    /**
     * Get the z component of the Vector
     *
     * @return Z component
     */
    public float getZ() {
        return z;
    }

    /**
     * Set the z component of the Vector
     *
     * @param z Z component
     * @return Vector with modified y component
     */
    public Vector setZ(float z) {
        return new Vector(x, y, z);
    }

    /**
     * Get a specified component of the Vector
     *
     * @param axis Axis to get value for
     * @return Component of Vector based on Axis
     */
    public float get(Axis axis) {
        return switch (axis) {
            case X -> getX();
            case Y -> getY();
            case Z -> getZ();
        };
    }

    /**
     * Set a specified component of the Vector
     *
     * @param axis  Axis to set value to
     * @param value Value to set
     * @return Modified Vector
     */
    public Vector set(Axis axis, float value) {
        return switch (axis) {
            case X -> setX(value);
            case Y -> setY(value);
            case Z -> setZ(value);
        };
    }

    /**
     * Get the magnitude of the Vector
     *
     * @return Magnitude of Vector
     */
    public float getMagnitude() {
        return (float) Math.sqrt((x * x) + (y * y) + (z * z));
    }

    /**
     * Add together this Vector and other Vectors
     *
     * @param vector Vectors to add
     * @return Vector from the sum of the Vectors
     */
    public Vector add(Vector... vector) {
        Vector output = new Vector(this);
        for (Vector v : vector) {
            output = new Vector(output.x + v.x, output.y + v.y, output.z + v.z);
        }
        return output;
    }

    /**
     * Subtract Vectors from this Vector
     *
     * @param vector Vectors to subtract
     * @return Vector from subtraction of the Vectors
     */
    public Vector subtract(Vector... vector) {
        Vector output = new Vector(this);
        for (Vector v : vector) {
            output = new Vector(output.x - v.x, output.y - v.y, output.z - v.z);
        }
        return output;
    }

    /**
     * Multiply vector Components by other Vectors' components
     *
     * @param vector Vectors to multiply
     * @return Vector from multiplication of the Vectors
     */
    public Vector multiply(Vector... vector) {
        Vector output = new Vector(this);
        for (Vector v : vector) {
            output = new Vector(output.x * v.x, output.y * v.y, output.z * v.z);
        }
        return output;
    }

    /**
     * Multiply a Vector's magnitude by a scalar, which multiplies each component by the scalar
     *
     * @param scale What to scale the Vector by
     * @return Scaled Vector
     */
    public Vector multiply(float scale) {
        return new Vector(x * scale, y * scale, z * scale);
    }

    /**
     * Divide Vector components by other Vectors' components
     *
     * @param vector Vectors to divide
     * @return Vector from division of the Vectors
     */
    public Vector divide(Vector... vector) {
        Vector output = new Vector(this);
        for (Vector v : vector) {
            if (Float.compare(v.x, 0) == 0 || Float.compare(v.y, 0) == 0 || Float.compare(v.z, 0) == 0)
                throw new ArithmeticException("Cannot divide by zero");
            output = new Vector(output.x / v.x, output.y / v.y, output.z / v.z);
        }
        return output;
    }

    /**
     * Divide a Vector's magnitude by a scalar, which divides each component by the scalar
     *
     * @param scale What to scale the Vector by
     * @return Scaled Vector
     */
    public Vector divide(float scale) {
        if (Float.compare(scale, 0) == 0)
            throw new ArithmeticException("Cannot divide by zero");
        return new Vector(x / scale, y / scale, z / scale);
    }

    /**
     * Calculate the dot product of two Vectors
     *
     * @param vector Vector to dot product
     * @return Value of the dot product
     */
    public float dot(Vector vector) {
        return (x * vector.getX()) + (y * vector.getY()) + (z * vector.getZ());
    }

    /**
     * Cross multiply two Vectors
     *
     * @param vector Vector to cross multiply
     * @return Vector calculated from cross multiplication
     */
    public Vector cross(Vector vector) {
        float i = (y * vector.getZ()) - (z * vector.getY());
        float j = -((x * vector.getZ()) - (z * vector.getX()));
        float k = (x * vector.getY()) - (y * vector.getX());
        return new Vector(i, j, k);
    }

    /**
     * Calculate the distance between the ends of two Vectors
     *
     * @param vector Vector to calculate distance to
     * @return Distance to location
     */
    public float distance(Vector vector) {
        return (float) Math.sqrt(Math.pow(x - vector.getX(), 2) + Math.pow(y - vector.getY(), 2) + Math.pow(z - vector.getZ(), 2));
    }

    /**
     * Normalize the Vector (a Vector of magnitude 1)
     *
     * @return Normalized Vector
     */
    public Vector normalize() {
        float mag = getMagnitude();
        if (Float.compare(mag, 0) == 0)
            throw new ArithmeticException("Cannot divide by zero");
        return new Vector(x / mag, y / mag, z / mag);
    }

    /**
     * Get the angle between two Vectors in degrees
     *
     * @param vector Vector to get angle between
     * @return Angle between two Vectors
     */
    public float angleBetween(Vector vector) {
        float thisMag = getMagnitude();
        float thatMag = vector.getMagnitude();
        if (Float.compare(thisMag, 0) == 0 || Float.compare(thatMag, 0) == 0)
            throw new ArithmeticException("Cannot divide by zero");
        return (float) (Math.acos(dot(vector) / (thisMag * thatMag)) * (180 / Math.PI));
    }

    /**
     * Get the Vector as a String
     *
     * @return Vector as a String in vector format
     */
    @Override
    public String toString() {
        return "<" + x + "," + y + "," + z + ">";
    }

    /**
     * Check if two Vectors are equal
     *
     * @param vector Vector to compare to this Vector
     * @return True if the Vectors are equal
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

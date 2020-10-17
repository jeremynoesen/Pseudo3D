package jndev.pseudo3d.util;

/**
 * this class is for adding faster implementations of java's math class methods. these are especially useful for the
 * renderer classes and physics, where these calculations need to be fast. only methods that needed to be optimized are
 * here
 *
 * @author JNDev (Jeremaster101)
 */
public class FastMath {
    
    /**
     * get the maximum of two floats. this method cuts out checks for NaN for a minor speed improvement
     *
     * @param a a float
     * @param b another float
     * @return maximum of two floats
     */
    public static float max(float a, float b) {
        return a >= b ? a : b;
    }
    
    /**
     * get the minimum of two floats. this method cuts out checks for NaN for a minor speed improvement
     *
     * @param a a float
     * @param b another float
     * @return minimum of two floats
     */
    public static float min(float a, float b) {
        return a <= b ? a : b;
    }
    
    /**
     * round a float up to the nearest integer. this method cuts out many operations dealing with exponent and
     * converting the float to bits
     *
     * @param a a float
     * @return float rounded up to nearest integer
     */
    public static int ceil(float a) {
        return a - ((int) a) == 0 ? (int) a : ((int) a) + 1;
    }
    
    /**
     * round a float down to the nearest integer. this method cuts out many operations dealing with exponent and
     * converting the float to bits
     *
     * @param a a float
     * @return float rounded down to nearest integer
     */
    public static int floor(float a) {
        return (int) a;
    }
    
    /**
     * round a float to the nearest integer. this method cuts out many operations dealing with exponent and converting
     * the float to bits
     *
     * @param a a float
     * @return float rounded to nearest integer
     */
    public static int round(float a) {
        return a - ((int) a) < 0.5f ? (int) a : ((int) a) + 1;
    }
    
    /**
     * take base integer a to the integer power of b, same as multiplying a by itself b times
     *
     * @param a base
     * @param b exponent
     * @return a to the power of b
     */
    public static int pow(int a, int b) {
        int c = a;
        for (int i = 1; i < b; i++) {
            c *= a;
        }
        return c;
    }
}

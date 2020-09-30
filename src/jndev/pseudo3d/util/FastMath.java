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
    public static float ceil(float a) {
        return a - ((int) a) == 0 ? a : ((int) a) + 1;
    }
    
    /**
     * round a float down to the nearest integer. this method cuts out many operations dealing with exponent and
     * converting the float to bits
     *
     * @param a a float
     * @return float rounded down to nearest integer
     */
    public static float floor(float a) {
        return (int) a;
    }
}

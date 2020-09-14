package jndev.pseudo3d.util;

/**
 * this class is for adding faster implementations of java's math class methods. these are especially useful for the
 * renderer classes and physics, where these calculations need to be fast. only methods that needed to be optimized are
 * here
 *
 * @author JNDev (Jeremaster101)
 */
public class QuickMath {
    
    /**
     * get the maximum of two doubles. this method cuts out checks for NaN for a minor speed improvement
     *
     * @param a a double
     * @param b another double
     * @return maximum of two doubles
     */
    public static double max(double a, double b) {
        return a >= b ? a : b;
    }
    
    /**
     * get the minimum of two doubles. this method cuts out checks for NaN for a minor speed improvement
     *
     * @param a a double
     * @param b another double
     * @return minimum of two doubles
     */
    public static double min(double a, double b) {
        return a <= b ? a : b;
    }
    
    /**
     * round a double up to the nearest integer. this method cuts out many operations dealing with exponent and
     * converting the double to bits
     *
     * @param a a double
     * @return double rounded up to nearest integer
     */
    public static double ceil(double a) {
        return a - ((int) a) == 0 ? a : ((int) a) + 1;
    }
    
    /**
     * round a double down to the nearest integer. this method cuts out many operations dealing with exponent and
     * converting the double to bits
     *
     * @param a a double
     * @return double rounded down to nearest integer
     */
    public static double floor(double a) {
        return (int) a;
    }
}

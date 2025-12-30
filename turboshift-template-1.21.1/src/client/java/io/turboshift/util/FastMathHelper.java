package io.turboshift.util;

/**
 * Fast math operations for performance-critical code
 */
public class FastMathHelper {
    
    /**
     * Fast distance squared calculation (avoids expensive sqrt)
     */
    public static double distanceSquared(double x1, double y1, double z1, 
                                        double x2, double y2, double z2) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        double dz = z2 - z1;
        return dx * dx + dy * dy + dz * dz;
    }
    
    /**
     * Fast floor operation
     */
    public static int fastFloor(double value) {
        int i = (int) value;
        return value < i ? i - 1 :  i;
    }
    
    /**
     * Fast ceiling operation
     */
    public static int fastCeil(double value) {
        int i = (int) value;
        return value > i ? i + 1 : i;
    }
    
    /**
     * Fast absolute value for integers
     */
    public static int abs(int value) {
        return (value ^ (value >> 31)) - (value >> 31);
    }
    
    /**
     * Fast absolute value for floats
     */
    public static float abs(float value) {
        return value < 0 ? -value : value;
    }
    
    /**
     * Fast max for integers
     */
    public static int max(int a, int b) {
        return a > b ? a : b;
    }
    
    /**
     * Fast min for integers
     */
    public static int min(int a, int b) {
        return a < b ? a : b;
    }
    
    /**
     * Fast clamp operation
     */
    public static int clamp(int value, int min, int max) {
        return value < min ? min : (value > max ? max : value);
    }
    
    /**
     * Fast clamp operation for floats
     */
    public static float clamp(float value, float min, float max) {
        return value < min ? min : (value > max ?  max : value);
    }
    
    /**
     * Linear interpolation
     */
    public static double lerp(double start, double end, double t) {
        return start + t * (end - start);
    }
}
package io.turboshift.util;

import io.turboshift.TurboShift;
import io.turboshift.core.ConfigManager;
import io. turboshift.core.PerformanceTracker;

public class MemoryOptimizer {
    private static long lastGCTime = System.currentTimeMillis();
    private static long lastMemoryCheck = System.currentTimeMillis();
    
    /**
     * Tick method called every second
     */
    public static void tick() {
        if (! ConfigManager.getConfig().enableMemoryOptimization) {
            return;
        }
        
        long currentTime = System. currentTimeMillis();
        
        // Check memory usage every 5 seconds
        if (currentTime - lastMemoryCheck > 5000) {
            checkMemoryUsage();
            lastMemoryCheck = currentTime;
        }
        
        // Scheduled garbage collection
        if (ConfigManager.getConfig().autoGarbageCollection) {
            long gcInterval = ConfigManager.getConfig().gcIntervalSeconds * 1000L;
            
            if (currentTime - lastGCTime > gcInterval) {
                performGarbageCollection();
                lastGCTime = currentTime;
            }
        }
    }
    
    /**
     * Check memory usage and trigger GC if needed
     */
    private static void checkMemoryUsage() {
        int memoryUsage = PerformanceTracker.getMemoryUsagePercent();
        int threshold = ConfigManager.getConfig().memoryThresholdPercent;
        
        if (memoryUsage >= threshold) {
            TurboShift. LOGGER.warn("Memory usage at {}%, triggering garbage collection.. .", memoryUsage);
            performGarbageCollection();
        }
    }
    
    /**
     * Perform garbage collection and log results
     */
    private static void performGarbageCollection() {
        Runtime runtime = Runtime.getRuntime();
        long beforeFree = runtime.freeMemory();
        long beforeTotal = runtime.totalMemory();
        
        System.gc();
        
        long afterFree = runtime.freeMemory();
        long afterTotal = runtime.totalMemory();
        
        long freedMemory = (afterFree - beforeFree) / 1024 / 1024;
        
        if (ConfigManager.getConfig().detailedLogging) {
            TurboShift.LOGGER.info("Garbage collection completed:");
            TurboShift. LOGGER.info("  Freed: {} MB", freedMemory);
            TurboShift.LOGGER.info("  Total Memory: {} MB → {} MB", 
                    beforeTotal / 1024 / 1024, 
                    afterTotal / 1024 / 1024);
        }
    }
    
    /**
     * Get used memory in MB
     */
    public static long getUsedMemoryMB() {
        Runtime runtime = Runtime.getRuntime();
        return (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024;
    }
    
    /**
     * Get max available memory in MB
     */
    public static long getMaxMemoryMB() {
        return Runtime.getRuntime().maxMemory() / 1024 / 1024;
    }
    
    /**
     * Get free memory in MB
     */
    public static long getFreeMemoryMB() {
        return Runtime. getRuntime().freeMemory() / 1024 / 1024;
    }
    
    /**
     * Get memory usage percentage
     */
    public static int getMemoryUsagePercent() {
        long used = getUsedMemoryMB();
        long max = getMaxMemoryMB();
        if (max == 0) return 0;
        return (int) ((used * 100) / max);
    }
    
    /**
     * Force immediate garbage collection
     */
    public static void forceGC() {
        TurboShift.LOGGER. info("Forcing garbage collection...");
        performGarbageCollection();
    }
}
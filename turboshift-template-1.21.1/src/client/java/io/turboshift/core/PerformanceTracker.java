package io. turboshift.core;

import io.turboshift.TurboShift;
import net.minecraft.client.MinecraftClient;

public class PerformanceTracker {
    private static long lastUpdateTime = 0;
    private static int entityCount = 0;
    private static int particleCount = 0;
    private static int chunksRendered = 0;
    private static double currentFPS = 0;
    private static long usedMemoryMB = 0;
    private static long maxMemoryMB = 0;
    
    public static void initialize() {
        lastUpdateTime = System.currentTimeMillis();
        maxMemoryMB = Runtime.getRuntime().maxMemory() / 1024 / 1024;
        TurboShift.LOGGER. info("Performance Tracker initialized");
        TurboShift.LOGGER.info("Available Memory: {} MB", maxMemoryMB);
    }
    
    public static void update(MinecraftClient client) {
        if (! ConfigManager.getConfig().enableDebugMode) {
            return;
        }
        
        long currentTime = System.currentTimeMillis();
        long deltaTime = currentTime - lastUpdateTime;
        
        // Update every 5 seconds
        if (deltaTime >= 5000) {
            // Update memory stats
            Runtime runtime = Runtime.getRuntime();
            usedMemoryMB = (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024;
            
            // Get FPS (approximation)
            if (client.getCurrentFps() > 0) {
                currentFPS = client.getCurrentFps();
            }
            
            logMetrics();
            lastUpdateTime = currentTime;
        }
    }
    
    private static void logMetrics() {
        TurboShift.LOGGER. info("╔═══════════════════════════════════════╗");
        TurboShift. LOGGER.info("║      Performance Metrics              ║");
        TurboShift.LOGGER.info("╠═══════════════════════════════════════╣");
        TurboShift.LOGGER. info("║  FPS: {}", String.format("%-32s", currentFPS + " fps") + "║");
        TurboShift.LOGGER.info("║  Entities: {}", String.format("%-27s", entityCount) + "║");
        TurboShift.LOGGER.info("║  Particles: {}", String.format("%-26s", particleCount) + "║");
        TurboShift.LOGGER.info("║  Chunks: {}", String. format("%-29s", chunksRendered) + "║");
        TurboShift.LOGGER.info("║  Memory: {} MB / {} MB{}", 
                String.format("%4d", usedMemoryMB),
                String.format("%5d", maxMemoryMB),
                String.format("%9s", "║"));
        TurboShift.LOGGER.info("║  Usage: {}%{}", 
                String.format("%2d", getMemoryUsagePercent()),
                String.format("%28s", "║"));
        TurboShift.LOGGER. info("╚═══════════════════════════════════════╝");
    }
    
    public static int getMemoryUsagePercent() {
        if (maxMemoryMB == 0) return 0;
        return (int) ((usedMemoryMB * 100) / maxMemoryMB);
    }
    
    // Setters for mixins to update metrics
    public static void setEntityCount(int count) { entityCount = count; }
    public static void setParticleCount(int count) { particleCount = count; }
    public static void setChunksRendered(int count) { chunksRendered = count; }
    public static void setCurrentFPS(double fps) { currentFPS = fps; }
    
    // Getters
    public static int getEntityCount() { return entityCount; }
    public static int getParticleCount() { return particleCount; }
    public static int getChunksRendered() { return chunksRendered; }
    public static double getCurrentFPS() { return currentFPS; }
    public static long getUsedMemoryMB() { return usedMemoryMB; }
    public static long getMaxMemoryMB() { return maxMemoryMB; }
}
package io.turboshift.engine;

import io.turboshift.TurboShift;
import io.turboshift.core.ConfigManager;
import io.turboshift.config.TurboShiftConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.ChunkPos;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ChunkAccelerator - Optimizes chunk rendering and loading performance
 */
public class ChunkAccelerator {
    
    private static final ConcurrentHashMap<ChunkPos, Long> chunkLoadTimes = new ConcurrentHashMap<>();
    private static final AtomicInteger activeChunkBuilds = new AtomicInteger(0);
    private static int optimalThreadCount = -1;
    
    /**
     * Get optimal thread count for chunk building
     */
    public static int getOptimalThreadCount() {
        TurboShiftConfig config = ConfigManager.getConfig();
        
        if (!config.optimizeChunkRendering) {
            return 1;
        }
        
        if (optimalThreadCount > 0) {
            return optimalThreadCount;
        }
        
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        
        if (config.chunkBuilderThreads > 0) {
            optimalThreadCount = Math.min(config.chunkBuilderThreads, availableProcessors);
            logThreadCount(optimalThreadCount, "configured");
            return optimalThreadCount;
        }
        
        optimalThreadCount = calculateOptimalThreads(availableProcessors);
        logThreadCount(optimalThreadCount, "auto-detected");
        
        return optimalThreadCount;
    }
    
    private static int calculateOptimalThreads(int availableProcessors) {
        int reservedCores = 2;
        int calculatedThreads = Math.max(1, availableProcessors - reservedCores);
        int maxThreads = 8;
        
        TurboShiftConfig config = ConfigManager.getConfig();
        switch (config.activeProfile) {
            case MAXIMUM_PERFORMANCE:
                return Math.min(calculatedThreads, maxThreads);
            case BALANCED:
                return Math.min((int) (calculatedThreads * 0.75), maxThreads);
            case QUALITY_FOCUSED:
                return Math.min(Math.max(2, calculatedThreads / 2), maxThreads);
            default:
                return Math.min(calculatedThreads, maxThreads);
        }
    }
    
    public static boolean shouldUseAggressiveCulling() {
        return ConfigManager.getConfig().aggressiveChunkCulling;
    }
    
    public static boolean shouldUseAsyncLoading() {
        return ConfigManager.getConfig().asyncChunkLoading;
    }
    
    public static boolean shouldOptimizeFrustumCulling() {
        return ConfigManager.getConfig().optimizeFrustumCulling;
    }
    
    public static int getChunkPriority(ChunkPos chunkPos) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) {
            return 0;
        }
        
        int playerChunkX = client.player.getChunkPos().x;
        int playerChunkZ = client.player.getChunkPos().z;
        
        int deltaX = Math.abs(chunkPos.x - playerChunkX);
        int deltaZ = Math.abs(chunkPos.z - playerChunkZ);
        int distance = Math.max(deltaX, deltaZ);
        
        int basePriority = distance * 10;
        int lookDirectionBonus = isChunkInLookDirection(chunkPos, client) ? -50 : 0;
        
        Long lastLoadTime = chunkLoadTimes.get(chunkPos);
        int loadTimeBonus = (lastLoadTime != null && lastLoadTime > 100) ? 20 : 0;
        
        return basePriority + lookDirectionBonus + loadTimeBonus;
    }
    
    private static boolean isChunkInLookDirection(ChunkPos chunkPos, MinecraftClient client) {
        if (client.player == null) return false;
        
        float yaw = client.player.getYaw();
        int playerChunkX = client.player.getChunkPos().x;
        int playerChunkZ = client.player.getChunkPos().z;
        
        int deltaX = chunkPos.x - playerChunkX;
        int deltaZ = chunkPos.z - playerChunkZ;
        
        double angleToChunk = Math.toDegrees(Math.atan2(deltaZ, deltaX));
        double normalizedYaw = ((yaw % 360) + 360) % 360;
        
        double angleDiff = Math.abs(angleToChunk - normalizedYaw);
        if (angleDiff > 180) angleDiff = 360 - angleDiff;
        
        return angleDiff < 60;
    }
    
    public static void recordChunkLoadTime(ChunkPos chunkPos, long loadTimeMs) {
        chunkLoadTimes.put(chunkPos, loadTimeMs);
        if (chunkLoadTimes.size() > 1000) {
            chunkLoadTimes.clear(); // Simple clear to prevent memory leak
        }
    }
    
    public static void incrementActiveBuilds() { activeChunkBuilds.incrementAndGet(); }
    public static void decrementActiveBuilds() { activeChunkBuilds.decrementAndGet(); }
    public static int getActiveBuilds() { return activeChunkBuilds.get(); }
    
    public static boolean isUnderHeavyLoad() {
        return activeChunkBuilds.get() > (getOptimalThreadCount() * 1.5);
    }
    
    public static void clearCache() {
        chunkLoadTimes.clear();
        activeChunkBuilds.set(0);
    }

    private static void logThreadCount(int threadCount, String method) {
        if (ConfigManager.getConfig().detailedLogging) {
            TurboShift.LOGGER.info("Chunk builder using {} threads ({})", threadCount, method);
        }
    }
}
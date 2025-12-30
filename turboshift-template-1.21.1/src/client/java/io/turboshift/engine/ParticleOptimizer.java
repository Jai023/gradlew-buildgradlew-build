package io. turboshift.engine;

import io.turboshift.core. ConfigManager;
import io.turboshift.config.TurboShiftConfig;
import io.turboshift.core. PerformanceTracker;

import java.util.Random;

public class ParticleOptimizer {
    private static final Random RANDOM = new Random();
    private static int currentParticleCount = 0;
    private static long lastResetTime = System.currentTimeMillis();
    
    /**
     * Determines if a particle should be spawned
     */
    public static boolean shouldSpawnParticle() {
        TurboShiftConfig config = ConfigManager.getConfig();
        
        if (!config.optimizeParticles) {
            return true;
        }
        
        // Reset counter every second
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastResetTime > 1000) {
            currentParticleCount = 0;
            lastResetTime = currentTime;
        }
        
        // Check max particle limit
        if (currentParticleCount >= config.maxActiveParticles) {
            return false;
        }
        
        // Apply reduction percentage
        int reductionLevel = config.particleReduction;
        if (RANDOM.nextInt(100) < reductionLevel) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Smart particle spawning based on distance and performance
     */
    public static boolean shouldSpawnParticleSmart(double distanceSq) {
        TurboShiftConfig config = ConfigManager.getConfig();
        
        if (!config.smartParticleSpawning) {
            return shouldSpawnParticle();
        }
        
        // Basic check first
        if (! shouldSpawnParticle()) {
            return false;
        }
        
        // Distance-based spawning - reduce particles at distance
        double nearDistance = 16 * 16; // 16 blocks
        double farDistance = 32 * 32;  // 32 blocks
        
        if (distanceSq > farDistance) {
            // Very far - spawn only 10% of particles
            return RANDOM.nextInt(100) < 10;
        } else if (distanceSq > nearDistance) {
            // Medium distance - spawn 50% of particles
            return RANDOM.nextInt(100) < 50;
        }
        
        // Close distance - normal spawning
        return true;
    }
    
    /**
     * Increment particle counter
     */
    public static void onParticleSpawned() {
        currentParticleCount++;
        PerformanceTracker.setParticleCount(currentParticleCount);
    }
    
    /**
     * Decrement particle counter
     */
    public static void onParticleRemoved() {
        if (currentParticleCount > 0) {
            currentParticleCount--;
            PerformanceTracker.setParticleCount(currentParticleCount);
        }
    }
    
    /**
     * Reset particle counter
     */
    public static void resetCounter() {
        currentParticleCount = 0;
        lastResetTime = System.currentTimeMillis();
        PerformanceTracker.setParticleCount(0);
    }
    
    /**
     * Get current particle count
     */
    public static int getCurrentParticleCount() {
        return currentParticleCount;
    }
    
    /**
     * Check if particle system is under stress
     */
    public static boolean isUnderStress() {
        TurboShiftConfig config = ConfigManager.getConfig();
        return currentParticleCount > (config.maxActiveParticles * 0.8);
    }
}
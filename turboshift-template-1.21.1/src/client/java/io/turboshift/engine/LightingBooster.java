package io. turboshift.engine;

import io.turboshift.core. ConfigManager;
import io.turboshift.config.TurboShiftConfig;

public class LightingBooster {
    
    /**
     * Check if lighting update should be skipped for hidden blocks
     */
    public static boolean shouldSkipLightUpdate(boolean isHidden) {
        TurboShiftConfig config = ConfigManager.getConfig();
        
        if (!config.fastLighting) {
            return false;
        }
        
        // Skip lighting updates for hidden blocks when optimization is enabled
        return isHidden && config.skipHiddenLighting;
    }
    
    /**
     * Check if async light calculation should be used
     */
    public static boolean shouldUseAsyncCalculation() {
        return ConfigManager.getConfig().asyncLightCalculation;
    }
    
    /**
     * Check if smooth lighting optimization is enabled
     */
    public static boolean shouldOptimizeSmoothLighting() {
        return ConfigManager.getConfig().smoothLightingOptimization;
    }
    
    /**
     * Get lighting update priority based on visibility
     * Returns: 0 = high priority, 1 = medium, 2 = low
     */
    public static int getLightingPriority(boolean isVisible, double distanceSq) {
        TurboShiftConfig config = ConfigManager.getConfig();
        
        if (!config.fastLighting) {
            return 0; // Always high priority when optimization is off
        }
        
        if (! isVisible) {
            return 2; // Low priority for non-visible blocks
        }
        
        // Distance-based priority
        double nearDistance = 16 * 16;
        double farDistance = 32 * 32;
        
        if (distanceSq < nearDistance) {
            return 0; // High priority for close blocks
        } else if (distanceSq < farDistance) {
            return 1; // Medium priority
        } else {
            return 2; // Low priority for far blocks
        }
    }
}
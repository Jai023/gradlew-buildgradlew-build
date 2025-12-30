package io.turboshift. engine;

import io.turboshift.core.ConfigManager;
import io.turboshift.config.TurboShiftConfig;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

public class EntityCullingSystem {
    
    /**
     * Determines if an entity should be rendered based on distance and visibility
     */
    public static boolean shouldRenderEntity(Entity entity, Vec3d cameraPos) {
        TurboShiftConfig config = ConfigManager.getConfig();
        
        if (!config.enableEntityCulling) {
            return true;
        }
        
        double distanceSq = entity.squaredDistanceTo(cameraPos);
        double maxDistance = config.entityRenderDistance;
        double maxDistanceSq = maxDistance * maxDistance;
        
        // Distance-based culling
        if (distanceSq > maxDistanceSq) {
            return false;
        }
        
        // Cull invisible entities more aggressively
        if (config.cullInvisibleEntities && entity.isInvisible()) {
            double invisibleCullDistance = maxDistance * 0.5;
            if (distanceSq > invisibleCullDistance * invisibleCullDistance) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Determines if an entity should be ticked based on distance
     */
    public static boolean shouldTickEntity(Entity entity, Vec3d cameraPos) {
        TurboShiftConfig config = ConfigManager.getConfig();
        
        if (!config.smartEntityTicking) {
            return true;
        }
        
        double distanceSq = entity.squaredDistanceTo(cameraPos);
        double tickDistance = config.entityTickDistance;
        
        return distanceSq <= tickDistance * tickDistance;
    }
    
    /**
     * Calculate entity LOD (Level of Detail) based on distance
     * Returns:  0 = full detail, 1 = medium, 2 = low
     */
    public static int getEntityLOD(Entity entity, Vec3d cameraPos) {
        TurboShiftConfig config = ConfigManager.getConfig();
        
        if (!config. smartEntityTicking) {
            return 0; // Full detail
        }
        
        double distanceSq = entity.squaredDistanceTo(cameraPos);
        double renderDistance = config.entityRenderDistance;
        
        double lowDetailThreshold = (renderDistance * 0.33) * (renderDistance * 0.33);
        double mediumDetailThreshold = (renderDistance * 0.66) * (renderDistance * 0.66);
        
        if (distanceSq < lowDetailThreshold) {
            return 0; // Full detail
        } else if (distanceSq < mediumDetailThreshold) {
            return 1; // Medium detail
        } else {
            return 2; // Low detail
        }
    }
}
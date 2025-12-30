package io.turboshift.core;

import io.turboshift.TurboShift;
import io.turboshift.config.TurboShiftConfig;

import java.util.ArrayList;
import java.util.List;

public class OptimizationEngine {
    private static final List<Optimization> registeredOptimizations = new ArrayList<>();
    
    public static void registerAll() {
        TurboShiftConfig config = ConfigManager.getConfig();
        
        TurboShift.LOGGER. info("╔═══════════════════════════════════════╗");
        TurboShift.LOGGER.info("║   Registering Optimizations...         ║");
        TurboShift.LOGGER.info("╚═══════════════════════════════════════╝");
        
        // Entity Optimizations
        if (config.enableEntityCulling) {
            register(new Optimization(
                "Entity Distance Culling",
                "Culls entities beyond " + config.entityRenderDistance + " blocks",
                OptimizationType.ENTITY
            ));
        }
        
        if (config.smartEntityTicking) {
            register(new Optimization(
                "Smart Entity Ticking",
                "Reduces tick rate for distant entities",
                OptimizationType.ENTITY
            ));
        }
        
        // Chunk Optimizations
        if (config.optimizeChunkRendering) {
            int threads = config.chunkBuilderThreads == 0 
                    ? Runtime.getRuntime().availableProcessors() - 2 
                    : config.chunkBuilderThreads;
            register(new Optimization(
                "Multi-threaded Chunk Builder",
                "Using " + threads + " worker threads",
                OptimizationType.CHUNK
            ));
        }
        
        if (config.aggressiveChunkCulling) {
            register(new Optimization(
                "Aggressive Chunk Culling",
                "Culls non-visible chunks aggressively",
                OptimizationType.CHUNK
            ));
        }
        
        if (config.asyncChunkLoading) {
            register(new Optimization(
                "Async Chunk Loading",
                "Loads chunks asynchronously",
                OptimizationType.CHUNK
            ));
        }
        
        // Particle Optimizations
        if (config.optimizeParticles) {
            register(new Optimization(
                "Particle Throttling",
                config.particleReduction + "% reduction, max " + config.maxActiveParticles,
                OptimizationType. PARTICLE
            ));
        }
        
        // Lighting Optimizations
        if (config.fastLighting) {
            register(new Optimization(
                "Fast Lighting Engine",
                "Optimized lighting calculations",
                OptimizationType. LIGHTING
            ));
        }
        
        if (config.asyncLightCalculation) {
            register(new Optimization(
                "Async Light Calculation",
                "Moves lighting to background threads",
                OptimizationType. LIGHTING
            ));
        }
        
        // Memory Optimizations
        if (config. enableMemoryOptimization) {
            register(new Optimization(
                "Smart Memory Manager",
                "Automatic memory optimization",
                OptimizationType. MEMORY
            ));
        }
        
        if (config.autoGarbageCollection) {
            register(new Optimization(
                "Auto Garbage Collection",
                "GC every " + config.gcIntervalSeconds + " seconds",
                OptimizationType.MEMORY
            ));
        }
        
        // Rendering Optimizations
        if (config.cullHiddenFaces) {
            register(new Optimization(
                "Hidden Face Culling",
                "Skips rendering hidden block faces",
                OptimizationType.RENDERING
            ));
        }
        
        if (config.batchRendering) {
            register(new Optimization(
                "Batch Rendering System",
                "Groups similar render calls",
                OptimizationType. RENDERING
            ));
        }
        
        if (config.optimizeFrustumCulling) {
            register(new Optimization(
                "Optimized Frustum Culling",
                "Faster visibility checks",
                OptimizationType. RENDERING
            ));
        }
        
        // Summary
        TurboShift. LOGGER.info("╔═══════════════════════════════════════╗");
        TurboShift.LOGGER. info("║  Active Optimizations: {}            ║", 
                String.format("%2d", registeredOptimizations. size()));
        TurboShift.LOGGER.info("╚═══════════════════════════════════════╝");
        
        for (Optimization opt : registeredOptimizations) {
            TurboShift.LOGGER.info("  ✓ [{}] {}", 
                    opt.type.getShortName(), 
                    opt.name);
            if (config.detailedLogging) {
                TurboShift.LOGGER.info("      └─ {}", opt.description);
            }
        }
        
        TurboShift. LOGGER.info("═══════════════════════════════════════");
    }
    
    private static void register(Optimization optimization) {
        registeredOptimizations.add(optimization);
    }
    
    public static List<Optimization> getRegisteredOptimizations() {
        return new ArrayList<>(registeredOptimizations);
    }
    
    public static int getOptimizationCount() {
        return registeredOptimizations.size();
    }
    
    // Optimization class
    public static class Optimization {
        private final String name;
        private final String description;
        private final OptimizationType type;
        
        public Optimization(String name, String description, OptimizationType type) {
            this.name = name;
            this.description = description;
            this.type = type;
        }
        
        public String getName() { return name; }
        public String getDescription() { return description; }
        public OptimizationType getType() { return type; }
    }
    
    public enum OptimizationType {
        ENTITY("ENT"),
        CHUNK("CHK"),
        PARTICLE("PAR"),
        LIGHTING("LIT"),
        MEMORY("MEM"),
        RENDERING("REN");
        
        private final String shortName;
        
        OptimizationType(String shortName) {
            this.shortName = shortName;
        }
        
        public String getShortName() {
            return shortName;
        }
    }
}
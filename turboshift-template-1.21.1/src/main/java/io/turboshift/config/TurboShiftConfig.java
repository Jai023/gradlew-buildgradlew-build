package io.turboshift.config;

public class TurboShiftConfig {
    
    // === Performance Profiles ===
    public PerformanceProfile activeProfile = PerformanceProfile.BALANCED;
    
    // === Entity Optimization ===
    public boolean enableEntityCulling = true;
    public int entityRenderDistance = 64;
    public boolean smartEntityTicking = true;
    public int entityTickDistance = 48;
    public boolean cullInvisibleEntities = true;
    
    // === Chunk Optimization ===
    public boolean optimizeChunkRendering = true;
    public int chunkBuilderThreads = 0; // 0 = auto-detect
    public boolean aggressiveChunkCulling = true;
    public boolean asyncChunkLoading = true;
    public boolean optimizeFrustumCulling = true;
    
    // === Particle Optimization ===
    public boolean optimizeParticles = true;
    public int particleReduction = 50; // 0-100 percentage
    public int maxActiveParticles = 4000;
    public boolean smartParticleSpawning = true;
    
    // === Lighting Optimization ===
    public boolean fastLighting = true;
    public boolean skipHiddenLighting = true;
    public boolean asyncLightCalculation = true;
    public boolean smoothLightingOptimization = true;
    
    // === Memory Management ===
    public boolean enableMemoryOptimization = true;
    public boolean autoGarbageCollection = true;
    public int gcIntervalSeconds = 300; // 5 minutes
    public int memoryThresholdPercent = 85;
    
    // === Rendering Optimization ===
    public boolean cullHiddenFaces = true;
    public boolean batchRendering = true;
    public boolean optimizeBlockRendering = true;
    public boolean fastEntityAnimation = true;
    
    // === Advanced Settings ===
    public boolean enableDebugMode = false;
    public boolean showPerformanceHUD = false;
    public boolean detailedLogging = false;
    public boolean experimentalFeatures = false;
    
    // === Performance Profiles ===
    public enum PerformanceProfile {
        MAXIMUM_PERFORMANCE("Maximum Performance - Best FPS"),
        BALANCED("Balanced - Quality & Performance"),
        QUALITY_FOCUSED("Quality Focused - Better Visuals"),
        CUSTOM("Custom - User Defined");
        
        private final String description;
        
        PerformanceProfile(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    /**
     * Apply a performance profile preset
     */
    public void applyProfile(PerformanceProfile profile) {
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        
        switch (profile) {
            case MAXIMUM_PERFORMANCE: 
                // Maximum FPS settings
                entityRenderDistance = 48;
                entityTickDistance = 32;
                particleReduction = 75;
                maxActiveParticles = 2000;
                chunkBuilderThreads = Math.min(availableProcessors, 8);
                aggressiveChunkCulling = true;
                cullInvisibleEntities = true;
                fastEntityAnimation = true;
                break;
                
            case BALANCED:
                // Balanced settings (default)
                entityRenderDistance = 64;
                entityTickDistance = 48;
                particleReduction = 50;
                maxActiveParticles = 4000;
                chunkBuilderThreads = Math.max(2, availableProcessors - 2);
                aggressiveChunkCulling = true;
                cullInvisibleEntities = true;
                fastEntityAnimation = false;
                break;
                
            case QUALITY_FOCUSED: 
                // Quality over performance
                entityRenderDistance = 96;
                entityTickDistance = 64;
                particleReduction = 25;
                maxActiveParticles = 8000;
                chunkBuilderThreads = Math. max(2, availableProcessors / 2);
                aggressiveChunkCulling = false;
                cullInvisibleEntities = false;
                fastEntityAnimation = false;
                break;
                
            case CUSTOM:
                // Keep current settings
                break;
        }
        
        this.activeProfile = profile;
    }
    
    /**
     * Validate and fix invalid configuration values
     */
    public void validate() {
        entityRenderDistance = Math.max(16, Math.min(entityRenderDistance, 256));
        entityTickDistance = Math.max(16, Math.min(entityTickDistance, 128));
        particleReduction = Math.max(0, Math.min(particleReduction, 100));
        maxActiveParticles = Math.max(500, Math.min(maxActiveParticles, 20000));
        chunkBuilderThreads = Math.max(0, Math.min(chunkBuilderThreads, 16));
        gcIntervalSeconds = Math.max(60, Math.min(gcIntervalSeconds, 1800));
        memoryThresholdPercent = Math.max(50, Math.min(memoryThresholdPercent, 95));
    }
}
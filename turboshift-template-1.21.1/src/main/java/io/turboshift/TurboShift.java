package io.turboshift;

import io.turboshift.core.ConfigManager;
// REMOVED: OptimizationEngine and PerformanceTracker imports (they are Client-side)
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TurboShift implements ModInitializer {
    public static final String MOD_ID = "turboshift";
    public static final String MOD_NAME = "TurboShift";
    public static final String VERSION = "1.0.0";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    @Override
    public void onInitialize() {
        long startTime = System.currentTimeMillis();
        
        LOGGER.info("╔═══════════════════════════════════════╗");
        LOGGER.info("║     TurboShift Performance Engine     ║");
        LOGGER.info("║          Initializing v{}...          ║", VERSION);
        LOGGER.info("╚═══════════════════════════════════════╝");
        
        // Load configuration (Safe for Main because it's just File I/O)
        ConfigManager.initialize();
        LOGGER.info("✓ Configuration system loaded");
        
        long loadTime = System.currentTimeMillis() - startTime;
        LOGGER.info("TurboShift Common Core loaded in {}ms", loadTime);
    }
    
    public static String getVersion() {
        return VERSION;
    }
}
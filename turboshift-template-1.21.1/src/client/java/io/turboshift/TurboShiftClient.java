package io.turboshift.client;

import io.turboshift.TurboShift;
import io.turboshift.core.OptimizationEngine;
import io.turboshift.core.PerformanceTracker;
import net.fabricmc.api.ClientModInitializer;

public class TurboShiftClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        TurboShift.LOGGER.info("Initializing TurboShift Client Optimizations...");

        // Register optimizations (These are rendering/client-side)
        OptimizationEngine.registerAll();
        TurboShift.LOGGER.info("✓ Optimization engine initialized");
        
        // Initialize performance tracking (Uses MinecraftClient)
        PerformanceTracker.initialize();
        TurboShift.LOGGER.info("✓ Performance tracker ready");

        TurboShift.LOGGER.info("║  Performance Mode:  ENGAGED 🚀         ║");
    }
}
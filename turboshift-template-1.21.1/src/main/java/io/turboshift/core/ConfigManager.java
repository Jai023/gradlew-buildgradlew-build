package io.turboshift. core;

import com.google.gson.Gson;
import com. google.gson.GsonBuilder;
import io.turboshift.TurboShift;
import io.turboshift.config.TurboShiftConfig;

import java.io. File;
import java.io. FileReader;
import java.io.FileWriter;
import java. io.IOException;
import java. nio.file.Files;
import java.nio.file.Path;
import java.nio.file. Paths;

public class ConfigManager {
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .create();
    
    private static final Path CONFIG_DIR = Paths.get("config", "turboshift");
    private static final File CONFIG_FILE = CONFIG_DIR.resolve("turboshift.json").toFile();
    
    private static TurboShiftConfig config;
    
    public static void initialize() {
        try {
            Files.createDirectories(CONFIG_DIR);
            load();
            TurboShift.LOGGER. info("Configuration directory:  {}", CONFIG_DIR. toAbsolutePath());
        } catch (IOException e) {
            TurboShift. LOGGER.error("Failed to create config directory", e);
            config = new TurboShiftConfig();
            config.validate();
        }
    }
    
    public static void load() {
        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                config = GSON.fromJson(reader, TurboShiftConfig. class);
                if (config == null) {
                    config = new TurboShiftConfig();
                }
                config.validate();
                TurboShift.LOGGER.info("✓ Configuration loaded:  {}", CONFIG_FILE.getName());
                TurboShift.LOGGER. info("  Active Profile: {}", config.activeProfile. getDescription());
            } catch (Exception e) {
                TurboShift.LOGGER.error("Failed to load config, using defaults", e);
                config = new TurboShiftConfig();
                config.validate();
                save();
            }
        } else {
            TurboShift.LOGGER. info("No configuration file found, creating defaults.. .");
            config = new TurboShiftConfig();
            config.validate();
            save();
        }
    }
    
    public static void save() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            config.validate();
            GSON.toJson(config, writer);
            TurboShift.LOGGER. info("✓ Configuration saved:  {}", CONFIG_FILE.getName());
        } catch (IOException e) {
            TurboShift.LOGGER.error("Failed to save configuration", e);
        }
    }
    
    public static TurboShiftConfig getConfig() {
        if (config == null) {
            config = new TurboShiftConfig();
            config.validate();
        }
        return config;
    }
    
    public static void setConfig(TurboShiftConfig newConfig) {
        config = newConfig;
        config.validate();
        save();
    }
    
    public static void reload() {
        TurboShift.LOGGER. info("Reloading configuration.. .");
        load();
    }
}
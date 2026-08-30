package com.agi.qa.config;

import org.aeonbits.owner.ConfigCache;

/**
 * Single access point to the {@link Configuration}.
 *
 * <p>The instance is cached by Owner, so every caller shares the same
 * resolved configuration without re-reading the sources.
 */
public final class ConfigFactory {

    private ConfigFactory() {
        // Utility class - not instantiable.
    }

    public static Configuration get() {
        return ConfigCache.getOrCreate(Configuration.class);
    }
}

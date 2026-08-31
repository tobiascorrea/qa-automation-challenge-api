package com.agi.qa.config;

import org.aeonbits.owner.ConfigCache;

public final class ConfigFactory {

    private ConfigFactory() {
    }

    public static Configuration get() {
        return ConfigCache.getOrCreate(Configuration.class);
    }
}

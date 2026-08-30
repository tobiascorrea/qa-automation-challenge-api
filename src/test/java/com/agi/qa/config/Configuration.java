package com.agi.qa.config;

import org.aeonbits.owner.Config;

/**
 * Typed, immutable view over the test configuration.
 *
 * <p>Values are resolved with the following precedence (highest first):
 * <ol>
 *     <li>System properties (e.g. {@code -Dbase.uri=...})</li>
 *     <li>Operating system environment variables</li>
 *     <li>{@code config.properties} on the classpath</li>
 * </ol>
 * This precedence lets the same suite run unchanged locally and in CI, where
 * overrides are typically injected as environment variables or JVM flags.
 */
@Config.LoadPolicy(Config.LoadType.MERGE)
@Config.Sources({
        "system:properties",
        "system:env",
        "classpath:config.properties"
})
public interface Configuration extends Config {

    @Key("base.uri")
    String baseUri();

    @Key("base.path")
    String basePath();

    @Key("timeout.connection.ms")
    @DefaultValue("10000")
    int connectionTimeoutMs();

    @Key("timeout.socket.ms")
    @DefaultValue("30000")
    int socketTimeoutMs();

    @Key("http.logging.enabled")
    @DefaultValue("false")
    boolean httpLoggingEnabled();
}

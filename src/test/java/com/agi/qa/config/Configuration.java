package com.agi.qa.config;

import org.aeonbits.owner.Config;

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

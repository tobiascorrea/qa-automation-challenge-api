package com.agi.qa.core;

import com.agi.qa.config.ConfigFactory;
import com.agi.qa.config.Configuration;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

/**
 * Builds the reusable RestAssured request and response specifications.
 *
 * <p>Centralising these specs keeps every test consistent: same base URI,
 * timeouts, Allure attachment filter and logging policy. Tests never repeat
 * this boilerplate, they simply consume the specs.
 */
public final class SpecFactory {

    private static final Configuration CONFIG = ConfigFactory.get();

    private SpecFactory() {
        // Utility class - not instantiable.
    }

    /**
     * @return a request spec pointing at the API base URI + path, wired with
     * timeouts and the Allure filter. Logging is enabled only when configured.
     */
    public static RequestSpecification request() {
        RestAssuredConfig restAssuredConfig = RestAssuredConfig.config()
                .httpClient(HttpClientConfig.httpClientConfig()
                        .setParam("http.connection.timeout", CONFIG.connectionTimeoutMs())
                        .setParam("http.socket.timeout", CONFIG.socketTimeoutMs()));

        RequestSpecBuilder builder = new RequestSpecBuilder()
                .setBaseUri(CONFIG.baseUri())
                .setBasePath(CONFIG.basePath())
                .setConfig(restAssuredConfig)
                .setAccept(ContentType.JSON)
                .addFilter(new AllureRestAssured());

        if (CONFIG.httpLoggingEnabled()) {
            builder.log(LogDetail.ALL);
        }

        return builder.build();
    }

    /**
     * @return a response spec asserting a JSON body, useful as a shared
     * baseline for successful responses.
     */
    public static ResponseSpecification jsonResponse() {
        return new ResponseSpecBuilder()
                .expectContentType(ContentType.JSON)
                .build();
    }
}

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

public final class SpecFactory {

    private static final Configuration CONFIG = ConfigFactory.get();

    private SpecFactory() {
    }

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

    public static ResponseSpecification jsonResponse() {
        return new ResponseSpecBuilder()
                .expectContentType(ContentType.JSON)
                .build();
    }
}

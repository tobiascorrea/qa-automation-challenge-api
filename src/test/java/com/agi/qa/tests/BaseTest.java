package com.agi.qa.tests;

import com.agi.qa.client.BreedsClient;
import com.agi.qa.core.AllureEnvironmentWriter;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;

/**
 * Base class shared by every test.
 *
 * <p>It owns the cross-cutting test setup so the concrete test classes stay
 * focused on scenarios and assertions. The {@link BreedsClient} is exposed as
 * a protected field, keeping the tests decoupled from RestAssured.
 */
public abstract class BaseTest {

    protected final BreedsClient breedsClient = new BreedsClient();

    @BeforeAll
    static void globalSetup() {
        // Fail fast on unexpected content and keep RestAssured resilient to
        // small, non-breaking API changes across the whole suite.
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.useRelaxedHTTPSValidation();

        // Enrich the Allure report with environment metadata and categories.
        AllureEnvironmentWriter.write();
    }
}

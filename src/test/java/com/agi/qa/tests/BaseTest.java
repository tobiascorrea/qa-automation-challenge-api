package com.agi.qa.tests;

import com.agi.qa.client.BreedsClient;
import com.agi.qa.core.AllureEnvironmentWriter;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;

public abstract class BaseTest {

    protected final BreedsClient breedsClient = new BreedsClient();

    @BeforeAll
    static void globalSetup() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.useRelaxedHTTPSValidation();
        AllureEnvironmentWriter.write();
    }
}

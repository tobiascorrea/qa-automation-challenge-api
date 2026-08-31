package com.agi.qa.tests;

import com.agi.qa.model.ApiStatus;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Cross-cutting contract and negative scenarios that are not specific to a
 * single breed endpoint - e.g. hitting unknown routes and confirming the API
 * fails gracefully and consistently.
 */
@Epic("Dog API")
@Feature("API Contract & Negative Scenarios")
@DisplayName("API contract")
@org.junit.jupiter.api.TestInstance(org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS)
class ApiContractTest extends BaseTest {

    @ParameterizedTest(name = "unknown route \"{0}\" returns 404")
    @ValueSource(strings = {
            "/breeds/list/nope",
            "/breed/hound/nope",
            "/this/route/does/not/exist"
    })
    @Story("Unknown routes")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Should return 404 for unknown routes")
    @Description("Any route that is not part of the API surface must fail gracefully with a 404.")
    void shouldReturn404ForUnknownRoutes(String path) {
        Response response = breedsClient.get(path);

        assertThat("HTTP status for " + path, response.statusCode(), is(HttpStatus.SC_NOT_FOUND));
    }

    @Test
    @Story("Unknown routes")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("A 404 error should still expose an error response body when JSON is returned")
    @Description("When the API answers a 404 with a JSON body, it must follow the error contract "
            + "(status=error). Non-JSON 404s are tolerated but the status code is always asserted.")
    void unknownRouteShouldExposeErrorBodyWhenJson() {
        Response response = breedsClient.get("/breed/nonexistentbreed/images");

        assertThat("HTTP status code", response.statusCode(), is(HttpStatus.SC_NOT_FOUND));

        String contentType = response.getContentType();
        if (contentType != null && contentType.contains("application/json")) {
            assertThat("status", response.jsonPath().getString("status"), is(ApiStatus.ERROR.value()));
            assertThat("message", response.jsonPath().getString("message"), notNullValue());
        }
    }

    @ParameterizedTest(name = "{0} responds 200 with JSON content-type")
    @MethodSource("successfulEndpoints")
    @Story("Response headers")
    @Severity(SeverityLevel.MINOR)
    @DisplayName("Every successful endpoint should be served as JSON with 200")
    @Description("Cross-endpoint content-type contract: each success endpoint returns 200 and application/json.")
    void successfulEndpointsShouldBeJson(String label, Supplier<Response> call) {
        Response response = call.get();

        assertThat("HTTP status for " + label, response.statusCode(), is(HttpStatus.SC_OK));
        assertThat("content type present for " + label, response.getContentType(), notNullValue());
        assertThat("content type contains application/json for " + label,
                response.getContentType().contains("application/json"), is(true));
    }

    private Stream<Arguments> successfulEndpoints() {
        return Stream.of(
                Arguments.of("GET /breeds/list/all", (Supplier<Response>) breedsClient::listAllBreeds),
                Arguments.of("GET /breed/hound/images", (Supplier<Response>) () -> breedsClient.getBreedImages("hound")),
                Arguments.of("GET /breeds/image/random", (Supplier<Response>) breedsClient::getRandomImage)
        );
    }
}

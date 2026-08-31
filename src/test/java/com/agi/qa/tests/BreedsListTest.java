package com.agi.qa.tests;

import com.agi.qa.model.ApiStatus;
import com.agi.qa.model.BreedsListResponse;
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

import java.util.List;
import java.util.Map;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

@Epic("Dog API")
@Feature("Breeds Listing")
@DisplayName("GET /breeds/list/all")
class BreedsListTest extends BaseTest {

    @Test
    @Story("List all breeds")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Should return 200 with a JSON body and success status")
    @Description("Verifies the endpoint responds successfully and follows the expected response structure.")
    void shouldReturnSuccessfulResponse() {
        Response response = breedsClient.listAllBreeds();

        assertThat("HTTP status code", response.statusCode(), is(HttpStatus.SC_OK));
        assertThat("Content type", response.getContentType(), notNullValue());
        assertThat("Body 'status' field",
                response.jsonPath().getString("status"), is(ApiStatus.SUCCESS.value()));
    }

    @Test
    @Story("List all breeds")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Should return a non-empty map of breeds")
    @Description("The 'message' object must contain at least one breed, deserialized into a typed model.")
    void shouldReturnNonEmptyBreedMap() {
        Response response = breedsClient.listAllBreeds();

        BreedsListResponse body = response.as(BreedsListResponse.class);
        Map<String, List<String>> breeds = body.getMessage();

        assertThat("status", body.getStatus(), is(ApiStatus.SUCCESS.value()));
        assertThat("number of breeds", breeds.size(), is(greaterThan(0)));
    }

    @Test
    @Story("List all breeds")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Should include well-known breeds")
    @Description("Sanity-checks the payload by asserting the presence of stable, well-known breeds.")
    void shouldContainWellKnownBreeds() {
        Response response = breedsClient.listAllBreeds();
        Map<String, ?> breeds = response.jsonPath().getMap("message");

        assertThat(breeds, hasKey("hound"));
        assertThat(breeds, hasKey("bulldog"));
        assertThat(breeds, hasKey("retriever"));
    }

    @Test
    @Story("List all breeds")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Sub-breeds of 'bulldog' should be a valid, non-blank list")
    @Description("Each breed maps to a list of sub-breeds; when present, entries must be non-blank strings.")
    void bulldogShouldExposeValidSubBreeds() {
        Response response = breedsClient.listAllBreeds();

        List<String> subBreeds = response.jsonPath().getList("message.bulldog");

        assertThat("bulldog sub-breeds", subBreeds, notNullValue());
        assertThat("bulldog sub-breeds count", subBreeds.size(), is(greaterThan(0)));
        assertThat("no blank sub-breed names", subBreeds, everyItem(not(is(""))));
    }

    @Test
    @Story("List all breeds")
    @Severity(SeverityLevel.MINOR)
    @DisplayName("Should respond within an acceptable time")
    @Description("Guards against performance regressions on the listing endpoint.")
    void shouldRespondWithinAcceptableTime() {
        Response response = breedsClient.listAllBreeds();

        assertThat("response time (ms)", response.time(), is(lessThan(5000L)));
    }

    @Test
    @Story("List all breeds")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Response should match the JSON schema contract")
    @Description("Locks the response contract: breeds map + success status, validated against a JSON schema.")
    void shouldMatchJsonSchema() {
        breedsClient.listAllBreeds()
                .then()
                .body(matchesJsonSchemaInClasspath("schemas/breeds-list-schema.json"));
    }
}

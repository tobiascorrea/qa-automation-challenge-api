package com.agi.qa.tests;

import com.agi.qa.model.ApiStatus;
import com.agi.qa.model.ImageListResponse;
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
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.notNullValue;

@Epic("Dog API")
@Feature("Breed Images")
@DisplayName("GET /breed/{breed}/images")
class BreedImagesTest extends BaseTest {

    private static final String IMAGE_URL_PATTERN = "^https?://.+\\.(jpg|jpeg|png|gif|webp|bmp)$";

    @Test
    @Story("Fetch images by breed")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Should return 200 and a non-empty list of image URLs for a valid breed")
    @Description("Happy path: a known breed returns a success response with a populated image list.")
    void shouldReturnImagesForValidBreed() {
        Response response = breedsClient.getBreedImages("hound");

        assertThat("HTTP status code", response.statusCode(), is(HttpStatus.SC_OK));

        ImageListResponse body = response.as(ImageListResponse.class);
        assertThat("status", body.getStatus(), is(ApiStatus.SUCCESS.value()));
        assertThat("image list", body.getMessage(), notNullValue());
        assertThat("image count", body.getMessage().size(), is(greaterThan(0)));
    }

    @Test
    @Story("Fetch images by breed")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Response should match the JSON schema contract")
    @Description("Locks the response contract: array of image URLs + success status.")
    void shouldMatchJsonSchema() {
        breedsClient.getBreedImages("hound")
                .then()
                .body(matchesJsonSchemaInClasspath("schemas/image-list-schema.json"));
    }

    @Test
    @Story("Fetch images by breed")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Every returned image should be a valid image URL")
    @Description("Validates the data format: each entry must be an http(s) URL ending with an image extension.")
    void shouldReturnValidImageUrls() {
        Response response = breedsClient.getBreedImages("hound");

        List<String> images = response.jsonPath().getList("message");

        assertThat(images, everyItem(matchesPattern(IMAGE_URL_PATTERN)));
    }

    @ParameterizedTest(name = "breed \"{0}\" returns images")
    @ValueSource(strings = {"hound", "bulldog", "retriever", "beagle", "poodle"})
    @Story("Fetch images by breed")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Should return images for several well-known breeds")
    @Description("Data-driven coverage across multiple stable breeds.")
    void shouldReturnImagesForMultipleBreeds(String breed) {
        Response response = breedsClient.getBreedImages(breed);

        assertThat("HTTP status for " + breed, response.statusCode(), is(HttpStatus.SC_OK));
        assertThat("images for " + breed,
                response.jsonPath().getList("message").size(), is(greaterThan(0)));
    }

    @Test
    @Story("Fetch images by breed - negative")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Should return 404 and error status for a non-existent breed")
    @Description("Negative scenario: an unknown breed must fail gracefully with a 404 and an error response body.")
    void shouldReturn404ForNonExistentBreed() {
        Response response = breedsClient.getBreedImages("nonexistentbreed");

        assertThat("HTTP status code", response.statusCode(), is(HttpStatus.SC_NOT_FOUND));
        assertThat("status", response.jsonPath().getString("status"), is(ApiStatus.ERROR.value()));
        assertThat("error message", response.jsonPath().getString("message"), notNullValue());
    }

    @ParameterizedTest(name = "{1}")
    @CsvSource({
            "'golden retriever', breed name with a space",
            "'12345',            numeric breed name",
            "'houndxyz',         unknown breed similar to a real one",
            "'!@#',              breed with special characters"
    })
    @Story("Fetch images by breed - negative")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Should return 404 for invalid breed inputs")
    @Description("Data-driven negative coverage: several malformed or unknown breed inputs must all fail with 404.")
    void shouldReturn404ForInvalidBreedInputs(String breed, String scenario) {
        Response response = breedsClient.getBreedImages(breed);

        assertThat("HTTP status for [" + scenario + "]",
                response.statusCode(), is(HttpStatus.SC_NOT_FOUND));
    }

    @Test
    @Story("Fetch images by breed - negative")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Error response should honor the JSON error contract")
    @Description("Beyond the status code, the 404 body must be JSON with status=error and a non-null message.")
    void errorResponseShouldHonorContract() {
        Response response = breedsClient.getBreedImages("nonexistentbreed");

        assertThat("HTTP status code", response.statusCode(), is(HttpStatus.SC_NOT_FOUND));
        assertThat("content type", response.getContentType(), notNullValue());
        assertThat("content type is JSON", response.getContentType(), matchesPattern(".*application/json.*"));
        assertThat("status", response.jsonPath().getString("status"), is(ApiStatus.ERROR.value()));
        assertThat("error message", response.jsonPath().getString("message"), notNullValue());
    }

    @ParameterizedTest(name = "\"{0}\" is treated case-insensitively")
    @ValueSource(strings = {"HOUND", "Hound", "hOuNd"})
    @Story("Fetch images by breed")
    @Severity(SeverityLevel.MINOR)
    @DisplayName("Breed lookup should be case-insensitive")
    @Description("Documented behavior check: the API resolves breed names regardless of case, "
            + "returning 200 with a populated image list.")
    void breedLookupShouldBeCaseInsensitive(String breed) {
        Response canonical = breedsClient.getBreedImages("hound");
        Response variant = breedsClient.getBreedImages(breed);

        assertThat("HTTP status for '" + breed + "'", variant.statusCode(), is(HttpStatus.SC_OK));
        assertThat("status for '" + breed + "'",
                variant.jsonPath().getString("status"), equalToIgnoringCase(ApiStatus.SUCCESS.value()));
        assertThat("image count for '" + breed + "'",
                variant.jsonPath().getList("message").size(), is(greaterThan(0)));
        assertThat("same dataset size as canonical 'hound'",
                variant.jsonPath().getList("message").size(),
                is(canonical.jsonPath().getList("message").size()));
    }
}

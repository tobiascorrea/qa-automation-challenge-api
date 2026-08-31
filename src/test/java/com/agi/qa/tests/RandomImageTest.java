package com.agi.qa.tests;

import com.agi.qa.model.ApiStatus;
import com.agi.qa.model.RandomImageResponse;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;

@Epic("Dog API")
@Feature("Random Image")
@DisplayName("GET /breeds/image/random")
class RandomImageTest extends BaseTest {

    private static final String IMAGE_URL_PATTERN = "^https?://.+\\.(jpg|jpeg|png|gif|webp|bmp)$";

    @Test
    @Story("Fetch a random image")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Should return 200 with a success status and a single image URL")
    @Description("Happy path for the random image endpoint, validated through a typed model.")
    void shouldReturnRandomImage() {
        Response response = breedsClient.getRandomImage();

        assertThat("HTTP status code", response.statusCode(), is(HttpStatus.SC_OK));

        RandomImageResponse body = response.as(RandomImageResponse.class);
        assertThat("status", body.getStatus(), is(ApiStatus.SUCCESS.value()));
        assertThat("image url", body.getMessage(), is(not(emptyOrNullString())));
        assertThat("image url is https", body.getMessage(), startsWith("https://"));
    }

    @Test
    @Story("Fetch a random image")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Response should match the JSON schema contract")
    @Description("Locks the response contract: single image URL string + success status.")
    void shouldMatchJsonSchema() {
        breedsClient.getRandomImage()
                .then()
                .body(matchesJsonSchemaInClasspath("schemas/random-image-schema.json"));
    }

    @Test
    @Story("Fetch a random image")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("The returned URL should be a valid image URL")
    @Description("Validates the data format of the single image URL returned.")
    void shouldReturnValidImageUrl() {
        Response response = breedsClient.getRandomImage();

        assertThat(response.jsonPath().getString("message"), matchesPattern(IMAGE_URL_PATTERN));
    }

    @RepeatedTest(value = 3, name = "randomness check {currentRepetition}/{totalRepetitions}")
    @Story("Fetch a random image")
    @Severity(SeverityLevel.MINOR)
    @DisplayName("Repeated calls should each return a valid image")
    @Description("Exercises the endpoint several times to confirm it consistently returns valid images.")
    void repeatedCallsShouldReturnValidImages() {
        Response response = breedsClient.getRandomImage();

        assertThat(response.statusCode(), is(HttpStatus.SC_OK));
        assertThat(response.jsonPath().getString("message"), matchesPattern(IMAGE_URL_PATTERN));
    }
}

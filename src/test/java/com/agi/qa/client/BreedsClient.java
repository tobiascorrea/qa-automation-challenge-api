package com.agi.qa.client;

import com.agi.qa.core.Endpoints;
import com.agi.qa.core.SpecFactory;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

/**
 * Service client for the Dog API breed endpoints.
 *
 * <p>This is the <b>only</b> layer that talks to RestAssured. Tests interact
 * with the API exclusively through these domain-oriented methods, which keeps
 * the HTTP concerns (base URI, paths, specs, filters) out of the test logic
 * and makes the suite easy to maintain and evolve.
 *
 * <p>Each method returns the raw {@link Response} so the test layer stays in
 * charge of assertions, while the {@code @Step} annotations enrich the Allure
 * report with a readable, business-level call log.
 */
public class BreedsClient {

    /**
     * {@code GET /breeds/list/all} - retrieve every breed and its sub-breeds.
     */
    @Step("List all breeds")
    public Response listAllBreeds() {
        return given()
                .spec(SpecFactory.request())
                .when()
                .get(Endpoints.BREEDS_LIST_ALL)
                .then()
                .extract()
                .response();
    }

    /**
     * {@code GET /breed/{breed}/images} - retrieve all images for a breed.
     *
     * @param breed breed name as used by the API (e.g. {@code hound})
     */
    @Step("Get images for breed '{breed}'")
    public Response getBreedImages(String breed) {
        return given()
                .spec(SpecFactory.request())
                .pathParam("breed", breed)
                .when()
                .get(Endpoints.BREED_IMAGES)
                .then()
                .extract()
                .response();
    }

    /**
     * {@code GET /breeds/image/random} - retrieve a single random image.
     */
    @Step("Get a random dog image")
    public Response getRandomImage() {
        return given()
                .spec(SpecFactory.request())
                .when()
                .get(Endpoints.RANDOM_IMAGE)
                .then()
                .extract()
                .response();
    }
}

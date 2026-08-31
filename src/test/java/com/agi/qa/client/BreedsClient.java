package com.agi.qa.client;

import com.agi.qa.core.Endpoints;
import com.agi.qa.core.SpecFactory;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class BreedsClient {

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

    @Step("GET arbitrary path '{path}'")
    public Response get(String path) {
        return given()
                .spec(SpecFactory.request())
                .when()
                .get(path)
                .then()
                .extract()
                .response();
    }
}

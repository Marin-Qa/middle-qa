package com.example.test.integration.utils;

import com.example.base.AbstractIntegrationTest;
import com.example.endpoint.user.EndpointUser;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class DeleteUserUtil {

    private final RequestSpecification spec;

    public DeleteUserUtil(RequestSpecification spec) {
        this.spec = spec;
    }

    public void deleteUSer(int id) {
        given()
                .spec(spec)
                .pathParam("id", id)
                .when()
                .delete(EndpointUser.DELETE)
                .then()
                .statusCode(204);

    }
}

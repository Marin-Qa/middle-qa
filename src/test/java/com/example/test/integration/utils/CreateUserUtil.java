package com.example.test.integration.utils;

import com.example.base.AbstractIntegrationTest;
import com.example.endpoint.user.EndpointUser;
import io.restassured.specification.RequestSpecification;

import java.util.Map;

import static io.restassured.RestAssured.given;


public class CreateUserUtil extends AbstractIntegrationTest {

    private final RequestSpecification spec;

    public CreateUserUtil(RequestSpecification spec) {
        this.spec = spec;
    }
    @SuppressWarnings("unchecked")
    public Map<String, Object>  createUser(){

        Map<String, String> userRequest = Map.of(
                "firstName", "firstName",
                "lastName", "lastName",
                "job", "job",
                "email", "email"
        );

        return given()
                .spec(spec)
                .body(userRequest)
                .when()
                .post(EndpointUser.CREATE)
                .then()
                .statusCode(200)
                .extract().as(Map.class);
    }
}

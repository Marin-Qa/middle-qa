package com.example.test.integration.utils;

import com.example.base.AbstractIntegrationTest;
import com.example.endpoint.user.EndpointUser;
import io.restassured.specification.RequestSpecification;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class GetUserUtil extends AbstractIntegrationTest {

    private final RequestSpecification spec;

    public GetUserUtil(RequestSpecification spec) {
        this.spec = spec;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getUser(int id){
        return given()
                .spec(spec)
                .pathParams("id", id)
                .when()
                .get(EndpointUser.USERS_BY_ID)
                .then()
                .statusCode(200)
                .extract().as(Map.class);
    }
}

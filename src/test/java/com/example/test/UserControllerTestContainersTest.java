package com.example.test;

import com.example.base.AbstractTestContainersIntegrationTest;
import com.example.dto.user.UserCreateRequest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class UserControllerTestContainersTest extends AbstractTestContainersIntegrationTest {

    @Test
    void createUser_shouldReturnCreatedUser() {
        UserCreateRequest request = new UserCreateRequest("John","Doe","QA","john.doe@example.com");

        given()
                .contentType(ContentType.JSON)
                .body(request) // RestAssured + Jackson сериализует record
        .when()
                .post("/api/users/create")
        .then()
                .statusCode(200)
                .body("firstName", equalTo("John"))
                .body("lastName", equalTo("Doe"));
    }

    @Test
    void getUserById_shouldReturnUser() {
        // Создаём пользователя
        UserCreateRequest request = new UserCreateRequest("Jane","Doe","Dev","jane.doe@example.com");

        var id = given()
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post("/api/users/create")
        .then()
                .extract()
                .path("id");

        // Проверяем через GET
        given()
        .when()
                .get("/api/users/{id}", id)
        .then()
                .statusCode(200)
                .body("firstName", equalTo("Jane"))
                .body("lastName", equalTo("Doe"));
    }
}

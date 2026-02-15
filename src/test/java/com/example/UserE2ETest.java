package com.example;

import com.example.base.AbstractTestContainersIntegrationTest;
import com.example.dto.user.UserCreateRequest;
import com.example.dto.user.UserUpdateRequest;
import com.example.endpoint.user.EndpointUser;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@Tag("e2e")
public class UserE2ETest extends AbstractTestContainersIntegrationTest {

    @RepeatedTest(10)
    void fullUserWorkflow() {
        // Синхронизация пользователей
        given()
                .contentType(ContentType.JSON)
                .queryParam("limit", 100)
        .when()
                .post(EndpointUser.SYNC)
        .then()
                .statusCode(200)
                .body("users", hasSize(100))
                .body("total", equalTo(100));

        // Получаем id пользователя из существующих
        int id = given()
                .contentType(ContentType.JSON)
        .when()
                .get(EndpointUser.USERS)
        .then()
                .statusCode(200)
                .extract()
                .path("data[0].id");

        // Обновляем пользователя id=1
        UserUpdateRequest updateRequest = new UserUpdateRequest(
                "UpdatedFirst",
                "UpdatedLast",
                "UpdatedJob",
                "updated.email@example.com"
        );

        given()
                .contentType(ContentType.JSON)
                .pathParams("id", id)
                .body(updateRequest)
        .when()
                .put(EndpointUser.PUT)
        .then()
                .statusCode(200)
                .body("firstName", equalTo("UpdatedFirst"))
                .body("lastName", equalTo("UpdatedLast"));

        // Проверяем что user изменен
        given()
                .contentType(ContentType.JSON)
                .pathParams("id", id)
        .when()
                .get(EndpointUser.USER_BY_ID)
        .then()
                .statusCode(200)
                .body("firstName", equalTo("UpdatedFirst"))
                .body("lastName", equalTo("UpdatedLast"));


        // Создаем нового пользователя
        UserCreateRequest createRequest = new UserCreateRequest(
                "John",
                "Doe",
                "QA",
                "john.doe@example.com"
        );

        int newUserId =
        given()
                .contentType(ContentType.JSON)
                .body(createRequest)
        .when()
                .post(EndpointUser.CREATE)
        .then()
                .statusCode(200)
                .body("firstName", equalTo("John"))
                .body("lastName", equalTo("Doe"))
                .extract()
                .path("id");

        // Удаляем созданного пользователя
        given()
                .contentType(ContentType.JSON)
                .pathParams("id", newUserId)
        .when()
                .delete(EndpointUser.DELETE)
        .then()
                .statusCode(204);

        // Проверяем что пользователь удален
        given()
                .contentType(ContentType.JSON)
                .pathParams("id", newUserId)
        .when()
                .get(EndpointUser.USER_BY_ID)
        .then()
                .statusCode(404);
    }
}

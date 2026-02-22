package com.example.test.integration;

import com.example.base.AbstractIntegrationTest;
import com.example.constants.endpoints.user.EndpointUser;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@Tag("integration")
@DisplayName("Интеграционные тесты с реальной бд (в этом случае с h2)")
@Story("Integration")
public class GetUsersTest extends AbstractIntegrationTest {

    @Test
    @DisplayName("Получение всех пользователей из БД")
    void getUsers_shouldReturnListOfUsers(){

        given()
                .spec(requestSpecification)
                .queryParam("limit",10)
        .when()
                .get(EndpointUser.USERS)
        .then()

                .statusCode(200)

                .body("users_total", notNullValue())
                .body("users_total", greaterThan(0))

                .body("data", notNullValue())
                .body("data.size()", greaterThan(0))

                .body("data[0].keySet()", containsInAnyOrder(
                        "id",
                        "firstName",
                        "lastName",
                        "job",
                        "email"
                ))

                .body("data.id", everyItem(notNullValue()))
                .body("data.firstName", everyItem(allOf(notNullValue(), not(emptyString()))))
                .body("data.lastName", everyItem(allOf(notNullValue(), not(emptyString()))))
                .body("data.job", everyItem(allOf(notNullValue(), not(emptyString()))))
                .body("data.email", everyItem(allOf(notNullValue(), not(emptyString()))));
    }
}

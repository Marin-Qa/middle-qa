package com.example.test.integration;

import com.example.base.AbstractIntegrationTest;
import com.example.constants.endpoints.user.EndpointUser;
import com.example.constants.request.QueryParamsName;
import com.example.constants.services.ServiceName;
import com.example.utils.rest.RestUtil;
import io.qameta.allure.Allure;
import io.qameta.allure.Description;
import io.qameta.allure.Owner;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import static org.hamcrest.Matchers.*;

@Tag("integration")
@DisplayName("Интеграционные тесты с реальной бд (в этом случае с h2)")
@Story("Поиск пользователей пол кол-ву")
public class GetUsersTest extends AbstractIntegrationTest {

    @Autowired
    RestUtil rest;

    @Test
    @DisplayName("Получение нескольких пользователей из БД")
    @Description("Проверяем получение нескольких пользователей. С реальной бд (в данном случае h2)")
    @Owner("Marin")
    void getUsers_shouldReturnListOfUsers() {

        Response users = Allure.step("Шаг 1. Выполняем запрос " + EndpointUser.USERS, () ->
                rest.serviceName(ServiceName.USER_MANAGEMENT)
                        .get(EndpointUser.USERS)
                        .queryParam(QueryParamsName.LIMIT, "10")
                        .send()
                );

        users.then()

                .statusCode(HttpStatus.OK.value())

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

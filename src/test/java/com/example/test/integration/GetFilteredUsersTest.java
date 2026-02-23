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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@Tag("integration")
@DisplayName("Интеграционные тесты с реальной бд (в этом случае с h2)")
@Story("Поски пользователя по идентификаторам")
public class GetFilteredUsersTest extends AbstractIntegrationTest {

    @Autowired
    RestUtil rest;

    @ParameterizedTest
    @DisplayName("Поиск по идентификаторам пользователя")
    @Description("Проверяем по идентификаторам пользователя. С реальной бд (в данном случае h2)")
    @MethodSource("getUsersByIdent")
    @Owner("Marin")
    void getUsers_shouldReturnListOfUsers_whenUserIdent(String queryName, String queryValue) {

        Response userByIdent = Allure.step("Шаг 1. Выполняем запрос " + EndpointUser.FILTERED, () ->
                rest.serviceName(ServiceName.USER_MANAGEMENT)
                        .get(EndpointUser.FILTERED)
                        .queryParam(queryName, queryValue)
                        .send()
        );

        userByIdent.then()
                .statusCode(HttpStatus.OK.value())

                .body("page", notNullValue())
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

    static Stream<Arguments> getUsersByIdent() {
        return Stream.of(
                arguments("domain", "x.dummyjson.com"),
                arguments("firstName", "Emily"),
                arguments("lastName", "Johnson"),
                arguments("job", "QA")
        );
    }

    @ParameterizedTest
    @DisplayName("Поиск по id")
    @Description("Проверяем по id пользователя. С реальной бд (в данном случае h2)")
    @MethodSource("getUsersWhenFilterByIDs")
    @Owner("Marin")
    void getUsers_shouldReturnUser_whenFilterByID(int queryValue){

        Response userById = Allure.step("Шаг 1. Выполняем запрос " + EndpointUser.FILTERED, () ->
                rest.serviceName(ServiceName.USER_MANAGEMENT)
                        .get(EndpointUser.FILTERED)
                        .queryParam(QueryParamsName.ID, String.valueOf(queryValue))
                        .send()
        );

        userById.then()

                .statusCode(HttpStatus.OK.value())

                .body("page", notNullValue())
                .body("users_total", notNullValue())
                .body("users_total", equalTo(1))

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

    static Stream<Arguments> getUsersWhenFilterByIDs() {
        Random random = new Random();
        int count = 5;
        return IntStream.range(0, count)
                .mapToObj(i -> Arguments.of((random.nextInt(100) + 1)));
    }

    @ParameterizedTest
    @DisplayName("Проверка пагинации пользователей")
    @Description("Проверяем пагинацию пользователей. С реальной бд (в данном случае h2)")
    @MethodSource("paginationArguments")
    @Owner("Marin")
    void getUsers_shouldReturnCorrectPage_whenPagination(int page, int size) {

        Response users = Allure.step("Шаг 1. Выполняем запрос " + EndpointUser.FILTERED, () ->
                rest.serviceName(ServiceName.USER_MANAGEMENT)
                        .get(EndpointUser.FILTERED)
                        .queryParam(QueryParamsName.PAGE, String.valueOf(page))
                        .queryParam(QueryParamsName.SIZE, String.valueOf(size))
                        .send()
        );

        users.then()
                .statusCode(HttpStatus.OK.value())

                .body("page", equalTo(page))
                .body("users_total", notNullValue())
                .body("data", notNullValue())
                .body("data.size()", lessThanOrEqualTo(size))

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

    static Stream<Arguments> paginationArguments() {
        return Stream.of(
                arguments(0, 5),
                arguments(0, 10),
                arguments(1, 5),
                arguments(2, 3)
        );
    }
}

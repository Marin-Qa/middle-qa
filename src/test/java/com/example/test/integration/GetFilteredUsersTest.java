package com.example.test.integration;

import com.example.base.AbstractIntegrationTest;
import com.example.constants.endpoints.user.EndpointUser;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@Tag("integration")
@DisplayName("Интеграционные тесты с реальной бд (в этом случае с h2)")
@Story("Integration")
public class GetFilteredUsersTest extends AbstractIntegrationTest {

    @ParameterizedTest
    @DisplayName("Поиск по идентификаторам пользователя")
    @MethodSource("getUsersByIdent")
    void getUsers_shouldReturnListOfUsers_whenUserIdent(String queryName, String queryValue){

        given()
                .spec(requestSpecification)
                .queryParam(queryName, queryValue)
                .when()
                .get(EndpointUser.FILTERED)
                .then()
                .statusCode(200)

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
    @MethodSource("getUsersWhenFilterByIDs")
    void getUsers_shouldReturnUser_whenFilterByID(int queryValue){
        given()
                .spec(requestSpecification)
                .queryParam("id", queryValue)
        .when()
                .get(EndpointUser.FILTERED)
        .then()

                .statusCode(200)

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
    @MethodSource("paginationArguments")
    void getUsers_shouldReturnCorrectPage_whenPagination(int page, int size) {

        given()
                .spec(requestSpecification)
                .queryParam("page", page)
                .queryParam("size", size)
        .when()
                .get(EndpointUser.FILTERED)
        .then()
                .statusCode(200)

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

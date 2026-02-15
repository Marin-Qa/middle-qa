package com.example.test.integration;

import com.example.base.AbstractIntegrationTest;
import com.example.endpoint.user.EndpointUser;
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
public class GetUserById extends AbstractIntegrationTest {

    @ParameterizedTest
    @DisplayName("Получение пользователя по id")
    @MethodSource("getUsersById")
    void getUsers_shouldReturnListOfUsers_whenUserIdent(int id) {

        given()
                .spec(requestSpecification)
                .pathParams("id", id)
        .when()
                .get(EndpointUser.USER_BY_ID)
        .then()

                .statusCode(200)

                .body("$", allOf(
                        hasKey("id"),
                        hasKey("firstName"),
                        hasKey("lastName"),
                        hasKey("job"),
                        hasKey("email")
                ))

                .body("id", notNullValue())
                .body("firstName", allOf(notNullValue(), not(emptyString())))
                .body("lastName", allOf(notNullValue(), not(emptyString())))
                .body("job", allOf(notNullValue(), not(emptyString())))
                .body("email", allOf(notNullValue(), not(emptyString())));
    }

    static Stream<Arguments> getUsersById() {
        Random random = new Random();
        int count = 5;
        return IntStream.range(0, count)
                .mapToObj(i -> Arguments.of(random.nextInt(100) + 1));
    }
}

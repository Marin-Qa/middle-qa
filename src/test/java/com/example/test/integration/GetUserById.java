package com.example.test.integration;

import com.example.base.AbstractIntegrationTest;
import com.example.constants.endpoints.user.EndpointUser;
import com.example.constants.request.PathParamsName;
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

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@Tag("integration")
@DisplayName("Интеграционные тесты с реальной бд (в этом случае с h2)")
@Story("Поиск пользователя по id")
public class GetUserById extends AbstractIntegrationTest {

    @Autowired
    RestUtil rest;

    @ParameterizedTest
    @DisplayName("Получение пользователя по id по pathParam")
    @Description("Проверяем по id по pathParam пользователя. С реальной бд (в данном случае h2)")
    @Owner("Marin")
    @MethodSource("getUsersById")
    void getUsers_shouldReturnListOfUsers_whenUserIdent(int id) {

        Response userById = Allure.step("Шаг 1. Выполняем запрос" + EndpointUser.USER_BY_ID, () ->
                rest.serviceName(ServiceName.USER_MANAGEMENT)
                        .get(EndpointUser.USER_BY_ID)
                        .pathParam(PathParamsName.ID, id)
                        .send()
                );

        userById.then()

                .statusCode(HttpStatus.OK.value())

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

//        given()
//                .spec(requestSpecification)
//                .pathParams("id", id)
//        .when()
//                .get(EndpointUser.USER_BY_ID)
//        .then()
//
//                .statusCode(200)
//
//                .body("$", allOf(
//                        hasKey("id"),
//                        hasKey("firstName"),
//                        hasKey("lastName"),
//                        hasKey("job"),
//                        hasKey("email")
//                ))
//
//                .body("id", notNullValue())
//                .body("firstName", allOf(notNullValue(), not(emptyString())))
//                .body("lastName", allOf(notNullValue(), not(emptyString())))
//                .body("job", allOf(notNullValue(), not(emptyString())))
//                .body("email", allOf(notNullValue(), not(emptyString())));
    }

    static Stream<Arguments> getUsersById() {
        Random random = new Random();
        int count = 5;
        return IntStream.range(0, count)
                .mapToObj(i -> Arguments.of((random.nextInt(100) + 1)));
    }
}

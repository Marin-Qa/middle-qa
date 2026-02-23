package com.example.test.integration;

import com.example.base.AbstractIntegrationTest;
import com.example.constants.endpoints.user.EndpointUser;
import com.example.constants.request.PathParamsName;
import com.example.constants.services.ServiceName;
import com.example.utils.rest.RestUtil;
import com.example.utils.user.GetUserUtil;
import com.example.utils.user.UserUtil;
import io.qameta.allure.Allure;
import io.qameta.allure.Description;
import io.qameta.allure.Owner;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.util.Map;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.hamcrest.Matchers.*;

@Tag("integration")
@DisplayName("Интеграционные тесты с реальной бд (в этом случае с h2)")
@Story("Изменение пользователя")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PutUserTest extends AbstractIntegrationTest {

    private String firstNameGet = "John";
    private String lastNameGet = "Johnson";
    private String jobGet = "QA";
    private String emailGet = "emily@dummy.com";;

    private GetUserUtil getUserUtil;
    private UserUtil userUtil;

    @Autowired
    RestUtil rest;

    @BeforeEach
    void setGetUserUtil(){
        getUserUtil = new GetUserUtil(requestSpecification);
        userUtil = new UserUtil();
    }

    @ParameterizedTest
    @DisplayName("Проверка изменения пользователя")
    @Description("Проверяем изменение пользователя. С реальной бд (в данном случае h2)")
    @MethodSource("putUserValue")
    @Owner("Marin")
    void putUser_shouldPutUSerInDb( String firstName, String lastName, String job, String email) {

        var user = getUserUtil.getUserWithRetry(10,500);
        var id = userUtil.getIdUserFromData(user);

        firstNameGet = user.get("firstName").toString();
        lastNameGet = user.get("lastName").toString();
        jobGet = user.get("job").toString();
        emailGet = user.get("email").toString();

        Map<String, String> userUpdate = Map.of(
                "firstName", firstName,
                "lastName", lastName,
                "job", job,
                "email", email
        );

        Response putUserReq = Allure.step("Шаг 1. Выполняем запрос " + EndpointUser.PUT,() ->
                rest.serviceName(ServiceName.USER_MANAGEMENT)
                        .put(EndpointUser.PUT)
                        .pathParam(PathParamsName.ID, id)
                        .body(userUpdate)
                        .send()
        );

        putUserReq.then()

                .statusCode(HttpStatus.OK.value())

                .body("firstName", equalTo(firstName));

        var putUser = getUserUtil.getUser(id);

        assertThat(putUser.get("id")).as("ID пользователя").isEqualTo(id);
        assertThat(putUser.get("firstName")).as("Имя пользователя").isEqualTo(firstName);
        assertThat(putUser.get("lastName")).as("Фамилия пользователя").isEqualTo(lastName);
        assertThat(putUser.get("job")).as("Должность пользователя").isEqualTo(job);
        assertThat(putUser.get("email")).as("Email пользователя").isEqualTo(email);
    }

     Stream<Arguments> putUserValue() {
        return Stream.of(
                arguments("тест",lastNameGet,jobGet,emailGet),
                arguments(firstNameGet,"тест",jobGet,emailGet),
                arguments(firstNameGet,lastNameGet,"тест", emailGet),
                arguments(firstNameGet,lastNameGet,jobGet,"тест"),

                arguments("тест","тест",jobGet,emailGet),
                arguments(firstNameGet,"тест","тест",emailGet),
                arguments(firstNameGet,lastNameGet,"тест", "тест"),
                arguments("тест",lastNameGet,jobGet,"тест"),

                arguments("тест","тест","тест",emailGet),
                arguments(firstNameGet,"тест","тест","тест"),
                arguments("тест",lastNameGet,"тест", "тест"),
                arguments("тест","тест",jobGet,"тест"),

                arguments("тест","тест","тест","тест")
        );
    }
}

package com.example.test.integration;

import com.example.base.AbstractIntegrationTest;
import com.example.endpoint.user.EndpointUser;
import com.example.test.integration.utils.GetUserUtil;
import com.example.test.integration.utils.UserUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Map;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.hamcrest.Matchers.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PutUserTest extends AbstractIntegrationTest {

    private String firstNameGet = "John";
    private String lastNameGet = "Johnson";
    private String jobGet = "QA";
    private String emailGet = "emily@dummy.com";;

    private GetUserUtil getUserUtil;
    private UserUtil userUtil;

    @BeforeEach
    void setGetUserUtil(){
        getUserUtil = new GetUserUtil(requestSpecification);
        userUtil = new UserUtil();
    }

    @ParameterizedTest
    @DisplayName("Проверка изменения пользователя")
    @MethodSource("putUserValue")
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

         given()
                 .spec(requestSpecification)
                 .pathParams("id", id)
                 .body(userUpdate)
         .when()
                 .put(EndpointUser.PUT)
         .then()

                 .statusCode(200)

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

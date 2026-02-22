package com.example.test.integration;

import com.example.base.AbstractIntegrationTest;
import com.example.constants.endpoints.user.EndpointUser;
import com.example.utils.user.DeleteUserUtil;
import com.example.utils.user.GetUserUtil;
import io.qameta.allure.Story;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Map;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@Tag("integration")
@Story("Services")
public class CreateUserTest extends AbstractIntegrationTest {

    private GetUserUtil getUserUtil;
    private DeleteUserUtil deleteUserUtil;

    @BeforeEach
    void setGetUserUtil(){
        getUserUtil = new GetUserUtil(requestSpecification);
        deleteUserUtil = new DeleteUserUtil(requestSpecification);
    }

    @ParameterizedTest
    @DisplayName("Создание нового пользователя")
    @MethodSource("createUserValues")
    void createUser_shouldReturnCreatedUser(String firstName, String lastName, String job, String email) {

        Map<String, String> userRequest = Map.of(
                "firstName", firstName,
                "lastName", lastName,
                "job", job,
                "email", email
        );

        @SuppressWarnings("unchecked")
        Map<String, Object> createdUser =
                given()
                        .spec(requestSpecification)
                        .body(userRequest)
                .when()
                        .post(EndpointUser.CREATE)
                .then()
                        .statusCode(200)
                        .body("id", notNullValue())
                        .body("firstName", equalTo(firstName))
                        .body("lastName", equalTo(lastName))
                        .body("job", equalTo(job))
                        .body("email", equalTo(email))
                        .extract()
                        .as(Map.class);

        assertThat(createdUser.get("id")).as("ID пользователя").isNotNull();
        assertThat(createdUser.get("firstName")).as("Имя пользователя").isEqualTo(firstName);
        assertThat(createdUser.get("lastName")).as("Фамилия пользователя").isEqualTo(lastName);
        assertThat(createdUser.get("job")).as("Должность пользователя").isEqualTo(job);
        assertThat(createdUser.get("email")).as("Email пользователя").isEqualTo(email);

        int userCreatedId =((Number) createdUser.get("id")).intValue();

        var userCreated = getUserUtil.getUser(userCreatedId);

        assertThat(userCreated.get("id")).as("ID пользователя").isNotNull();
        assertThat(userCreated.get("firstName")).as("Имя пользователя").isEqualTo(firstName);
        assertThat(userCreated.get("lastName")).as("Фамилия пользователя").isEqualTo(lastName);
        assertThat(userCreated.get("job")).as("Должность пользователя").isEqualTo(job);
        assertThat(userCreated.get("email")).as("Email пользователя").isEqualTo(email);

        deleteUserUtil.deleteUSer(userCreatedId);
    }

    // Метод поставки значений для параметризации
    static Stream<Arguments> createUserValues() {
        return Stream.of(
                arguments("Emily", "Johnson", "QA", "emily.johnson@dummy.com"),
                arguments("Anna", "Smith", "Developer", "anna.smith@dummy.com"),
                arguments("John", "Doe", "Manager", "john.doe@dummy.com")
        );
    }
}

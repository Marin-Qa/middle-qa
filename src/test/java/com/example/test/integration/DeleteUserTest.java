package com.example.test.integration;

import com.example.base.AbstractIntegrationTest;
import com.example.constants.endpoints.user.EndpointUser;
import com.example.utils.user.CreateUserUtil;
import com.example.utils.user.GetUserUtil;
import io.qameta.allure.Story;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.notNullValue;

@Tag("integration")
@Story("Services")
public class DeleteUserTest extends AbstractIntegrationTest {

    private GetUserUtil getUserUtil;
    private CreateUserUtil createUserUtil;

    @BeforeEach
    void setUpUtil() {
        getUserUtil = new GetUserUtil(requestSpecification);
        createUserUtil = new CreateUserUtil(requestSpecification);
    }

    @DisplayName("Удаление пользователя по ID")
    @RepeatedTest(10)
    void deleteUser_shouldRemoveUser() {
        var createdUser = createUserUtil.createUser();
        var userIdToDelete = ((Number) createdUser.get("id")).intValue();

        var userBeforeDelete = getUserUtil.getUser(userIdToDelete);
        assertThat(userBeforeDelete).as("Пользователь существует до удаления").isNotNull();
        assertThat(userBeforeDelete.get("id")).as("ID пользователя").isEqualTo(userIdToDelete);

        given()
                .spec(requestSpecification)
                .pathParam("id", userIdToDelete)
        .when()
                .delete(EndpointUser.DELETE)
        .then()
                .statusCode(204);

        given()
                .spec(requestSpecification)
                .pathParam("id", userIdToDelete)
        .when()
                .get(EndpointUser.USER_BY_ID)
        .then()
                .statusCode(404);
    }
}

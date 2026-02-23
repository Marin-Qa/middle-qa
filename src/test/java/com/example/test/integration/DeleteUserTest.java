package com.example.test.integration;

import com.example.base.AbstractIntegrationTest;
import com.example.constants.endpoints.user.EndpointUser;
import com.example.constants.request.PathParamsName;
import com.example.constants.services.ServiceName;
import com.example.utils.rest.RestUtil;
import com.example.utils.user.CreateUserUtil;
import com.example.utils.user.GetUserUtil;
import io.qameta.allure.Allure;
import io.qameta.allure.Description;
import io.qameta.allure.Owner;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.notNullValue;

@Tag("integration")
@DisplayName("Интеграционные тесты с реальной бд (в этом случае с h2)")
@Story("Удаление пользователя")
public class DeleteUserTest extends AbstractIntegrationTest {

    private GetUserUtil getUserUtil;
    private CreateUserUtil createUserUtil;

    @Autowired
    RestUtil rest;

    @BeforeEach
    void setUpUtil() {
        getUserUtil = new GetUserUtil(requestSpecification);
        createUserUtil = new CreateUserUtil(requestSpecification);
    }
    @RepeatedTest(3)
    @DisplayName("Удаление пользователя по ID")
    @Description("Проверяем удаление пользователя по id. С реальной бд (в данном случае h2)")
    @Owner("Marin")
    void deleteUser_shouldRemoveUser() {
        var createdUser = createUserUtil.createUser();
        var userIdToDelete = ((Number) createdUser.get("id")).intValue();

        var userBeforeDelete = getUserUtil.getUser(userIdToDelete);
        assertThat(userBeforeDelete).as("Пользователь существует до удаления").isNotNull();
        assertThat(userBeforeDelete.get("id")).as("ID пользователя").isEqualTo(userIdToDelete);

        Response deleteUserReq = Allure.step("Шаг 1. Выполняем запрос " + EndpointUser.DELETE, () ->
                rest.serviceName(ServiceName.USER_MANAGEMENT)
                        .delete(EndpointUser.DELETE)
                        .pathParam(PathParamsName.ID, userIdToDelete)
                        .send()
        );

        deleteUserReq.then().statusCode(HttpStatus.NO_CONTENT.value());


        given()
                .spec(requestSpecification)
                .pathParam(PathParamsName.ID, userIdToDelete)
        .when()
                .get(EndpointUser.USER_BY_ID)
        .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }
}

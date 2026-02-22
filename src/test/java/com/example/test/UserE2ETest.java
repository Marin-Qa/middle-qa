package com.example.test;

import com.example.base.AbstractTestContainersIntegrationTest;
import com.example.constants.request.PathParamsName;
import com.example.constants.request.QueryParamsName;
import com.example.constants.services.ServiceName;
import com.example.dto.user.UserCreateRequest;
import com.example.dto.user.UserUpdateRequest;
import com.example.constants.endpoints.user.EndpointUser;
import com.example.utils.rest.RestUtil;
import io.qameta.allure.*;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@Tag("e2e")
@Story("Е2Е")
public class UserE2ETest extends AbstractTestContainersIntegrationTest {

    @Autowired
    RestUtil rest;

    @Test
    @DisplayName("Полный успешный путь")
    @Description("Проверяем успешный полный путь. С помощью test-containers. Контейнер используется для бд, postreSql")
    @Owner("Marin")
    @Severity(SeverityLevel.BLOCKER)
    void fullUserWorkflow() {
        Response syncUsersFromContainer =  Allure.step("Шаг 1. Синхронизируем пользователей в БД контейнера", () ->
                rest
                .serviceName(ServiceName.USER_MANAGEMENT)
                        .post(EndpointUser.SYNC)
                        .queryParam(QueryParamsName.LIMIT, "100")
                        .send()
        );

        syncUsersFromContainer.then()
                .statusCode(HttpStatus.OK.value())
                .body("users", hasSize(100))
                .body("total", equalTo(100));

        Response oneUserFromExiting = Allure.step("Шаг 2. Получаем id пользователя из существующих ", () ->
                rest.serviceName(ServiceName.USER_MANAGEMENT)
                        .get(EndpointUser.USERS)
                        .queryParam(QueryParamsName.LIMIT, "1")
                        .send()
                );

        var id = oneUserFromExiting.then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .path("data[0].id");

        Response putUser  = Allure.step("Шаг 3. Обновляем пользователя id", () -> {

            UserUpdateRequest updateRequest = new UserUpdateRequest(
                    "UpdatedFirst",
                    "UpdatedLast",
                    "UpdatedJob",
                    "updated.email@example.com"
            );


            return rest.serviceName(ServiceName.USER_MANAGEMENT)
                    .put(EndpointUser.PUT)
                    .pathParam(PathParamsName.ID, id.toString())
                    .body(updateRequest)
                    .send();
        });

        putUser.then().log().all()
                .statusCode(HttpStatus.OK.value())
                .body("firstName", equalTo("UpdatedFirst"))
                .body("lastName", equalTo("UpdatedLast"));


        Response checkPutUser = Allure.step("Шаг 4. Проверяем, что user изменен", () ->
                rest.serviceName(ServiceName.USER_MANAGEMENT)
                        .get(EndpointUser.USER_BY_ID)
                        .pathParam(PathParamsName.ID, id.toString())
                        .send()
                );

        checkPutUser.then()
                .statusCode(HttpStatus.OK.value())
                .body("firstName", equalTo("UpdatedFirst"))
                .body("lastName", equalTo("UpdatedLast"));

        Response createUSer = Allure.step("Шаг 5. Создаем нового пользователя", () -> {
            UserCreateRequest createRequest = new UserCreateRequest(
                    "John",
                    "Doe",
                    "QA",
                    "john.doe@example.com"
            );
            return rest.serviceName(ServiceName.USER_MANAGEMENT)
                    .post(EndpointUser.CREATE)
                    .body(createRequest)
                    .send();
        });

        var newUserId = createUSer.then()
                .statusCode(HttpStatus.OK.value())
                .body("firstName", equalTo("John"))
                .body("lastName", equalTo("Doe"))
                .extract()
                .path(PathParamsName.ID);

        Response deleteUser = Allure.step("Шаг 6. Удаляем созданного пользователя ", () ->
                rest.serviceName(ServiceName.USER_MANAGEMENT)
                        .delete(EndpointUser.DELETE)
                        .pathParam(PathParamsName.ID, newUserId)
                        .send()
                );

        deleteUser.then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        Response checkDeletedUser = Allure.step("Шаг 7. Проверяем, что пользователь удален", () ->
                rest.serviceName(ServiceName.USER_MANAGEMENT)
                        .get(EndpointUser.USER_BY_ID)
                        .pathParam(PathParamsName.ID, newUserId)
                        .send()
                );

        checkDeletedUser.then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }
}

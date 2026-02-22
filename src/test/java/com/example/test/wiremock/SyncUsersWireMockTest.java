package com.example.test.wiremock;

import com.example.base.AbstractWireMockIntegrationTest;
import com.example.constants.endpoints.stub.UsersStub;
import com.example.constants.endpoints.user.EndpointUser;
import com.example.constants.services.ServiceName;
import com.example.utils.mock.MockUtil;
import com.example.constants.request.QueryParamsName;
import com.example.utils.rest.RestUtil;
import io.qameta.allure.Allure;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.util.Map;

import static java.lang.Integer.parseInt;
import static org.hamcrest.Matchers.equalTo;

@Tag("wiremock")
@DisplayName("Тесты с WireMock")
public class SyncUsersWireMockTest extends AbstractWireMockIntegrationTest {

    @Autowired
    MockUtil mock;
    @Autowired
    RestUtil rest;

    @ParameterizedTest
    @DisplayName("Тест эндпоинта \"/sync\". Мок")
    @CsvSource({
            "2,sync/sync-users-2.json",
            "5,sync/sync-users-5.json",
            "10,sync/sync-users-10.json",
    })
    void syncUsers_shouldReturnSyncedUsers(String limit , String mockFile) {
        Allure.step("Генерация заглушки", () -> mock
                .mock(
                wireMockServer,
                ServiceName.USER_MANAGEMENT,
                "GET",
                UsersStub.FOR_API_USERS_SYNC,
                Map.of(QueryParamsName.LIMIT, limit),
                null,
                mockFile,
                HttpStatus.OK.value())
        );

        Response response = Allure.step("Выполняем запрос к сервису", () ->
                rest
                .serviceName(ServiceName.USER_MANAGEMENT)
                .post(EndpointUser.SYNC)
                .queryParam(QueryParamsName.LIMIT, limit)
                .send()
        );

        response.then()
                .statusCode(HttpStatus.OK.value())
                .body("users.size()", equalTo(parseInt(limit)))
                .body("users[0].firstName", equalTo("Emily"))
                .body("users[1].firstName", equalTo("Michael"));
    }
}

package com.example.test.test_containers;

import com.example.base.AbstractTestContainersIntegrationTest;
import com.example.constants.request.PathParamsName;
import com.example.constants.request.QueryParamsName;
import com.example.constants.services.ServiceName;
import com.example.dto.user.UserCreateRequest;
import com.example.dto.user.UserUpdateRequest;
import com.example.constants.endpoints.user.EndpointUser;
import com.example.utils.user.CreateUserUtil;
import com.example.utils.user.UserUtil;
import com.example.utils.rest.RestUtil;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import static org.hamcrest.Matchers.*;
@Tag("test-containers")
@Story("API")
@DisplayName("Тесты с test-containers")
public class UserManagementTestContainersTest extends AbstractTestContainersIntegrationTest {

    private CreateUserUtil createUserUtil;
    private UserUtil userUtil;
    @Autowired
    private RestUtil rest;

   @BeforeEach
   void setUp(){
       createUserUtil = new CreateUserUtil(requestSpecification);
       userUtil = new UserUtil();
   }

    @Test
    @DisplayName("Создание пользователя")
    @Description("Проверяем создание пользователя. С помощью test-containers. Контейнер используется для бд, postgresql")
    @Owner("Marin")
    @Severity(SeverityLevel.CRITICAL)
    void createUser_shouldReturnCreatedUser() {
        Response createUserReq = Allure.step("Шаг 1. Выполняем запрос " + EndpointUser.CREATE, () -> {
            UserCreateRequest request = new UserCreateRequest(
                    "John","Doe","QA","john.doe@example.com");

            return rest.serviceName(ServiceName.USER_MANAGEMENT)
                    .post(EndpointUser.CREATE)
                    .body(request)
                    .send();
        });

        createUserReq.then()
                .statusCode(HttpStatus.OK.value())
                .body("firstName", equalTo("John"))
                .body("lastName", equalTo("Doe"));
    }

    @Test
    @DisplayName("Получение пользователя")
    @Description("Проверяем получение пользователя по id. С помощью test-containers. Контейнер используется для бд, postgresql")
    @Owner("Marin")
    @Severity(SeverityLevel.CRITICAL)
    void getUserById_shouldReturnUser() {
        var user = createUserUtil.createUser();
        int id = userUtil.getIdUserFromData(user);

        Response getUserReq = Allure.step("Шаг 1. Выполняем запрос "  + EndpointUser.USER_BY_ID, () ->
                rest.serviceName(ServiceName.USER_MANAGEMENT)
                        .get(EndpointUser.USER_BY_ID)
                        .pathParam(PathParamsName.ID, id)
                        .send()
                );

        getUserReq.then()
                .statusCode(HttpStatus.OK.value())
                .body("firstName", equalTo("createdFirstName"))
                .body("lastName", equalTo("createdLastName"));
    }


    @Test
    @DisplayName("Изменение пользователя")
    @Description("Проверяем изменение пользователя. С помощью test-containers. Контейнер используется для бд, postgresql")
    @Owner("Marin")
    @Severity(SeverityLevel.CRITICAL)
    void updateUser_shouldReturnUpdatedUser() {
        var user = createUserUtil.createUser();
        int id = userUtil.getIdUserFromData(user);

        Response putUserReq = Allure.step("Шаг 1. Выполняем запрос "  + EndpointUser.PUT, () -> {
            UserUpdateRequest updateRequest = new UserUpdateRequest(
                    "UpdatedFirst",
                    "UpdatedLast",
                    "UpdatedJob",
                    "updated.email@example.com");

            return rest.serviceName(ServiceName.USER_MANAGEMENT)
                    .put(EndpointUser.PUT)
                    .pathParam(PathParamsName.ID, id)
                    .body(updateRequest)
                    .send();
                }
        );

        putUserReq.then().log().body()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(id))
                .body("firstName", equalTo("UpdatedFirst"))
                .body("lastName", equalTo("UpdatedLast"))
                .body("job", equalTo("UpdatedJob"))
                .body("email", equalTo("updated.email@example.com"));
    }

    @Test
    @DisplayName("Удаление пользователя")
    @Description("Проверяем удаление пользователя. С помощью test-containers. Контейнер используется для бд, postgresql")
    @Owner("Marin")
    @Severity(SeverityLevel.CRITICAL)
    void deleteUser_shouldReturnSuccess() {
        var user = createUserUtil.createUser();
        int id = userUtil.getIdUserFromData(user);

        Response deleteUserReq = Allure.step("Шаг 1. Выполняем запрос "  + EndpointUser.DELETE, () ->

                rest.serviceName(ServiceName.USER_MANAGEMENT)
                            .delete(EndpointUser.DELETE)
                            .pathParam(PathParamsName.ID, id)
                            .send()
        );
        deleteUserReq.then().statusCode(HttpStatus.NO_CONTENT.value());

        Response checkDeleteUserReq = Allure.step("Шаг 2. Выполняем запрос для проверки отсутствия пользователя"  + EndpointUser.USER_BY_ID, () ->

                rest.serviceName(ServiceName.USER_MANAGEMENT)
                        .delete(EndpointUser.USER_BY_ID)
                        .pathParam(PathParamsName.ID, id)
                        .send()
        );

        checkDeleteUserReq.then().statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("Получение пользователя (filtered)")
    @Description("Проверяем получение пользователя (filtered). С помощью test-containers. Контейнер используется для бд, postgresql")
    @Owner("Marin")
    @Severity(SeverityLevel.CRITICAL)
    void getFilteredUsers_withFirstNameFilter_shouldReturnCorrectUser() {
        var user = createUserUtil.createUser();
        int id = userUtil.getIdUserFromData(user);

        String firstName = user.get("firstName").toString();

        Response getFilteredUserReq = Allure.step("Шаг 1. Выполняем запрос "  + EndpointUser.FILTERED, () ->
                rest.serviceName(ServiceName.USER_MANAGEMENT)
                        .get(EndpointUser.FILTERED)
                        .queryParam(QueryParamsName.FIRST_NAME, firstName)
                        .send()
        );

        getFilteredUserReq.then()
                .statusCode(HttpStatus.OK.value())
                .body("data.id", hasItem(id))
                .body("data.firstName", everyItem(equalTo(firstName)));
    }

    @Test
    @DisplayName("Получение пользователя (filtered)")
    @Description("Проверяем получение пользователя (filtered) по 2 параметрам. С помощью test-containers. Контейнер используется для бд, postgresql")
    @Owner("Marin")
    void getFilteredUsers_withJobAndLastNameFilter_shouldReturnCorrectUser() {
        var user = createUserUtil.createUser();
        int id = userUtil.getIdUserFromData(user);

        String lastName = user.get("lastName").toString();
        String job = user.get("job").toString();

        Response getFilteredUserReq = Allure.step("Шаг 1. Выполняем запрос "  + EndpointUser.FILTERED, () ->
                rest.serviceName(ServiceName.USER_MANAGEMENT)
                        .get(EndpointUser.FILTERED)
                        .queryParam(QueryParamsName.LAST_NAME, lastName)
                        .queryParam(QueryParamsName.JOB, job)
                        .send()
        );

        getFilteredUserReq.then()
                .statusCode(HttpStatus.OK.value())
                .body("data.id", hasItem(id))
                .body("data.job", everyItem(equalTo(job)))
                .body("data.lastName", everyItem(equalTo(lastName)));
    }

    @ParameterizedTest
    @DisplayName("Получение пользователя (filtered)")
    @Description("Проверяем получение пользователя (filtered) пагинация (размер0. С помощью test-containers. Контейнер используется для бд, postgresql")
    @Owner("Marin")
    @ValueSource(ints = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15})
    void getFilteredUsers_withPagination_shouldReturnCorrectPageSize(int pageSize) {
       for(int i = 0; i < 20; i++){
           createUserUtil.createUser();
       }

        Response getFilteredUserReq = Allure.step("Шаг 1. Выполняем запрос "  + EndpointUser.FILTERED, () ->
                rest.serviceName(ServiceName.USER_MANAGEMENT)
                        .get(EndpointUser.FILTERED)
                        .queryParam(QueryParamsName.PAGE, "0")
                        .queryParam(QueryParamsName.SIZE, String.valueOf(pageSize))
                        .send()
        );

        getFilteredUserReq.then()
                .statusCode(HttpStatus.OK.value())
                .body("data", hasSize(lessThanOrEqualTo(pageSize)));
    }
}

package com.example.test.test_containers;

import com.example.base.AbstractTestContainersIntegrationTest;
import com.example.constants.services.ServiceName;
import com.example.dto.user.UserCreateRequest;
import com.example.dto.user.UserUpdateRequest;
import com.example.constants.endpoints.user.EndpointUser;
import com.example.utils.user.CreateUserUtil;
import com.example.utils.user.DeleteUserUtil;
import com.example.utils.user.UserUtil;
import com.example.utils.rest.RestUtil;
import io.qameta.allure.Allure;
import io.qameta.allure.Story;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
@Tag("test-containers")
@Story("API")
public class UserManagementTestContainersTest extends AbstractTestContainersIntegrationTest {

    private DeleteUserUtil deleteUserUtil;
    private CreateUserUtil createUserUtil;
    private UserUtil userUtil;
    @Autowired
    private RestUtil rest;

   @BeforeEach
   void setUp(){
       deleteUserUtil = new DeleteUserUtil(requestSpecification);
       createUserUtil = new CreateUserUtil(requestSpecification);
       userUtil = new UserUtil();
   }

    @Test
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
//               .extract().path("id");

    }

    @Test
    void getUserById_shouldReturnUser() {
        var user = createUserUtil.createUser();
        int id = userUtil.getIdUserFromData(user);

        given()
        .when()
                .get(EndpointUser.USER_BY_ID, id)
        .then()
                .statusCode(HttpStatus.OK.value())
                .body("firstName", equalTo("createdFirstName"))
                .body("lastName", equalTo("createdLastName"));

        deleteUserUtil.deleteUSer(id);
    }

    @Test
    void updateUser_shouldReturnUpdatedUser() {
        var user = createUserUtil.createUser();
        int id = userUtil.getIdUserFromData(user);

        UserUpdateRequest updateRequest = new UserUpdateRequest(
                "UpdatedFirst",
                "UpdatedLast",
                "UpdatedJob",
                "updated.email@example.com"
        );

        given()
                .contentType(ContentType.JSON)
                .body(updateRequest)
                .when()
                .put(EndpointUser.USER_BY_ID, id)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("firstName", equalTo("UpdatedFirst"))
                .body("lastName", equalTo("UpdatedLast"))
                .body("job", equalTo("UpdatedJob"))
                .body("email", equalTo("updated.email@example.com"));

        deleteUserUtil.deleteUSer(id);
    }

    @Test
    void deleteUser_shouldReturnSuccess() {
        var user = createUserUtil.createUser();
        int id = userUtil.getIdUserFromData(user);

        // Удаляем
        given()
                .when()
                .delete(EndpointUser.USER_BY_ID, id)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        // Проверяем что пользователь удален
        given()
                .when()
                .get(EndpointUser.USER_BY_ID, id)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void getFilteredUsers_withoutFilters_shouldReturnUsers() {
        var user = createUserUtil.createUser();
        int id = userUtil.getIdUserFromData(user);

        String firstName = user.get("firstName").toString();
        String job = user.get("job").toString();

        given()
                .contentType(ContentType.JSON)
                .queryParam("page", 0)
                .queryParam("size", 1)
                .when()
                .get(EndpointUser.FILTERED)
                .then()
                .statusCode(200)
                .body("data.id", hasItem(id))
                .body("data.firstName", hasItem(firstName))
                .body("data.job", hasItem(job));

        deleteUserUtil.deleteUSer(id);
    }

    @Test
    void getFilteredUsers_withFirstNameFilter_shouldReturnCorrectUser() {
        var user = createUserUtil.createUser();
        int id = userUtil.getIdUserFromData(user);

        String firstName = user.get("firstName").toString();

        given()
                .contentType(ContentType.JSON)
                .queryParam("firstName", firstName)
                .when()
                .get(EndpointUser.FILTERED)
                .then()
                .statusCode(200)
                .body("data.id", hasItem(id))
                .body("data.firstName", everyItem(equalTo(firstName)));

        deleteUserUtil.deleteUSer(id);
    }

    @Test
    void getFilteredUsers_withJobAndLastNameFilter_shouldReturnCorrectUser() {
        var user = createUserUtil.createUser();
        int id = userUtil.getIdUserFromData(user);

        String lastName = user.get("lastName").toString();
        String job = user.get("job").toString();

        given()
                .queryParam("job", job)
                .queryParam("lastName", lastName)
                .when()
                .get(EndpointUser.FILTERED)
                .then()
                .statusCode(200)
                .body("data.id", hasItem(id))
                .body("data.job", everyItem(equalTo(job)))
                .body("data.lastName", everyItem(equalTo(lastName)));

        deleteUserUtil.deleteUSer(id);
    }

    @Test
    void getFilteredUsers_withPagination_shouldReturnCorrectPageSize() {
        var user1 = createUserUtil.createUser();
        var user2 = createUserUtil.createUser();
        int pageSize = 1;

        given()
                .contentType(ContentType.JSON)
                .queryParam("page", 0)
                .queryParam("size", pageSize)
                .when()
                .get(EndpointUser.FILTERED)
                .then()
                .statusCode(200)
                .body("data", hasSize(lessThanOrEqualTo(pageSize)));

        deleteUserUtil.deleteUSer(userUtil.getIdUserFromData(user1));
        deleteUserUtil.deleteUSer(userUtil.getIdUserFromData(user2));
    }


}

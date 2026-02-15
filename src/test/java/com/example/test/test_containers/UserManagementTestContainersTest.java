package com.example.test.test_containers;

import com.example.base.AbstractTestContainersIntegrationTest;
import com.example.dto.user.UserCreateRequest;
import com.example.dto.user.UserUpdateRequest;
import com.example.endpoint.user.EndpointUser;
import com.example.entity.User;
import com.example.test.integration.utils.CreateUserUtil;
import com.example.test.integration.utils.DeleteUserUtil;
import com.example.test.integration.utils.UserUtil;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
@Tag("test-containers")
public class UserManagementTestContainersTest extends AbstractTestContainersIntegrationTest {

    private DeleteUserUtil deleteUserUtil;
    private CreateUserUtil createUserUtil;
    private UserUtil userUtil;

   @BeforeEach
   void setUp(){
       deleteUserUtil = new DeleteUserUtil(requestSpecification);
       createUserUtil = new CreateUserUtil(requestSpecification);
       userUtil = new UserUtil();
   }

    @Test
    void createUser_shouldReturnCreatedUser() {
        UserCreateRequest request = new UserCreateRequest(
                "John","Doe","QA","john.doe@example.com");

        int id = given()
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(EndpointUser.CREATE)
        .then()
                .statusCode(200)
                .body("firstName", equalTo("John"))
                .body("lastName", equalTo("Doe"))
                .extract().path("id");

        deleteUserUtil.deleteUSer(id);
    }

    @Test
    void getUserById_shouldReturnUser() {
        var user = createUserUtil.createUser();
        int id = userUtil.getIdUserFromData(user);

        given()
        .when()
                .get(EndpointUser.USER_BY_ID, id)
        .then()
                .statusCode(200)
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
                .statusCode(200)
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
                .statusCode(204);

        // Проверяем что пользователь удален
        given()
                .when()
                .get(EndpointUser.USER_BY_ID, id)
                .then()
                .statusCode(404);
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

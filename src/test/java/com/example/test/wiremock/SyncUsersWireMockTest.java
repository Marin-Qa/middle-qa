package com.example.test.wiremock;

import com.example.base.AbstractWireMockIntegrationTest;
import com.example.endpoint.user.EndpointUser;
import com.github.tomakehurst.wiremock.client.WireMock;
import io.restassured.http.ContentType;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.given;
import static java.lang.Integer.parseInt;
import static org.hamcrest.Matchers.equalTo;

public class SyncUsersWireMockTest extends AbstractWireMockIntegrationTest {

    @ParameterizedTest
    @CsvSource({
            "2,sync/sync-users-2.json",
            "5,sync/sync-users-5.json",
            "10,sync/sync-users-10.json",
    })
    void syncUsers_shouldReturnSyncedUsers(String limit , String mockFile) {
        wireMockServer.stubFor(get(urlPathEqualTo("/users"))
                .withQueryParam("limit", WireMock.equalTo(limit))
                .willReturn(aResponse()
                        .withHeader("Content-Type", ContentType.JSON.toString())
                        .withBodyFile(mockFile)
                        .withStatus(200)
                ));

        given()
                .contentType(ContentType.JSON)
                .queryParam("limit", limit)
        .when()
                .post(EndpointUser.SYNC)
        .then()
                .statusCode(200)
                .body("users.size()", equalTo(parseInt(limit)))
                .body("users[0].firstName", equalTo("Emily"))
                .body("users[1].firstName", equalTo("Michael"));
    }
}

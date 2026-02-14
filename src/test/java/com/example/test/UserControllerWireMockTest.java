package com.example.test;

import com.example.base.AbstractWireMockIntegrationTest;
import com.github.tomakehurst.wiremock.client.WireMock;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class UserControllerWireMockTest extends AbstractWireMockIntegrationTest {

    @Test
    void syncUsers_shouldReturnSyncedUsers() {
        // Мокаем внешний API DummyJSON
        wireMockServer.stubFor(get(urlPathEqualTo("/users"))
                .withQueryParam("limit", WireMock.equalTo("10"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", ContentType.JSON.toString())
                        .withBody("""
                                    {
                                      "users": [
                                        {"id":1,"firstName":"Alice","lastName":"Smith","job":"QA Engineer","domain":"alice.example.com"},
                                        {"id":2,"firstName":"Bob","lastName":"Johnson","job":"DevOps Engineer","domain":"bob.example.com"},
                                        {"id":3,"firstName":"Carol","lastName":"Williams","job":"Product Manager","domain":"carol.example.com"},
                                        {"id":4,"firstName":"David","lastName":"Brown","job":"Backend Developer","domain":"david.example.com"},
                                        {"id":5,"firstName":"Emma","lastName":"Jones","job":"Frontend Developer","domain":"emma.example.com"},
                                        {"id":6,"firstName":"Frank","lastName":"Garcia","job":"QA Analyst","domain":"frank.example.com"},
                                        {"id":7,"firstName":"Grace","lastName":"Miller","job":"UX Designer","domain":"grace.example.com"},
                                        {"id":8,"firstName":"Henry","lastName":"Davis","job":"Business Analyst","domain":"henry.example.com"},
                                        {"id":9,"firstName":"Isabel","lastName":"Martinez","job":"Scrum Master","domain":"isabel.example.com"},
                                        {"id":10,"firstName":"Jack","lastName":"Wilson","job":"Support Engineer","domain":"jack.example.com"}
                                      ],
                                      "total": 10
                                    }
                                    """)
                        .withStatus(200)
                ));

        given()
                .contentType(ContentType.JSON)
                .queryParam("limit", "10")
        .when()
                .post("/api/users/sync")
        .then()
                .statusCode(200)
                .body("users.size()", equalTo(10))
                .body("users[0].firstName", equalTo("Alice"))
                .body("users[1].firstName", equalTo("Bob"));
    }
}

package com.example.base;

import com.example.Application;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;


import static io.restassured.RestAssured.given;

@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public abstract class AbstractIntegrationTest {

    @Value("${db.port}")
    protected int port;

    @Value("${url.url}")
    protected String url;

    protected RequestSpecification requestSpecification;

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = url;
        RestAssured.port = port;
        requestSpecification = given().contentType(ContentType.JSON);
    }
}

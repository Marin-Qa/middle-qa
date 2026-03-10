package com.example.base;

import static io.restassured.RestAssured.given;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import com.example.Application;
import com.example.constants.endpoints.user.EndpointUser;
import com.example.constants.request.QueryParamsName;
import com.example.constants.services.ServiceName;
import com.example.utils.rest.RestUtil;

import io.qameta.allure.Epic;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Epic("INTEGRATION с реальной бд")
@Execution(ExecutionMode.SAME_THREAD)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class AbstractIntegrationTest {

    @Value("${db.port}")
    protected int port;

    @Value("${url.url}")
    protected String url;

    @Autowired
    protected RestUtil rest;

    protected RequestSpecification requestSpecification;

    @BeforeAll
    void init() {
        RestAssured.baseURI = url;
        RestAssured.port = port;
        
        var getUsers = rest
        .serviceName(ServiceName.USER_MANAGEMENT)
        .post(EndpointUser.SYNC)
        .queryParam(QueryParamsName.LIMIT, "100")
        .send();

        getUsers.then()
        .statusCode(HttpStatus.OK.value());
    }

    @BeforeEach
    void setUp() {
        requestSpecification = given().contentType(ContentType.JSON);
    }
}

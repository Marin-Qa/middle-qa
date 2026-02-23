package com.example.base;

import com.example.Application;
import io.qameta.allure.Allure;
import io.qameta.allure.Epic;
import io.qameta.allure.Story;
import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;

@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("mock")
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Epic("WIREMOCK")
@DisplayName("Синхронизация пользователей с помощью wiremock (не реальный сервис)")
@Story("Заглушка на внешний сервис")
public abstract class AbstractWireMockIntegrationTest {

    @LocalServerPort
    protected int port;

    protected static WireMockServer wireMockServer;

    static {
        Allure.step("Генерация заглушки на порту 8089" , () -> {
            wireMockServer = new WireMockServer(WireMockConfiguration.options().port(8089));
            wireMockServer.start();
        });
    }

    @BeforeEach
    void setUpRestAssured() {
        RestAssured.port = port;
    }

    // Для остановки WireMock после всех тестов
    @AfterAll
    static void stopWireMock() {
        Allure.step("Остановка заглушки", () -> {
            if (wireMockServer != null && wireMockServer.isRunning()) {
                wireMockServer.stop();
            }
        });
    }
}

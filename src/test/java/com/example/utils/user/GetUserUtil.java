package com.example.utils.user;

import com.example.constants.endpoints.user.EndpointUser;
import com.example.constants.request.PathParamsName;
import io.restassured.specification.RequestSpecification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Random;

import static io.restassured.RestAssured.given;

public class GetUserUtil{

    private final RequestSpecification spec;

    public GetUserUtil(RequestSpecification spec) {
        this.spec = spec;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getUser(int id){
        return given()
                .spec(spec)
                .pathParams(PathParamsName.ID, id)
                .when()
                .get(EndpointUser.USER_BY_ID)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract().as(Map.class);
    }

    public  Map<String, Object> getUserWithRetry(int maxAttempts, long delayMillis) {
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                // Попытка получить пользователя
                Random random = new Random();
                int id = random.nextInt(100) + 1;
                return getUser(id);
            } catch (AssertionError e) {
                // Если это последняя попытка — пробрасываем
                if (attempt == maxAttempts) {
                    throw e;
                }
                // Иначе ждём перед повторной попыткой
                try {
                    Thread.sleep(delayMillis);
                } catch (InterruptedException ie) {
                    // Восстанавливаем флаг прерывания и выходим
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Поток прерван во время ожидания повторной попытки", ie);
                }
            }
        }
        throw new IllegalStateException("Метод getUserWithRetry() завершился неудачно");
    }
}

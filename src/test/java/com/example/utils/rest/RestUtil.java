package com.example.utils.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Allure;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static io.restassured.RestAssured.given;

@Component
public class RestUtil {

    private String serviceName;
    private String method;
    private String endpoint;
    private Map<String, Object> pathParams = new HashMap<>();
    private Map<String, String> queryParams = new HashMap<>();
    private Map<String, String> headers = new HashMap<>();
    private String requestBody;
    private boolean logRequest;

    public RestUtil() {
    }

    public RestUtil serviceName(String serviceName) {
        this.serviceName = serviceName;
        this.method = null;
        this.endpoint = null;
        this.pathParams.clear();
        this.queryParams.clear();
        this.headers.clear();
        this.requestBody = null;
        return this;
    }

    public RestUtil log() {
        this.logRequest = true;
        return this;
    }

    public RestUtil body(Object obj) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            this.requestBody = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка сериализации объекта в JSON", e);
        }
        return this;
    }

    public RestUtil get(String endpoint) { this.method = "GET"; this.endpoint = endpoint; return this; }
    public RestUtil post(String endpoint) { this.method = "POST"; this.endpoint = endpoint; return this; }
    public RestUtil put(String endpoint) { this.method = "PUT"; this.endpoint = endpoint; return this; }
    public RestUtil delete(String endpoint) { this.method = "DELETE"; this.endpoint = endpoint; return this; }

    public RestUtil pathParam(String key, Object value) { pathParams.put(key, value); return this; }
    public RestUtil queryParam(String key, String value) { queryParams.put(key, value); return this; }
    public RestUtil header(String key, String value) { headers.put(key, value); return this; }

    public Response send() {
        Response response;

        response = Allure.step("HTTP запрос: " + method + " " + endpoint, step -> {

            pathParams.forEach((k,v) -> step.parameter("Path param: " + k, v));
            queryParams.forEach((k,v) -> step.parameter("Query param: " + k, v));
            headers.forEach((k,v) -> step.parameter("Header: " + k, v));
            if (requestBody != null) {
                Allure.addAttachment("Тело запроса", ContentType.JSON.toString(), requestBody);
            }

            String resolvedUrl = resolvePath(endpoint, pathParams);


            var spec = given().contentType(ContentType.JSON);
            if (logRequest) {spec = spec.log().all();}
            if (!queryParams.isEmpty()) spec.queryParams(queryParams);
            if (!headers.isEmpty()) spec.headers(headers);
            if (requestBody != null && (method.equalsIgnoreCase("POST") || method.equalsIgnoreCase("PUT"))) {
                spec.body(requestBody);
            }

            Response resp = switch (method.toUpperCase()) {
                case "GET" -> spec.when().get(resolvedUrl);
                case "POST" -> spec.when().post(resolvedUrl);
                case "PUT" -> spec.when().put(resolvedUrl);
                case "DELETE" -> spec.when().delete(resolvedUrl);
                default -> throw new IllegalArgumentException("Неподдерживаемый HTTP метод: " + method);
            };

            String respBody = Objects.toString(resp.getBody().asPrettyString(), "");
            Allure.addAttachment("Тело ответа", ContentType.JSON.toString(), respBody);
            int respStatusCode = resp.statusCode();
            Allure.addAttachment("Код ответа", String.valueOf(respStatusCode));

            return resp;
        });

        return response;
    }

    private static String resolvePath(String url, Map<String, Object> pathParams) {
        if (pathParams == null || pathParams.isEmpty()) return url;

        String result = url;
        for (var entry : pathParams.entrySet()) {
            Object value = entry.getValue();
            // Преобразуем в строку и подстраховываем null
            String strValue = value != null ? value.toString() : "";
            result = result.replace("{" + entry.getKey() + "}", strValue);
        }
        return result;
    }
}
package com.example.utils.mock;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.qameta.allure.Allure;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
@Component
public class MockUtil {

    public void mock(
            WireMockServer server,
            String serviceName,
            String method,
            String url,
            Map<String, String> queryParams,
            Map<String, String> pathParams,
            String responseFile,
            int status
    ) {
        Allure.step("Mock: " + serviceName, step -> {
            step.parameter("Method", method);
            step.parameter("URL template", url);
            step.parameter("Status", status);
            if (queryParams != null) queryParams.forEach((k, v) -> step.parameter("Query param: " + k, v));
            if (pathParams != null) pathParams.forEach((k, v) -> step.parameter("Path param: " + k, v));

            try {
                String resolvedUrl = resolvePath(url, pathParams);

                String body = Files.readString(Path.of("src/test/resources/__files/" + responseFile));
                Allure.addAttachment("Mock response body", "application/json", body, ".json");

                var mapping = switch (method.toUpperCase()) {
                    case "GET" -> get(urlPathEqualTo(resolvedUrl));
                    case "POST" -> post(urlPathEqualTo(resolvedUrl));
                    case "PUT" -> put(urlPathEqualTo(resolvedUrl));
                    case "DELETE" -> delete(urlPathEqualTo(resolvedUrl));
                    default -> throw new IllegalArgumentException("Неподдерживаемый метод");
                };

                if (queryParams != null && !queryParams.isEmpty()) {
                    for (var entry : queryParams.entrySet()) {
                        mapping.withQueryParam(entry.getKey(), equalTo(entry.getValue()));
                    }
                }
                server.stubFor(mapping.willReturn(aResponse()
                        .withStatus(status)
                        .withHeader("Content-Type", "application/json")
                        .withBody(body)));

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    private static String resolvePath(String url, Map<String, String> pathParams) {
        if (pathParams == null || pathParams.isEmpty()) return url;
        String result = url;
        for (var entry : pathParams.entrySet()) {
            result = result.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return result;
    }
}
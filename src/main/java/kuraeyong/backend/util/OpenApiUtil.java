package kuraeyong.backend.util;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OpenApiUtil {
    private static String serviceKey;

    @Value("${service-key}")
    public void setServiceKey(String key) {
        serviceKey = key;
    }

    public static String getKricOpenApiURL(String serviceId, String operationId, String queryString) {
        return "https://openapi.kric.go.kr/openapi" +
                "/" + serviceId +
                "/" + operationId +
                "?serviceKey=" + serviceKey +
                queryString;
    }
}

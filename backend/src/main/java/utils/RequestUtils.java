package utils;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.databind.ObjectMapper;

public class RequestUtils {
    private final ObjectMapper objectMapper;

    public RequestUtils() {
        objectMapper = new ObjectMapper();
    }

    public <T> T mapRequestBody(HttpServletRequest request, Class<T> clazz) throws IOException {
        try (BufferedReader reader = request.getReader()) {
            return objectMapper.readValue(reader, clazz);
        }
    }
}

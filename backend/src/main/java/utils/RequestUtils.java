package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

import com.fasterxml.jackson.databind.ObjectMapper;

import common.HttpStatusCode;
import exceptions.CustomException;
import stores.session.Session;
import stores.session.SimpleSessionManager;

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

    public Session getSession(HttpServletRequest request) {
        String sessionId = request.getHeader("JSESSIONID");
        if (sessionId == null) {
            throw new CustomException(HttpStatusCode.NOT_ACCEPTABLE, "Session not valid");
        }
        Session session = SimpleSessionManager.getInstance().getSession(sessionId);
        if (session == null || !session.isValid()) {
            throw new CustomException(HttpStatusCode.NOT_ACCEPTABLE, "Session not valid");
        }
        return session;
    }

    public Part getImagePartMultipartFormData(HttpServletRequest request) throws IOException, ServletException {
        return request.getPart("image");
    }
}

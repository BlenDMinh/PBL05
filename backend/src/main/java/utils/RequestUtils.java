package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import com.google.gson.Gson;

import common.HttpStatusCode;
import exceptions.CustomException;
import stores.session.Session;
import stores.session.SimpleSessionManager;

public class RequestUtils {
    private final Gson gson;

    public RequestUtils() {
        gson = new Gson();
    }

    public <T> T mapRequestBody(HttpServletRequest request, Class<T> clazz) throws IOException {
        try (BufferedReader reader = request.getReader()) {
            return gson.fromJson(reader, clazz);
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

    public Map<String, String[]> getQueryParameters(HttpServletRequest request)
            throws UnsupportedEncodingException {
        Map<String, String[]> queryParameters = new HashMap<>();
        String queryString = request.getQueryString();
        if (StringUtils.isNotEmpty(queryString)) {
            queryString = URLDecoder.decode(queryString, StandardCharsets.UTF_8.toString());
            String[] parameters = queryString.split("&");
            for (String parameter : parameters) {
                String[] keyValuePair = parameter.split("=");
                String[] values = queryParameters.get(keyValuePair[0]);
                // length is one if no value is available.
                values = keyValuePair.length == 1 ? ArrayUtils.add(values, "")
                        : ArrayUtils.addAll(values, keyValuePair[1].split(",")); // handles CSV separated query param
                                                                                 // values.
                queryParameters.put(keyValuePair[0], values);
            }
        }
        return queryParameters;
    }
}

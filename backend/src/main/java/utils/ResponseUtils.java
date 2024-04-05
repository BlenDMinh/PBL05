package utils;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

public class ResponseUtils {
    private Gson gson = new Gson();

    public void responseJson(HttpServletResponse response, Object object) throws IOException {
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String data = this.gson.toJson(object);
        out.print(data);
        out.flush();
    }

    public void responseJson(HttpServletResponse response, Object object, int status) throws IOException {
        PrintWriter out = response.getWriter();
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String data = this.gson.toJson(object);
        out.print(data);
        out.flush();
    }
}

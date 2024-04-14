package api.auth.register;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import common.HttpStatusCode;
import exceptions.CustomException;
import modules.auth.dto.RegisterRequestDto;
import modules.auth.dto.RegisterResponseDto;
import modules.auth.service.AuthService;
import utils.RequestUtils;
import utils.ResponseUtils;

@WebServlet("/auth/register")
public class RegisterServlet extends HttpServlet {
    final RequestUtils requestUtils = new RequestUtils();
    final ResponseUtils responseUtils = new ResponseUtils();
    final AuthService authService = new AuthService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        RegisterRequestDto registerRequestDto = requestUtils.mapRequestBody(req, RegisterRequestDto.class);
        String id = authService.register(registerRequestDto.getDisplayName(), registerRequestDto.getEmail(),
                registerRequestDto.getPassword());
        if (id == null) {
            throw new CustomException(HttpStatusCode.SERVICE_UNAVAILABLE, "Cannot register");
        } else {
            responseUtils.responseJson(resp, new RegisterResponseDto(id));
        }
    }
}

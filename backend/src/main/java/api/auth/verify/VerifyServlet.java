package api.auth.verify;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import common.HttpStatusCode;
import exceptions.CustomException;
import modules.auth.dto.RegisterIdAndVerifyCodeDto;
import modules.auth.service.AuthService;
import utils.RequestUtils;
import utils.ResponseUtils;

@WebServlet("/auth/verify")
public class VerifyServlet extends HttpServlet {
    final RequestUtils requestUtils = new RequestUtils();
    final ResponseUtils responseUtils = new ResponseUtils();
    final AuthService authService = new AuthService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        RegisterIdAndVerifyCodeDto registerIdAndVerifyCodeDto = requestUtils.mapRequestBody(req,
                RegisterIdAndVerifyCodeDto.class);
        boolean result = authService.verifyAndCreateAccount(registerIdAndVerifyCodeDto.getRegisterId(),
                registerIdAndVerifyCodeDto.getCode());
        if (!result) {
            throw new CustomException(HttpStatusCode.CONFLICT, "Verify Failed");
        } else {
            responseUtils.responseMessage(resp, "Verify Success! Please sign in again");
        }
    }
}

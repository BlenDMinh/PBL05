package servlets.auth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.modelmapper.ModelMapper;

import com.fasterxml.jackson.databind.ObjectMapper;

import common.HttpStatusCode;
import common.Role;
import common.service.session.AuthSessionManager;
import exceptions.CustomException;
import modules.admin.dto.AdminDto;
import modules.auth.dto.LoginDto;
import modules.auth.dto.LoginResponseDto;
import modules.auth.dto.UserPasswordDto;
import modules.auth.service.AuthService;
import modules.player.dto.PlayerDto;
import utils.RequestUtils;
import utils.ResponseUtils;

@WebServlet("/auth/login")
public class LoginServlet extends HttpServlet {

  private final AuthService authService;
  private final ResponseUtils responseUtils;
  private final RequestUtils requestUtils;
  private final ModelMapper modelMapper;

  public LoginServlet() {
    authService = new AuthService();
    responseUtils = new ResponseUtils();
    requestUtils = new RequestUtils();
    modelMapper = new ModelMapper();
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    LoginDto loginDto = requestUtils.mapRequestBody(req, LoginDto.class);
    UserPasswordDto userPasswordDto = authService.userLogin(loginDto.getEmail(), loginDto.getPassword());
    if (userPasswordDto == null) {
      throw new CustomException(HttpStatusCode.NOT_FOUND, "User not found");
    }
    String sessionId = AuthSessionManager.getInstance().createSession(userPasswordDto);
    if (userPasswordDto.getRole() == Role.ADMIN) {
      AdminDto adminDto = modelMapper.map(userPasswordDto, AdminDto.class);
      responseUtils.responseJson(resp, new LoginResponseDto<AdminDto>(adminDto, sessionId));
    } else {
      PlayerDto playerDto = modelMapper.map(userPasswordDto, PlayerDto.class);
      responseUtils.responseJson(resp, new LoginResponseDto<PlayerDto>(playerDto, sessionId));
    }
  }
}

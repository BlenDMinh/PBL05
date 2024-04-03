package servlets.auth;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import modules.auth.service.AuthService;

@WebServlet("/auth/login")
public class LoginServlet extends HttpServlet {

  private AuthService authService;

  @Override
  public void init() throws ServletException {
    super.init();
    authService = new AuthService();
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    PrintWriter out = resp.getWriter();

    out.println("Hi");
  }
}

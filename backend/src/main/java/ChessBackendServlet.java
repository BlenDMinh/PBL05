import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;

import utils.ConnectionPool;

import java.io.*;
import java.sql.SQLException;

@WebServlet("/live")
public class ChessBackendServlet extends HttpServlet {
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    PrintWriter out = response.getWriter();
    out.println("<HTML>");
    out.println("<HEAD>");
    out.println("<TITLE>Hello From Servlet</TITLE>");
    out.println("</HEAD>");
    out.println("<BODY>");
    out.println("Servlet live ");
    try {
      out.println(ConnectionPool.getConnection() != null ? "Database connected" : "Database disconnected");
    } catch (InterruptedException | SQLException e) {
      e.printStackTrace();
    }
    out.println("</BODY>");
    out.println("</HTML>");
  }
}
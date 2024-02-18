import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import utils.DBConnection;

import java.io.*;

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
    out.println("Servlet live !!!");
    out.println(DBConnection.getConnecttion() != null ? "Database connected" : "Database disconnected");
    out.println("</BODY>");
    out.println("</HTML>");
  }
}
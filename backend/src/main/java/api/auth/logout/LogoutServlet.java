package api.auth.logout;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import stores.session.Session;
import stores.session.SimpleSessionManager;
import utils.RequestUtils;
import utils.ResponseUtils;

@WebServlet("/api/auth/logout")
public class LogoutServlet extends HttpServlet {
    private final RequestUtils requestUtils = new RequestUtils();
    private final ResponseUtils responseUtils = new ResponseUtils();

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Session session = requestUtils.getSession(req);
        SimpleSessionManager.getInstance().invalidateSession(session.getSessionId());
        responseUtils.responseJson(resp, "Logout successfully");
    }
}

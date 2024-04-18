package api.profile.game;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import modules.profile.dto.PaginationGameHistoryDto;
import modules.profile.service.ProfileService;
import utils.RequestUtils;
import utils.ResponseUtils;

@WebServlet("/profile/games-of/*")
public class ProfileGameServlet extends HttpServlet {
    final RequestUtils requestUtils = new RequestUtils();
    final ResponseUtils responseUtils = new ResponseUtils();
    final ProfileService profileService = new ProfileService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo != null) {
            // Extract the id from the path
            String[] pathParts = pathInfo.split("/");
            int id = Integer.parseInt(pathParts[pathParts.length - 1]);
            int page = 1, size = 50;
            try {
                page = Integer.parseInt(req.getParameter("page"));
                size = Integer.parseInt(req.getParameter("size"));
            } catch (NumberFormatException | NullPointerException e) {
            }
            PaginationGameHistoryDto paginationGameHistoryDto = profileService.getPaginationGameHistoryByPlayerId(id,
                    page, size);
            responseUtils.responseJson(resp, paginationGameHistoryDto);
        }
    }
}

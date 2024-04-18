package api.profile.info;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import common.HttpStatusCode;
import exceptions.CustomException;
import modules.profile.dto.PlayerDto;
import modules.profile.service.ProfileService;
import utils.RequestUtils;
import utils.ResponseUtils;

@WebServlet("/profile/info/*")
public class ProfileInfoServlet extends HttpServlet {
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
            PlayerDto playerDto = profileService.getPlayerById(id);
            if (playerDto == null) {
                throw new CustomException(HttpStatusCode.NOT_FOUND, "Profile not found");
            } else {
                responseUtils.responseJson(resp, playerDto);
            }
        }
    }
}

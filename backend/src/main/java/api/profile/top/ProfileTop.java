package api.profile.top;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import modules.profile.service.ProfileService;
import utils.RequestUtils;
import utils.ResponseUtils;

@WebServlet("/profile/top")
public class ProfileTop extends HttpServlet {
    final RequestUtils requestUtils = new RequestUtils();
    final ResponseUtils responseUtils = new ResponseUtils();
    final ProfileService profileService = new ProfileService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String[] n_array = requestUtils.getQueryParameters(req).get("n");
        if (n_array != null) {
            int n = Integer.parseInt(n_array[0]);
            String[] keywords = requestUtils.getQueryParameters(req).get("keyword");
            if (keywords != null) {
                responseUtils.responseJson(resp, profileService.getTopNPlayerByDisplaynameOrEmail(n, keywords[0]));
            }
        }
    }
}

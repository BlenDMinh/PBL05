package api.friend.player;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import common.HttpStatusCode;
import common.dto.UserPasswordDto;
import exceptions.CustomException;
import modules.friend.dto.FriendRequestDto;
import modules.friend.dto.PaginationFriendDto;
import modules.friend.service.FriendService;
import shared.session.Session;
import shared.session.SessionKey;
import utils.RequestUtils;
import utils.ResponseUtils;

@WebServlet("/friend/player/*")
public class FriendPlayerServlet extends HttpServlet {
    final RequestUtils requestUtils = new RequestUtils();
    final ResponseUtils responseUtils = new ResponseUtils();
    final FriendService friendService = new FriendService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo != null) {
            // Extract the id from the path
            String[] pathParts = pathInfo.split("/");
            int id = Integer.parseInt(pathParts[pathParts.length - 1]);
            int page = 1, size = 50;
            String keyword = "";
            try {
                page = Integer.parseInt(req.getParameter("page"));
                size = Integer.parseInt(req.getParameter("size"));
                keyword = req.getParameter("keyword").toString();
            } catch (NumberFormatException | NullPointerException e) {
            }
            PaginationFriendDto friends = friendService.getPaginationFriendDto(id, page, size,
                    keyword);
            responseUtils.responseJson(resp, friends);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Session session = requestUtils.getSession(req);
        int userId = session.getAttribute(SessionKey.USER_PASSWORD_DTO, UserPasswordDto.class).getId();
        FriendRequestDto friendRequestDto = requestUtils.mapRequestBody(req,
                FriendRequestDto.class);
        boolean success = friendService.unfriend(userId, friendRequestDto.getReceiverId());
        if (success) {
            responseUtils.responseMessage(resp, "Success");
        } else {
            throw new CustomException(HttpStatusCode.NOT_ACCEPTABLE, "Fail to perform");
        }
    }
}

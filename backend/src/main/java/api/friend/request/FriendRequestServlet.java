package api.friend.request;

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
import modules.friend.dto.PaginationFullFriendRequestDto;
import modules.friend.dto.ResponseFriendRequestDto;
import modules.friend.service.FriendService;
import stores.session.Session;
import stores.session.SessionKey;
import utils.RequestUtils;
import utils.ResponseUtils;

@WebServlet("/friend/requests")
public class FriendRequestServlet extends HttpServlet {
    final RequestUtils requestUtils = new RequestUtils();
    final ResponseUtils responseUtils = new ResponseUtils();
    final FriendService friendService = new FriendService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Session session = requestUtils.getSession(req);
        int senderId = session.getAttribute(SessionKey.USER_PASSWORD_DTO, UserPasswordDto.class).getId();
        FriendRequestDto friendRequestDto = requestUtils.mapRequestBody(req, FriendRequestDto.class);
        boolean created = friendService.createFriendRequest(senderId, friendRequestDto.getReceiverId());
        if (created) {
            responseUtils.responseMessage(resp, "Created");
        } else {
            throw new CustomException(HttpStatusCode.NOT_ACCEPTABLE, "Friend Request not create");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int page = 1, size = 50;
        try {
            page = Integer.parseInt(req.getParameter("page"));
            size = Integer.parseInt(req.getParameter("size"));
        } catch (NumberFormatException | NullPointerException e) {
        }
        Session session = requestUtils.getSession(req);
        UserPasswordDto userPasswordDto = session.getAttribute(SessionKey.USER_PASSWORD_DTO, UserPasswordDto.class);
        PaginationFullFriendRequestDto requests = friendService.getPaginationFriendRequestOfReceiver(
                userPasswordDto.getId(), page, size);
        responseUtils.responseJson(resp, requests);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Session session = requestUtils.getSession(req);
        int userId = session.getAttribute(SessionKey.USER_PASSWORD_DTO, UserPasswordDto.class).getId();
        ResponseFriendRequestDto responseFriendRequestDto = requestUtils.mapRequestBody(req,
                ResponseFriendRequestDto.class);
        boolean success = false;
        if (responseFriendRequestDto.isAccept()) {
            success = friendService.acceptFriend(responseFriendRequestDto.getSenderId(), userId);
        } else {
            success = friendService.rejectFriend(responseFriendRequestDto.getSenderId(), userId);
        }
        if (success) {
            responseUtils.responseMessage(resp, "Success");
        } else {
            throw new CustomException(HttpStatusCode.NOT_ACCEPTABLE, "Fail to perform");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Session session = requestUtils.getSession(req);
        int senderId = session.getAttribute(SessionKey.USER_PASSWORD_DTO, UserPasswordDto.class).getId();
        FriendRequestDto friendRequestDto = requestUtils.mapRequestBody(req, FriendRequestDto.class);
        boolean deleted = friendService.rejectFriend(senderId, friendRequestDto.getReceiverId());
        if (deleted) {
            responseUtils.responseMessage(resp, "Deleted");
        } else {
            throw new CustomException(HttpStatusCode.NOT_ACCEPTABLE, "Friend Request not delete or not exist");
        }
    }
}

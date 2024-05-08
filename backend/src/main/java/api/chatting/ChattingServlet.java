package api.chatting;

import java.io.IOException;
import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import common.dto.UserPasswordDto;
import modules.chat.dto.UserWithLastMessageDto;
import modules.chat.service.ChatService;
import shared.session.Session;
import shared.session.SessionKey;
import utils.RequestUtils;
import utils.ResponseUtils;

@WebServlet("/chatting")
public class ChattingServlet extends HttpServlet {
    final ChatService chatService = new ChatService();
    final RequestUtils requestUtils = new RequestUtils();
    final ResponseUtils responseUtils = new ResponseUtils();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Session session = requestUtils.getSession(req);
        UserPasswordDto userPasswordDto = session.getAttribute(SessionKey.USER_PASSWORD_DTO, UserPasswordDto.class);
        List<UserWithLastMessageDto> userInChatDtos = chatService.getUserWithLastMessageOfUser(userPasswordDto.getId());
        responseUtils.responseJson(resp, userInChatDtos);
    }
}

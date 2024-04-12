package api.chatting;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import exceptions.CustomException;
import modules.auth.dto.UserPasswordDto;
import modules.chat.dto.UserInChatDto;
import modules.chat.service.ChatService;
import stores.session.Session;
import stores.session.SessionKey;
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
        List<UserInChatDto> userInChatDtos = chatService.getUserInChatOfSender(userPasswordDto.getId());
        responseUtils.responseJson(resp, userInChatDtos);
    }
}

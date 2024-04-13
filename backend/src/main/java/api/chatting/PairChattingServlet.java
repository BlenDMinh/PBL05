package api.chatting;

import java.io.IOException;
import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import modules.auth.dto.UserPasswordDto;
import modules.chat.dto.MessageResponseDto;
import modules.chat.dto.UserWithLastMessageDto;
import modules.chat.service.ChatService;
import stores.session.Session;
import stores.session.SessionKey;
import utils.RequestUtils;
import utils.ResponseUtils;

@WebServlet("/chatting/pair/*")
public class PairChattingServlet extends HttpServlet {
    final ChatService chatService = new ChatService();
    final RequestUtils requestUtils = new RequestUtils();
    final ResponseUtils responseUtils = new ResponseUtils();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo != null) {
            // Extract the id from the path
            String[] pathParts = pathInfo.split("/");
            int id = Integer.parseInt(pathParts[pathParts.length - 1]);
            Session session = requestUtils.getSession(req);
            UserPasswordDto userPasswordDto = session.getAttribute(SessionKey.USER_PASSWORD_DTO, UserPasswordDto.class);
            List<MessageResponseDto> messages = chatService.getMessageOfPair(userPasswordDto.getId(), id);
            responseUtils.responseJson(resp, messages);
        }
    }
}
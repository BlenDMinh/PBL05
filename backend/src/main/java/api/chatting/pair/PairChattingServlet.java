package api.chatting.pair;

import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import modules.auth.dto.UserPasswordDto;
import modules.chat.dto.PaginationMessageResponseDto;
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
            int page = 1, size = 50;
            String keyword = "";
            try {
                page = Integer.parseInt(req.getParameter("page"));
                size = Integer.parseInt(req.getParameter("size"));
                keyword = req.getParameter("keyword").toString();
            } catch (NumberFormatException | NullPointerException e) {
            }
            Session session = requestUtils.getSession(req);
            UserPasswordDto userPasswordDto = session.getAttribute(SessionKey.USER_PASSWORD_DTO, UserPasswordDto.class);
            PaginationMessageResponseDto messages = chatService.getPaginationMessageOfPair(userPasswordDto.getId(), id,
                    page, size, keyword);
            responseUtils.responseJson(resp, messages);
        }
    }
}
package api.game.gamelog;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import modules.game_chesslib.service.GameService;
import utils.RequestUtils;
import utils.ResponseUtils;

@WebServlet("/game-log/*")
public class GameLogServlet extends HttpServlet {
    final RequestUtils requestUtils = new RequestUtils();
    final ResponseUtils responseUtils = new ResponseUtils();
    final GameService gameService = new GameService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo != null) {
            // Extract the id from the path
            String[] pathParts = pathInfo.split("/");
            String id = pathParts[pathParts.length - 1];
            responseUtils.responseJson(resp, gameService.getGameLog(id));
        }
    }
}

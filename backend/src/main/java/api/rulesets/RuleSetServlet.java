package api.rulesets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import common.HttpStatusCode;
import exceptions.CustomException;
import modules.ruleset.dto.RuleSetDto;
import modules.ruleset.service.RuleSetService;
import utils.RequestUtils;
import utils.ResponseUtils;

@WebServlet("/rulesets/*")
public class RuleSetServlet extends HttpServlet {
    RequestUtils requestUtils = new RequestUtils();
    ResponseUtils responseUtils = new ResponseUtils();
    RuleSetService ruleSetService = new RuleSetService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo != null) {
            // Extract the id from the path
            String[] pathParts = pathInfo.split("/");
            int id = Integer.parseInt(pathParts[pathParts.length - 1]);
            RuleSetDto ruleSetDto = ruleSetService.getPublishedRuleSetById(id);
            if (ruleSetDto == null) {
                throw new CustomException(HttpStatusCode.NOT_FOUND, "Ruleset not found");
            }
            responseUtils.responseJson(resp, ruleSetDto);
            return;
        }
        responseUtils.responseJson(resp, ruleSetService.getAllRuleSet(true));
    }
}

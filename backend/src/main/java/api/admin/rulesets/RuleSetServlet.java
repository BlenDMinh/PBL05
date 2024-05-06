package api.admin.rulesets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import common.HttpStatusCode;
import exceptions.CustomException;
import modules.ruleset.dto.AddRuleSetRequest;
import modules.ruleset.dto.RuleSetDto;
import modules.ruleset.dto.UpdateRuleSetRequest;
import modules.ruleset.service.RuleSetService;
import utils.RequestUtils;
import utils.ResponseUtils;

@WebServlet("/admin/rulesets/*")
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
            RuleSetDto ruleSetDto = ruleSetService.getById(id);
            if (ruleSetDto == null) {
                throw new CustomException(HttpStatusCode.NOT_FOUND, "Ruleset not found");
            }
            responseUtils.responseJson(resp, ruleSetDto);
            return;
        }
        String[] values = requestUtils.getQueryParameters(req).get("published");
        if (values == null) {
            responseUtils.responseJson(resp, ruleSetService.getAllRuleSet());
            return;
        }
        boolean published = values[0].equals("1");
        responseUtils.responseJson(resp, ruleSetService.getAllRuleSet(published));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        AddRuleSetRequest addRuleSetRequest = requestUtils.mapRequestBody(req, AddRuleSetRequest.class);
        boolean success = ruleSetService.addRuleSet(addRuleSetRequest.getName(), addRuleSetRequest.getDetail(),
                addRuleSetRequest.getDescription());
        if (success) {
            responseUtils.responseMessage(resp, "Ruleset added");
        } else {
            throw new CustomException(HttpStatusCode.INTERNAL_SERVER_ERROR, "Ruleset not add");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo != null) {
            // Extract the id from the path
            String[] pathParts = pathInfo.split("/");
            int id = Integer.parseInt(pathParts[pathParts.length - 1]);
            UpdateRuleSetRequest updateRuleSetRequest = requestUtils.mapRequestBody(req, UpdateRuleSetRequest.class);
            boolean success = ruleSetService.updateRuleSet(id, updateRuleSetRequest.getName(),
                    updateRuleSetRequest.getDetail(), updateRuleSetRequest.getDescription(),
                    updateRuleSetRequest.isPublished());
            if (success) {
                responseUtils.responseMessage(resp, "Ruleset updated");
            } else {
                throw new CustomException(HttpStatusCode.INTERNAL_SERVER_ERROR, "Ruleset not found or not updated");
            }
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo != null) {
            // Extract the id from the path
            String[] pathParts = pathInfo.split("/");
            int id = Integer.parseInt(pathParts[pathParts.length - 1]);
            boolean success = ruleSetService.deleteRuleSet(id);
            if (success) {
                responseUtils.responseMessage(resp, "Ruleset deleted");
            } else {
                throw new CustomException(HttpStatusCode.INTERNAL_SERVER_ERROR, "Ruleset not found or not deleted");
            }
        }
    }
}

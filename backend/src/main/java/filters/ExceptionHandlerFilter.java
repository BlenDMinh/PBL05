package filters;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;

import common.HttpStatusCode;
import exceptions.CustomException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import utils.ResponseUtils;

import java.io.IOException;

@WebFilter(filterName = "ExceptionHandlerFilter")
public class ExceptionHandlerFilter implements Filter {
    private ResponseUtils responseUtils;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
        responseUtils = new ResponseUtils();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            request.setCharacterEncoding("UTF-8");
            chain.doFilter(request, response);
        } catch (CustomException ex) {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            responseUtils.responseJson(httpResponse, new ErrorResponse(ex.getMessage(), ex.getErrorCode()),
                    ex.getErrorCode());
        } catch (Throwable throwable) {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            responseUtils.responseJson(httpResponse,
                    new ErrorResponse("Internal Server Error", HttpStatusCode.INTERNAL_SERVER_ERROR),
                    HttpStatusCode.INTERNAL_SERVER_ERROR);
        }
    }
}

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
class ErrorResponse {
    private String message;
    private int code;
}
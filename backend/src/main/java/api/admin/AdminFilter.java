package api.admin;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

import common.HttpStatusCode;
import common.Role;
import common.dto.UserPasswordDto;
import exceptions.CustomException;
import stores.session.Session;
import stores.session.SessionKey;
import utils.RequestUtils;

@WebFilter(filterName = "AdminFilter", urlPatterns = "/admin/*")
public class AdminFilter implements Filter {
    RequestUtils requestUtils = new RequestUtils();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        Session session = requestUtils.getSession((HttpServletRequest) request);
        UserPasswordDto userPasswordDto = session.getAttribute(SessionKey.USER_PASSWORD_DTO, UserPasswordDto.class);
        if (!userPasswordDto.getRole().equals(Role.ADMIN)) {
            throw new CustomException(HttpStatusCode.NOT_ACCEPTABLE, "You do not have permission");
        }
        chain.doFilter(request, response);
    }
}

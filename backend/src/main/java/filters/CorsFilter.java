package filters;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletRequest;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import common.Env;

@WebFilter(filterName = "CorsFilter")
public class CorsFilter implements Filter {

  private static final String[] allowedOrigins = Env.CORS_ALLOWS;

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
      throws IOException, ServletException {

    HttpServletRequest request = (HttpServletRequest) servletRequest;
    String requestOrigin = request.getHeader("Origin");
    if (requestOrigin != null) {
      if (isAllowedOrigin(requestOrigin)) {
        // Authorize the origin, all headers, and all methods
        ((HttpServletResponse) servletResponse).addHeader("Access-Control-Allow-Origin", requestOrigin);
        ((HttpServletResponse) servletResponse).addHeader("Access-Control-Allow-Headers", "*");
        ((HttpServletResponse) servletResponse).addHeader("Access-Control-Allow-Methods",
            "GET, OPTIONS, HEAD, PUT, POST, DELETE");

        HttpServletResponse resp = (HttpServletResponse) servletResponse;

        // CORS handshake (pre-flight request)
        if (request.getMethod().equals("OPTIONS")) {
          resp.setStatus(HttpServletResponse.SC_ACCEPTED);
          return;
        }
      }
    }
    // pass the request along the filter chain
    filterChain.doFilter(request, servletResponse);
  }

  private boolean isAllowedOrigin(String origin) {
    for (String allowedOrigin : allowedOrigins) {
      if (origin.equals(allowedOrigin) || allowedOrigin.equals("*"))
        return true;
    }
    return false;
  }
}
package com.reactivechat.websocket;

import java.io.IOException;
import java.security.Principal;
import java.util.Arrays;
import java.util.stream.Collectors;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

/**
 * Filter used to authenticate request and create user principal
 */
@WebFilter("/chat/*")
public class AccessTokenFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {
        
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        
        // Extract access token from the request
//        String token = request.getParameter("access-token");
//        if (token == null || token.trim().isEmpty()) {
//            returnForbiddenError(response, "An access token is required to connect");
//            return;
//        }
//
    
        String value = (request.getCookies() != null)
            ? Arrays.stream(request.getCookies()).map(Cookie::getName).collect(Collectors.joining(","))
            : "NO-COOKIES";
    
        System.out.println();
        System.out.println();
        System.out.println("=============> COOKIES: " + value);
        System.out.println();
        System.out.println();
        
        AuthenticatedRequest authenticated = new AuthenticatedRequest(request,
            "asdkasdkasdkasdkasdkasdokasdokasodk");
    
        filterChain.doFilter(authenticated, servletResponse);
    }
    
    @Override
    public void init(FilterConfig filterConfig) {
    
    }
    
    @Override
    public void destroy() {
    
    }
    
    private void returnForbiddenError(HttpServletResponse response, String message) throws IOException {
        response.sendError(HttpServletResponse.SC_FORBIDDEN, message);
    }
    
    private static class AuthenticatedRequest extends HttpServletRequestWrapper {
        
        private final String token;
        
        public AuthenticatedRequest(HttpServletRequest request, String token) {
            super(request);
            this.token = token;
        }
        
        @Override
        public Principal getUserPrincipal() {
            return () -> token;
        }
    }
}

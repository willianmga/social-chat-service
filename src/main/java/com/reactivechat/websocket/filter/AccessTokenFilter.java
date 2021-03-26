package com.reactivechat.websocket.filter;

import com.reactivechat.core.ValidateTokenServerResponse;
import com.reactivechat.exception.ResponseStatus;
import com.reactivechat.session.session.UserAuthenticationDetails;
import java.io.IOException;
import java.nio.file.attribute.UserPrincipal;
import java.security.Principal;
import java.util.Optional;
import java.util.stream.Stream;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.eclipse.jetty.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * Filter used to authenticate request and create user principal
 */
@WebFilter("/chat/*")
public class AccessTokenFilter implements Filter {
    
    private static final String TOKEN_NOT_PRESENT_ERROR = "Access token must be provided";
    private static final String TOKEN_EXPIRED_ERROR = "Access token provided is expired";
    private static final String TOKEN_INVALID_ERROR = "Access token provided is invalid";
    private static final String SERVER_ERROR = "A server error occuried";
    private static final String AUTH_SEVER_URL = "social.chat.auth.service.url";
    private static final String B_COOKIE = "b";
    
    //private final Environment environment;
    
    public AccessTokenFilter() {
        ///this.environment = ContextLoader.getCurrentWebApplicationContext().getBean(Environment.class);
    }
    
    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {
        
        final HttpServletRequest request = (HttpServletRequest) servletRequest;
        final HttpServletResponse response = (HttpServletResponse) servletResponse;
        final Optional<String> bTokenOpt = getBToken(request);
    
        if (bTokenOpt.isPresent()) {
    
            final String token = bTokenOpt.get();
            final ResponseEntity<ValidateTokenServerResponse> tokenResponse = validateToken(token)
                .block();
    
            if (tokenResponse != null && HttpStatus.isSuccess(tokenResponse.getStatusCodeValue()) &&
                tokenResponse.getBody() != null && ResponseStatus.SUCCESS.equals(tokenResponse.getBody().getStatus())) {
                
                handleSuccess(servletResponse, filterChain, request, token, tokenResponse.getBody());
            } else {
                handleError(response, tokenResponse);
            }
    
        } else {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, TOKEN_NOT_PRESENT_ERROR);
        }
    
    }
    
    @Override
    public void init(FilterConfig filterConfig) {}
    
    @Override
    public void destroy() {}
    
    private void handleSuccess(ServletResponse servletResponse, FilterChain filterChain,
                               HttpServletRequest request, String token,
                               ValidateTokenServerResponse tokenResponse) throws IOException, ServletException {
        
        final LoggedInUser loggedInUser = LoggedInUser.builder()
            .sessionId(tokenResponse.getSessionId())
            .userAuthenticationDetails(
                UserAuthenticationDetails.builder()
                    .token(token)
                    .userId(tokenResponse.getUserId())
                    .build()
            )
            .build();
        
        filterChain.doFilter(new AuthenticatedRequest(request, loggedInUser), servletResponse);
    }
    
    private void handleError(final HttpServletResponse response,
                             final ResponseEntity<ValidateTokenServerResponse> responseEntity) throws IOException {
        
        if (responseEntity != null && responseEntity.getStatusCodeValue() == 403) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, TOKEN_EXPIRED_ERROR);
        } else if (responseEntity != null && responseEntity.getStatusCodeValue() == 401) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, TOKEN_INVALID_ERROR);
        } else {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, SERVER_ERROR);
        }
    }

    private Mono<ResponseEntity<ValidateTokenServerResponse>> validateToken(final String token) {
        
        WebClient.builder()
            .build();
        
        return WebClient.create()
            .post()
            //.uri(environment.getProperty(AUTH_SEVER_URL) + "/v1/auth/token/valid")
            .uri("https://dev-server.com/api/v1/auth/token/valid")
            .header(HttpHeaders.AUTHORIZATION, token)
            .retrieve()
            .toEntity(ValidateTokenServerResponse.class);
    }

    private Optional<String> getBToken(final HttpServletRequest request) {
        
        if (request.getCookies() != null) {
            return Stream.of(request.getCookies())
                .filter(cookie -> B_COOKIE.equals(cookie.getName()) && cookie.getValue() != null)
                .map(cookie -> cookie.getValue().trim())
                .findFirst();
        }

        return Optional.empty();
    }

    private static class AuthenticatedRequest extends HttpServletRequestWrapper {
        
        private final LoggedInUser loggedInUser;
    
        public AuthenticatedRequest(final HttpServletRequest request,
                                    final LoggedInUser loggedInUser) {
            super(request);
            this.loggedInUser = loggedInUser;
        }
    
        @Override
        public Principal getUserPrincipal() {
            return loggedInUser;
        }
        
    }
    
    @Getter
    @Builder
    @ToString
    @EqualsAndHashCode
    public static class LoggedInUser implements UserPrincipal {
    
        private final String sessionId;
        private final UserAuthenticationDetails userAuthenticationDetails;
        
        @Override
        public String getName() {
            return userAuthenticationDetails.getUserId();
        }
        
        public String getToken() {
            return userAuthenticationDetails.getToken();
        }
        
    }
    
}
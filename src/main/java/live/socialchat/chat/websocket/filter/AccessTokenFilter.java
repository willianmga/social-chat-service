package live.socialchat.chat.websocket.filter;

import java.io.IOException;
import java.nio.file.attribute.UserPrincipal;
import java.security.Principal;
import java.util.Objects;
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
import live.socialchat.chat.core.ValidateTokenServerResponse;
import live.socialchat.chat.exception.ResponseStatus;
import live.socialchat.chat.session.session.UserAuthenticationDetails;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

/**
 * Filter used to authenticate request and create user principal
 */
@Component
@WebFilter("/chat/*")
public class AccessTokenFilter implements Filter {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AccessTokenFilter.class);
    private static final String TOKEN_NOT_PRESENT_ERROR = "Access token must be provided";
    private static final String TOKEN_EXPIRED_ERROR = "Access token provided is expired";
    private static final String TOKEN_INVALID_ERROR = "Access token provided is invalid";
    private static final String SERVER_ERROR = "A server error occuried";
    private static final String AUTH_SEVER_URL = "social.chat.auth.service.url";
    private static final String B_COOKIE = "b";
    
    private final Environment environment;
    
    public AccessTokenFilter(final Environment environment) {
        this.environment = environment;
    }
    
    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {
        
        final HttpServletRequest request = (HttpServletRequest) servletRequest;
        final HttpServletResponse response = (HttpServletResponse) servletResponse;
        final Optional<String> bTokenOpt = getBToken(request);
    
        if (bTokenOpt.isPresent()) {
    
            try {
    
                final String token = bTokenOpt.get();
                final ValidateTokenServerResponse tokenResponse = validateToken(token).block();
    
                if (tokenResponse != null && ResponseStatus.SUCCESS.equals(tokenResponse.getStatus())) {
                    handleSuccess(servletResponse, filterChain, request, token, tokenResponse);
                } else {
                    handleServerError(response);
                }
                
            } catch (WebClientResponseException e) {
                handleError(response, e.getRawStatusCode());
            }

        } else {
            handleTokenNotPresent(response);
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
    
        LOGGER.info("Connection accepted from session {}", tokenResponse.getSessionId());
    }
    
    private void handleError(final HttpServletResponse response,
                             int responseStatusCode) throws IOException {
        
        if (responseStatusCode == 403) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, TOKEN_EXPIRED_ERROR);
            LOGGER.error("Connection rejected due to expired token. Status {}", responseStatusCode);
        } else if (responseStatusCode == 401) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, TOKEN_INVALID_ERROR);
            LOGGER.error("Connection rejected due to invalid token. Status {}", responseStatusCode);
        } else {
            handleServerError(response);
        }
    
    }
    
    private void handleTokenNotPresent(HttpServletResponse response) throws IOException {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, TOKEN_NOT_PRESENT_ERROR);
        LOGGER.error("Connection rejected due to token not present. Status 401");
    }
    
    private void handleServerError(HttpServletResponse response) throws IOException {
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, SERVER_ERROR);
        LOGGER.error("Connection rejected due to server error. Status 500");
    }
    
    private Mono<ValidateTokenServerResponse> validateToken(final String token) {
        return WebClient.builder()
            .baseUrl(getAuthServiceUrl())
            .build()
            .post()
            .uri("/v1/auth/token/valid")
            .header(HttpHeaders.AUTHORIZATION, token)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(ValidateTokenServerResponse.class);
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
    
    private String getAuthServiceUrl() {
        final String authServiceUrl = environment.getRequiredProperty(AUTH_SEVER_URL);
        Objects.requireNonNull(authServiceUrl, "Couldn't find social-chat-auth-service url");
        return authServiceUrl;
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
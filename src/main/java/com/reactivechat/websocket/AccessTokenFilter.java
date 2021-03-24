package com.reactivechat.websocket;

import com.reactivechat.core.ValidateTokenServerResponse;
import com.reactivechat.exception.ChatException;
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
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * Filter used to authenticate request and create user principal
 */
@WebFilter("/chat/*")
public class AccessTokenFilter implements Filter {
    
    private static final String AUTH_SEVER_URL = "social.chat.auth.service.url";
    private static final String B_COOKIE = "b";
    
    //private final Environment environment;
    
    public AccessTokenFilter() {
        ///this.environment = ContextLoader.getCurrentWebApplicationContext().getBean(Environment.class);
    }
    
    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain filterChain) {
        
        final HttpServletRequest request = (HttpServletRequest) servletRequest;
        final HttpServletResponse response = (HttpServletResponse) servletResponse;
        
        getBToken(request)
            .switchIfEmpty(Mono.error(new IllegalArgumentException("access token must be provided")))
            .doOnError(error -> returnForbiddenError(response, error.getMessage()))
            .flatMap(token -> getUserAuthenticationDetails(request, token))
            .doOnError(error -> returnForbiddenError(response, error.getMessage()))
            .subscribe(authenticatedRequest -> {
                try {
                    filterChain.doFilter(authenticatedRequest, servletResponse);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

    }

    @Override
    public void init(FilterConfig filterConfig) {}
    
    @Override
    public void destroy() {}
    
    private Mono<AuthenticatedRequest> getUserAuthenticationDetails(final HttpServletRequest request,
                                                                    final String token) {
    
        return WebClient.create()
            .post()
            //.uri(environment.getProperty(AUTH_SEVER_URL) + "/v1/auth/token/valid")
            .uri("https://dev-server.com/api/v1/auth/token/valid")
            .header(HttpHeaders.AUTHORIZATION, token)
            .retrieve()
            .toEntity(ValidateTokenServerResponse.class)
            .switchIfEmpty(Mono.error(new ChatException("access token must be provided")))
            .handle((responseEntity, sink) -> {
    
                ValidateTokenServerResponse response = responseEntity.getBody();
    
                if (HttpStatus.isSuccess(responseEntity.getStatusCodeValue()) && response != null &&
                    ResponseStatus.SUCCESS.equals(response.getStatus())) {
    
                    final LoggedInUser loggedInUser = LoggedInUser.builder()
                        .sessionId(response.getSessionId())
                        .userAuthenticationDetails(
                            UserAuthenticationDetails.builder()
                                .token(token)
                                .userId(response.getUserId())
                                .build()
                        )
                        .build();
    
                    sink.next(new AuthenticatedRequest(request, loggedInUser));
        
                }
                
                sink.error(new ChatException("Failed to validate token"));
                
            });

    }

    private Mono<String> getBToken(final HttpServletRequest request) {
        
        if (request.getCookies() != null) {
    
            final Optional<String> cookieOpt = Stream.of(request.getCookies())
                .filter(cookie -> B_COOKIE.equals(cookie.getName()) && cookie.getValue() != null)
                .map(cookie -> cookie.getValue().trim())
                .findFirst();
            
            return cookieOpt
                .map(Mono::just)
                .orElseGet(Mono::empty);
        }

        return Mono.empty();
    }
    
    private void returnForbiddenError(HttpServletResponse response, String message) {
        try {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @Getter
    @ToString
    @EqualsAndHashCode(callSuper = false)
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
            return sessionId;
        }
        
    }
    
}
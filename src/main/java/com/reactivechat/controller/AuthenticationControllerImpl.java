package com.reactivechat.controller;

import com.reactivechat.exception.ChatException;
import com.reactivechat.exception.ErrorType;
import com.reactivechat.model.message.AuthenticateRequest;
import com.reactivechat.model.message.AuthenticateResponse;
import com.reactivechat.model.User;
import com.reactivechat.model.message.MessageType;
import com.reactivechat.model.message.ResponseMessage;
import com.reactivechat.repository.SessionsRepository;
import com.reactivechat.repository.UsersRepository;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;
import javax.websocket.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationControllerImpl implements AuthenticationController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationControllerImpl.class);
    
    private final UsersRepository usersRepository;
    private final SessionsRepository sessionsRepository;
    private final MessageBroadcasterController broadcasterController;
    
    @Autowired
    public AuthenticationControllerImpl(final UsersRepository usersRepository,
                                        final SessionsRepository sessionsRepository,
                                        final MessageBroadcasterController broadcasterController) {
        this.usersRepository = usersRepository;
        this.sessionsRepository = sessionsRepository;
        this.broadcasterController = broadcasterController;
    }
    
    @Override
    public void handleAuthenticate(final AuthenticateRequest authenticateRequest,
                                   final Session session) {
    
        try {
    
            AuthenticateResponse response = authenticate(authenticateRequest, session);
    
            final ResponseMessage<Object> responseMessage = ResponseMessage
                .builder()
                .type(MessageType.AUTHENTICATE)
                .payload(response)
                .build();
    
            broadcasterController.broadcastToSession(session, responseMessage);
            
        } catch (ChatException e) {
    
            final ResponseMessage<Object> responseMessage = ResponseMessage
                .builder()
                .type(MessageType.AUTHENTICATE)
                .payload(e.toErrorMessage())
                .build();
    
            broadcasterController.broadcastToSession(session, responseMessage);
            
        }

    }
    
    private AuthenticateResponse authenticate(final AuthenticateRequest authenticateRequest,
                                              final Session session) {
        
        Optional<User> userOpt = usersRepository.findByUsername(authenticateRequest.getUsername());
        
        if (userOpt.isPresent()) {

            try {
                
                User user = userOpt.get();
                String token = buildToken(session, user);
                sessionsRepository.create(user, session);
                sessionsRepository.authenticate(session, token);
    
                LOGGER.info("New session created: {}", session.getId());
    
                return AuthenticateResponse.builder()
                    .user(user)
                    .token(token)
                    .build();
                
            } catch (Exception e) {
                throw new ChatException("Failed to authenticate", ErrorType.SERVER_ERROR);
            }
         
        }
        
        throw new ChatException("Invalid Credentials", ErrorType.INVALID_CREDENTIALS);
    }
    
    @Override
    public boolean isAuthenticatedSession(final Session session, final String token) {
        return sessionsRepository.sessionIsAuthenticated(session, token);
    }
    
    @Override
    public void logoff(final Session session) {
    
        User user = sessionsRepository.findBySession(session);
        sessionsRepository.delete(user, session);
    
        ResponseMessage<Object> responseMessage = ResponseMessage
            .builder()
            .type(MessageType.LOGOFF)
            .build();
    
        broadcasterController.broadcastToSession(session, responseMessage);
        
    }
    
    private String buildToken(final Session session, final User user) {
        
        final String token = UUID.randomUUID().toString() + "-" + user.getId() + "-" + session.getId();
        
        return Base64
            .getEncoder()
            .encodeToString(token.getBytes(StandardCharsets.UTF_8));
    }
    
}

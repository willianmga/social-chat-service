package com.reactivechat.controller.impl;

import com.reactivechat.controller.MessageBroadcasterController;
import com.reactivechat.model.message.ChatMessage;
import com.reactivechat.model.message.ChatMessage.DestinationType;
import com.reactivechat.model.message.Message;
import com.reactivechat.model.message.ResponseMessage;
import com.reactivechat.model.session.ChatSession;
import com.reactivechat.repository.SessionRepository;
import com.reactivechat.repository.UserRepository;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class MessageBroadcasterControllerImpl implements MessageBroadcasterController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageBroadcasterControllerImpl.class);
    
    private final UserRepository usersRepository;
    private final SessionRepository sessionRepository;
    
    @Autowired
    public MessageBroadcasterControllerImpl(final UserRepository userRepository,
                                            final SessionRepository sessionRepository) {
        
        this.usersRepository = userRepository;
        this.sessionRepository = sessionRepository;
    }
    
    @Override
    public void broadcastChatMessage(final ChatSession chatSession,
                                     final ResponseMessage<ChatMessage> message) {
        
        final DestinationType destinationType = message.getPayload().getDestinationType();
        
        if (DestinationType.USER.equals(destinationType)) {
            
            broadcastToUser(message.getPayload().getDestinationId(), message);
            
        } else if (DestinationType.ALL_USERS_GROUP.equals(destinationType)) {

            broadcastToAllExceptSession(chatSession, message);
            
        } else {
            LOGGER.error("Failed to deliver message to destination type " + destinationType);
        }
    
    }
    
    @Override
    public void broadcastToAllExceptSession(final ChatSession chatSession,
                                            final Message message) {
        
        final List<ChatSession> sessions = sessionRepository
            .findAll()
            .toStream()
            .filter(existingSession -> !existingSession.getId().equals(chatSession.getId()))
            .collect(Collectors.toList());
        
        broadcastMessageToSessions(sessions, message);
        
    }
    
    @Override
    public void broadcastToUser(final String userId, final Message chatMessage) {
        
        final List<ChatSession> sessions = sessionRepository
            .findByUser(userId)
            .toStream()
            .collect(Collectors.toList());
            
        broadcastMessageToSessions(sessions, chatMessage);
    }
    
    @Override
    public void broadcastToSession(final ChatSession chatSession, final Message message) {
        broadcastMessageToSessions(Collections.singletonList(chatSession), message);
    }
    
    private void broadcastMessageToSessions(final List<ChatSession> sessions, final Message message) {
    
        sessions
            .forEach(session -> {
                try {
                    if (session.isOpen()) {
                        
                        if (session.isAuthenticated() || message.getType().isWhitelisted()) {
                            session.getWebSocketSession().getBasicRemote().sendObject(message);
                        } else {
                            LOGGER.error("Can't send message to not authenticated session: {}", session.getId());
                        }
                        
                    } else {
                        sessionRepository.deleteConnection(session.getConnectionId());
                        LOGGER.error("Can't send message to session {} because session is not opened", session.getId());
                    }
                } catch (Exception e) {
                    LOGGER.error("Error occurred while sending message to session {}. Reason: {}", session.getId(), e.getMessage());
                }
            });
     
    }
    
}
package com.reactivechat.controller;

import com.reactivechat.model.User;
import com.reactivechat.model.message.ChatMessage;
import com.reactivechat.model.message.ChatMessage.DestinationType;
import com.reactivechat.model.message.Message;
import com.reactivechat.model.message.ResponseMessage;
import com.reactivechat.repository.SessionsRepository;
import com.reactivechat.repository.UsersRepository;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.websocket.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class MessageBroadcasterControllerImpl implements MessageBroadcasterController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageBroadcasterControllerImpl.class);
    
    private final UsersRepository usersRepository;
    private final SessionsRepository sessionsRepository;
    
    @Autowired
    public MessageBroadcasterControllerImpl(final UsersRepository usersRepository, final SessionsRepository sessionsRepository) {
        this.usersRepository = usersRepository;
        this.sessionsRepository = sessionsRepository;
    }
    
    @Override
    public void broadcastChatMessage(final Session session,
                                     final ResponseMessage<ChatMessage> message) {
        
        final DestinationType destinationType = message.getPayload().getDestinationType();
        
        if (DestinationType.USER.equals(destinationType)) {
            
            final User user = usersRepository.findById(message.getPayload().getDestinationId());
            broadcastToUser(user, message);
            
        } else if (DestinationType.ALL_USERS_GROUP.equals(destinationType)) {

            broadcastToAllExceptSession(session, message);
            
        } else {
            LOGGER.error("Failed to deliver message to destination type " + destinationType);
        }
    
    }
    
    @Override
    public void broadcastToAllExceptSession(final Session session, final Message message) {
        
        final List<Session> sessions = sessionsRepository
            .findAll()
            .stream()
            .filter(existingSession -> !existingSession.getId().equals(session.getId()))
            .collect(Collectors.toList());
        
        broadcastMessageToSessions(sessions, message);
        
    }
    
    @Override
    public void broadcastToUser(final User user, final Message message) {
        final List<Session> sessions = sessionsRepository.findByUser(user);
        broadcastMessageToSessions(sessions, message);
    }
    
    @Override
    public void broadcastToSession(final Session session, final Message message) {
        broadcastMessageToSessions(Collections.singletonList(session), message);
    }
    
    private void broadcastMessageToSessions(final List<Session> sessions, final Message message) {
    
        sessions
            .forEach(session -> {
                try {
                    if (session.isOpen()) { // TODO: check that session is authenticated
                        session.getBasicRemote().sendObject(message);
                    } else {
                        LOGGER.error("Can't send message to session {} because session is not opened", session.getId());
                        // TODO: remove session
                    }
                } catch (Exception e) {
                    LOGGER.error("Error occurred while sending message to session {}. Reason: {}", session.getId(), e.getMessage());
                }
            });
     
    }
    
}
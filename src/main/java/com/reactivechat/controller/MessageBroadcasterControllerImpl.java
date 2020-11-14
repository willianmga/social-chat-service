package com.reactivechat.controller;

import com.reactivechat.exception.ChatException;
import com.reactivechat.model.Destination;
import com.reactivechat.model.Destination.DestinationType;
import com.reactivechat.model.Message;
import com.reactivechat.model.User;
import com.reactivechat.repository.InMemorySessionsRepository;
import com.reactivechat.repository.UsersRepository;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import javax.websocket.EncodeException;
import javax.websocket.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class MessageBroadcasterControllerImpl implements MessageBroadcasterController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageBroadcasterControllerImpl.class);
    
    private final UsersRepository usersRepository;
    private final InMemorySessionsRepository sessionsRepository;
    
    @Autowired
    public MessageBroadcasterControllerImpl(final UsersRepository usersRepository, final InMemorySessionsRepository sessionsRepository) {
        this.usersRepository = usersRepository;
        this.sessionsRepository = sessionsRepository;
    }
    
    public void broadcast(final Message message) {
    
        final Destination destination = message.getDestination();
        
        if (DestinationType.USER.equals(destination.getDestinationType())) {
            final User user = usersRepository.findById(destination.getDestinationId());
            broadcastToUser(user, message);
        } else {
            LOGGER.error("Failed to deliver message to destination type " + destination.getDestinationType());
        }
    
    }
    
    @Override
    public void broadcastToUser(final User user, final Message message) {
        
        try {
            final List<Session> sessions = sessionsRepository.findByUser(user);
            broadcastMessageToSessions(sessions, message);
        } catch (ChatException e) {
            LOGGER.error("Failed to deliver message to user " + user.getUsername() + ". Reason: " + e.getMessage());
        }
        
    }
    
    @Override
    public void broadcastToSession(final Session session, final Message message) {
        
        try {
            broadcastMessageToSessions(Collections.singletonList(session), message);
        } catch (ChatException e) {
            LOGGER.error("Failed to deliver message to session " + session.getId() + ". Reason: " + e.getMessage());
        }
        
    }
    
    private void broadcastMessageToSessions(final List<Session> sessions, final Message message) throws ChatException {
    
        sessions
            .forEach(session -> {
    
                try {
                    session.getBasicRemote().sendObject(message);
                } catch (IOException e) {
                    throw new ChatException("Failed to send message to session " + session.getId());
                } catch (EncodeException e) {
                    throw new ChatException("Failed to encode message to session " + session.getId());
                }
                
            });
     
    }
    
}
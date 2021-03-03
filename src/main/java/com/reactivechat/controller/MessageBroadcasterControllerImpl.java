package com.reactivechat.controller;

import com.reactivechat.model.Destination;
import com.reactivechat.model.Destination.DestinationType;
import com.reactivechat.model.Message;
import com.reactivechat.model.User;
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
    
    public void broadcast(final Session session, final Message message) {
    
        final Destination destination = message.getDestination();
        
        if (DestinationType.USER.equals(destination.getDestinationType())) {
            
            final User user = usersRepository.findById(destination.getDestinationId());
            broadcastToUser(user, message);
            
        } else if (DestinationType.ALL_USERS_GROUP.equals(destination.getDestinationType())) {

            broadcastToAllExceptSession(session, message);
            
        } else {
            LOGGER.error("Failed to deliver message to destination type " + destination.getDestinationType());
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
                    session.getBasicRemote().sendObject(message);
                } catch (Exception e) {
                    LOGGER.error("Error occurred while sending message to session " + session.getId() + ": " + e.getMessage());
                }
                
            });
     
    }
    
}
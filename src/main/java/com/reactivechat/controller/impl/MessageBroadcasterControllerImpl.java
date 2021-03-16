package com.reactivechat.controller.impl;

import com.reactivechat.controller.MessageBroadcasterController;
import com.reactivechat.model.message.ChatMessage;
import com.reactivechat.model.message.ChatMessage.DestinationType;
import com.reactivechat.model.message.Message;
import com.reactivechat.model.message.ResponseMessage;
import com.reactivechat.model.session.ChatSession;
import com.reactivechat.repository.SessionRepository;
import com.reactivechat.repository.UserRepository;
import java.util.concurrent.ExecutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@Controller
public class MessageBroadcasterControllerImpl implements MessageBroadcasterController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageBroadcasterControllerImpl.class);
    
    private final ExecutorService executorService;
    private final UserRepository usersRepository;
    private final SessionRepository sessionRepository;
    
    @Autowired
    public MessageBroadcasterControllerImpl(final ExecutorService executorService,
                                            final UserRepository userRepository,
                                            final SessionRepository sessionRepository) {
        
        this.executorService = executorService;
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
        
        final Flux<ChatSession> sessions = sessionRepository.findAllConnections()
            .filter(existingSession -> !existingSession.getConnectionId().equals(chatSession.getConnectionId()));
        
        broadcastMessageToSessions(sessions, message);
        
    }
    
    @Override
    public void broadcastToUser(final String userId, final Message chatMessage) {
        
        // TODO: find a mapping between server session and stored session in order to find user
        // TODO: current behavior lists all reauthentications of the user
        
        final Flux<ChatSession> sessions = sessionRepository
            .findByUser(userId);
            
        broadcastMessageToSessions(sessions, chatMessage);
    }
    
    @Override
    public void broadcastToSession(final ChatSession chatSession, final Message message) {
        broadcastMessageToSessions(Flux.just(chatSession), message);
    }
    
    private void broadcastMessageToSessions(final Flux<ChatSession> sessions, final Message message) {
    
        sessions
            .publishOn(Schedulers.fromExecutorService(executorService))
            .subscribe(session -> {
                try {
                    if (session.isOpen()) {
                        session.getWebSocketSession().getBasicRemote().sendObject(message);
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
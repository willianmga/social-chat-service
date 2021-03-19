package com.reactivechat.broadcast;

import com.reactivechat.message.message.ChatMessage;
import com.reactivechat.message.message.ChatMessage.DestinationType;
import com.reactivechat.message.message.Message;
import com.reactivechat.message.message.ResponseMessage;
import com.reactivechat.session.session.ChatSession;
import com.reactivechat.session.SessionRepository;
import java.util.concurrent.ExecutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@Controller
public class BroadcasterControllerImpl implements BroadcasterController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(BroadcasterControllerImpl.class);
    
    private final ExecutorService executorService;
    private final SessionRepository sessionRepository;
    
    @Autowired
    public BroadcasterControllerImpl(final ExecutorService executorService,
                                     final SessionRepository sessionRepository) {
        
        this.executorService = executorService;
        this.sessionRepository = sessionRepository;
    }
    
    @Override
    public void broadcastChatMessage(final ChatSession chatSession,
                                     final ResponseMessage<ChatMessage> message) {
        
        final DestinationType destinationType = message.getPayload().getDestinationType();
        final String senderUserId = message.getPayload().getFrom();
        final String destinationId = message.getPayload().getDestinationId();
    
        switch (destinationType) {
            case USER:
                broadcastToUser(destinationId, message);
                LOGGER.info("Messaged sent from user {} to user {}", senderUserId, destinationId);
                break;
            case ALL_USERS_GROUP:
                broadcastToAllExceptSession(chatSession, message);
                LOGGER.info("Messaged sent from user {} to all users", senderUserId);
                break;
            default:
                LOGGER.error("Failed to deliver message to destination type " + destinationType);
        }
        
    }
    
    @Override
    public void broadcastToAllExceptSession(final ChatSession chatSession,
                                            final Message message) {
        
        // TODO: check why new contact message isn't being sent.
        
        final Flux<ChatSession> sessions = sessionRepository.findAllConnections()
            .filter(session -> !session.getConnectionId().equals(chatSession.getConnectionId()));
    
        broadcast(sessions, message);
        
    }
    
    @Override
    public void broadcastToUser(final String userId, final Message chatMessage) {
        
        // TODO: find a mapping between server session and stored session in order to find user
        // TODO: current behavior lists all reauthentications of the user
        
        final Flux<ChatSession> sessions = sessionRepository
            .findByUser(userId);
    
        broadcast(sessions, chatMessage);
    }
    
    @Override
    public void broadcastToSession(final ChatSession chatSession, final Message message) {
        broadcast(Flux.just(chatSession), message);
    }
    
    private void broadcast(final Flux<ChatSession> sessions, final Message message) {
    
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
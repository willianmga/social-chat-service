package live.socialchat.chat.broadcast;

import java.util.concurrent.ExecutorService;
import live.socialchat.chat.message.message.ChatMessage;
import live.socialchat.chat.message.message.ChatMessage.DestinationType;
import live.socialchat.chat.message.message.Message;
import live.socialchat.chat.message.message.ResponseMessage;
import live.socialchat.chat.session.SessionRepository;
import live.socialchat.chat.session.session.ChatSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

/**
 * Broadcaster of messages to online users. Users not online should receive messages via push notification triggered by
 * the clients.
 */
@Service
public class BroadcasterServiceImpl implements BroadcasterService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(BroadcasterServiceImpl.class);
    
    private final ExecutorService executorService;
    private final SessionRepository sessionRepository;
    
    @Autowired
    public BroadcasterServiceImpl(final ExecutorService executorService,
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

        final Flux<ChatSession> sessions = sessionRepository.findAllActiveSessions()
            .filter(session -> !session.getConnectionId().equals(chatSession.getConnectionId()));
    
        broadcast(sessions, message);
        
    }
    
    @Override
    public void broadcastToUser(final String userId, final Message chatMessage) {
        broadcast(sessionRepository.findAllActiveSessionsByUser(userId), chatMessage);
    }
    
    @Override
    public void broadcastToSession(final ChatSession chatSession, final Message message) {
        broadcast(Flux.just(chatSession), message);
    }
    
    private void broadcast(final Flux<ChatSession> sessions, final Message message) {
    
        sessions
            .publishOn(Schedulers.fromExecutorService(executorService))
            .subscribe(chatSession -> {
                try {
                    if (chatSession.isOpen()) {
                        
                        if (chatSession.isLocal()) {
                            chatSession
                                .getWebSocketSession()
                                .getBasicRemote()
                                .sendObject(message);
                        } else {
                            LOGGER.info("Can't handle remote session. Operation not supported");
                        }
                        
                    } else {
                        sessionRepository.deleteSession(chatSession);
                        LOGGER.error("Can't send message to session {} because session is not opened", chatSession.getId());
                    }
                } catch (Exception e) {
                    LOGGER.error("Error occurred while sending message to session {}. Reason: {}", chatSession.getId(), e.getMessage());
                }
            });
     
    }
    
}
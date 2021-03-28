package live.socialchat.chat.server;

import live.socialchat.chat.broadcast.BroadcasterService;
import live.socialchat.chat.message.message.MessageType;
import live.socialchat.chat.message.message.ResponseMessage;
import live.socialchat.chat.session.SessionRepository;
import live.socialchat.chat.session.session.ChatSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServerMessageServiceImpl implements ServerMessageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerMessageServiceImpl.class);
    
    private final BroadcasterService broadcasterService;
    private final SessionRepository sessionRepository;
    
    @Autowired
    public ServerMessageServiceImpl(final BroadcasterService broadcasterService,
                                    final SessionRepository sessionRepository) {
        
        this.broadcasterService = broadcasterService;
        this.sessionRepository = sessionRepository;
    }

    @Override
    public void handleConnected(final ChatSession chatSession) {
        sessionRepository.createSession(chatSession)
            .subscribe(result -> {
                if (result) {
                    LOGGER.info("Connection opened: {}", chatSession.getConnectionId());
                } else {
                    LOGGER.error("Current connection replaced existing connection: {}", chatSession.getConnectionId());
                }
                sendServerMessage(chatSession, MessageType.CONNECTED);
            });
    }
    
    @Override
    public void handleDisconnected(final ChatSession chatSession) {
        sessionRepository.deleteSession(chatSession)
            .subscribe((result) -> {
                LOGGER.info("Connection {} successfully closed and deleted", chatSession.getConnectionId());
            });
    }
    
    @Override
    public void handlePing(final ChatSession chatSession) {
        sendServerMessage(chatSession, MessageType.PONG);
    }

    @Override
    public void handleInvalidRequest(final ChatSession chatSession) {
        sendServerMessage(chatSession, MessageType.INVALID_REQUEST);
    }

    private void sendServerMessage(final ChatSession chatSession, final MessageType messageType) {
        
        final ResponseMessage<Object> responseMessage = ResponseMessage.builder()
            .type(messageType)
            .build();
        
        broadcasterService.broadcastToSession(chatSession, responseMessage);
        
    }
    
}

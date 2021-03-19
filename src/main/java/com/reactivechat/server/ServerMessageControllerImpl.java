package com.reactivechat.server;

import com.reactivechat.broadcast.BroadcasterController;
import com.reactivechat.message.message.MessageType;
import com.reactivechat.message.message.ResponseMessage;
import com.reactivechat.session.session.ChatSession;
import com.reactivechat.session.SessionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServerMessageControllerImpl implements ServerMessageController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerMessageControllerImpl.class);
    
    private final BroadcasterController broadcasterController;
    private final SessionRepository sessionRepository;
    
    @Autowired
    public ServerMessageControllerImpl(final BroadcasterController broadcasterController,
                                       final SessionRepository sessionRepository) {
        
        this.broadcasterController = broadcasterController;
        this.sessionRepository = sessionRepository;
    }
    
    @Override
    public void handlePing(final ChatSession chatSession) {
        handle(chatSession, MessageType.PONG);
    }
    
    @Override
    public void handleConnected(final ChatSession chatSession) {
        handle(chatSession, MessageType.CONNECTED);
        LOGGER.info("Connection opened: {}", chatSession.getConnectionId());
    }
    
    @Override
    public void handleDisconnected(final ChatSession chatSession) {
        sessionRepository.deleteConnection(chatSession.getConnectionId())
            .subscribe((result) -> {
               if (result) {
                   LOGGER.info("Connection {} successfully closed and deleted", chatSession.getConnectionId());
               } else {
                   LOGGER.info("Failed to delete closed connection {}", chatSession.getConnectionId());
               }
            });
    }
    
    @Override
    public void handleNotAuthenticated(final ChatSession chatSession) {
        handle(chatSession, MessageType.NOT_AUTHENTICATED);
    }
    
    @Override
    public void handleInvalidRequest(final ChatSession chatSession) {
        handle(chatSession, MessageType.INVALID_REQUEST);
    }
    
    // TODO: make non blocking
    private void handle(final ChatSession chatSession, final MessageType messageType) {
        
        ResponseMessage<Object> responseMessage = ResponseMessage
            .builder()
            .type(messageType)
            .build();
        
        broadcasterController.broadcastToSession(chatSession, responseMessage);
        
    }
    
}

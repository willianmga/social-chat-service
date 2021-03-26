package com.reactivechat.server;

import com.reactivechat.broadcast.BroadcasterController;
import com.reactivechat.message.message.MessageType;
import com.reactivechat.message.message.ResponseMessage;
import com.reactivechat.session.SessionRepository;
import com.reactivechat.session.session.ChatSession;
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
    public void handleConnected(final ChatSession chatSession) {
        sessionRepository.createSession(chatSession)
            .subscribe(result -> {
                if (result) {
                    LOGGER.info("Connection opened: {}", chatSession.getConnectionId());
                } else {
                    LOGGER.error("Failed to open connection: {}", chatSession.getConnectionId());
                }
                sendServerMessage(chatSession, MessageType.CONNECTED);
            });
    }
    
    @Override
    public void handleDisconnected(final ChatSession chatSession) {
        sessionRepository.deleteSession(chatSession)
            .subscribe((result) -> {
               if (result) {
                   LOGGER.info("Connection {} successfully closed and deleted", chatSession.getConnectionId());
               } else {
                   LOGGER.error("Failed to delete closed connection {}", chatSession.getConnectionId());
               }
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
        
        broadcasterController.broadcastToSession(chatSession, responseMessage);
        
    }
    
}

package com.reactivechat.controller.impl;

import com.reactivechat.controller.ClientServerMessageController;
import com.reactivechat.controller.MessageBroadcasterController;
import com.reactivechat.model.message.MessageType;
import com.reactivechat.model.message.ResponseMessage;
import com.reactivechat.model.session.ChatSession;
import com.reactivechat.repository.SessionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClientServerMessageControllerImpl implements ClientServerMessageController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientServerMessageControllerImpl.class);
    
    private final MessageBroadcasterController broadcasterController;
    private final SessionRepository sessionRepository;
    
    @Autowired
    public ClientServerMessageControllerImpl(final MessageBroadcasterController broadcasterController,
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

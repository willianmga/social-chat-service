package com.reactivechat.controller.impl;

import com.reactivechat.controller.ClientServerMessageController;
import com.reactivechat.controller.MessageBroadcasterController;
import com.reactivechat.model.message.MessageType;
import com.reactivechat.model.message.ResponseMessage;
import javax.websocket.Session;
import org.springframework.stereotype.Service;

@Service
public class ClientServerMessageControllerImpl implements ClientServerMessageController {

    private final MessageBroadcasterController broadcasterController;
    
    public ClientServerMessageControllerImpl(final MessageBroadcasterController broadcasterController) {
        this.broadcasterController = broadcasterController;
    }
    
    @Override
    public void handlePing(final Session session) {
        handle(session, MessageType.PONG);
    }
    
    @Override
    public void handleConnected(final Session session) {
        handle(session, MessageType.CONNECTED);
    }
    
    @Override
    public void handleDisconnected(final Session session) {
        handle(session, MessageType.DISCONNECTED);
    }
    
    @Override
    public void handleNotAuthenticated(final Session session) {
        handle(session, MessageType.NOT_AUTHENTICATED);
    }
    
    private void handle(final Session session, final MessageType messageType) {
        
        ResponseMessage<Object> responseMessage = ResponseMessage
            .builder()
            .type(messageType)
            .build();
        
        broadcasterController.broadcastToSession(session, responseMessage);
        
    }
    
}

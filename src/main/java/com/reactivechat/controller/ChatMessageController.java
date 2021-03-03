package com.reactivechat.controller;

import com.reactivechat.model.message.ChatMessage;
import com.reactivechat.model.message.RequestMessage;
import javax.websocket.Session;

public interface ChatMessageController {
    
    void handleChatMessage(final Session session, final RequestMessage<ChatMessage> requestMessage);
    void handleContactsMessage(Session session);
    
}

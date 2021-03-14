package com.reactivechat.controller;

import com.reactivechat.model.message.ChatMessage;
import com.reactivechat.model.message.Message;
import com.reactivechat.model.message.ResponseMessage;
import com.reactivechat.model.session.ChatSession;

public interface MessageBroadcasterController {
    
    void broadcastChatMessage(final ChatSession chatSession, final ResponseMessage<ChatMessage> message);
    void broadcastToAllExceptSession(final ChatSession chatSession, final Message message);
    void broadcastToUser(final String userId, final Message message);
    void broadcastToSession(final ChatSession chatSession, final Message message);
    
}
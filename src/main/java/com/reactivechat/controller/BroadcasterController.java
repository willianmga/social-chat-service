package com.reactivechat.controller;

import com.reactivechat.model.message.ChatMessage;
import com.reactivechat.model.message.Message;
import com.reactivechat.model.message.ResponseMessage;
import com.reactivechat.model.session.ChatSession;

public interface BroadcasterController {
    
    void broadcastChatMessage(ChatSession chatSession, ResponseMessage<ChatMessage> message);
    void broadcastToAllExceptSession(ChatSession chatSession, Message message);
    void broadcastToUser(String userId, Message message);
    void broadcastToSession(ChatSession chatSession, Message message);
    
}
package com.reactivechat.broadcast;

import com.reactivechat.message.message.ChatMessage;
import com.reactivechat.message.message.Message;
import com.reactivechat.message.message.ResponseMessage;
import com.reactivechat.session.session.ChatSession;

public interface BroadcasterService {
    
    void broadcastChatMessage(ChatSession chatSession, ResponseMessage<ChatMessage> message);
    void broadcastToAllExceptSession(ChatSession chatSession, Message message);
    void broadcastToUser(String userId, Message message);
    void broadcastToSession(ChatSession chatSession, Message message);
    
}
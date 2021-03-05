package com.reactivechat.controller;

import com.reactivechat.model.User;
import com.reactivechat.model.message.ChatMessage;
import com.reactivechat.model.message.Message;
import com.reactivechat.model.message.ResponseMessage;
import javax.websocket.Session;

public interface MessageBroadcasterController {
    
    void broadcastChatMessage(Session session, ResponseMessage<ChatMessage> message);
    void broadcastToAllExceptSession(final Session session, final Message message);
    void broadcastToUser(final User user, final Message message);
    void broadcastToSession(final Session session, final Message message);
    
}
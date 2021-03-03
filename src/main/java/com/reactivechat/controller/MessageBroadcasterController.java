package com.reactivechat.controller;

import com.reactivechat.model.Message;
import com.reactivechat.model.User;
import javax.websocket.Session;

public interface MessageBroadcasterController {
    
    void broadcast(final Session session, final Message message);
    
    void broadcastToAllExceptSession(final Session session, final Message message);
    
    void broadcastToUser(final User user, final Message message);
    
    void broadcastToSession(final Session session, final Message message);
    
}
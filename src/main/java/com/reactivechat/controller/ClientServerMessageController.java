package com.reactivechat.controller;

import javax.websocket.Session;

public interface ClientServerMessageController {
    
    void handlePing(Session session);
    void handleConnected(Session session);
    void handleDisconnected(Session session);
    
    void handleNotAuthenticated(Session session);
}

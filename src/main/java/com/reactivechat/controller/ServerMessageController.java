package com.reactivechat.controller;

import com.reactivechat.model.session.ChatSession;

public interface ServerMessageController {
    
    void handlePing(final ChatSession chatSession);
    void handleConnected(final ChatSession chatSession);
    void handleDisconnected(final ChatSession chatSession);
    void handleNotAuthenticated(final ChatSession chatSession);
    void handleInvalidRequest(final ChatSession chatSession);
    
}

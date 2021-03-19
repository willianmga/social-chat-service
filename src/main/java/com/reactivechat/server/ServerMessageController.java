package com.reactivechat.server;

import com.reactivechat.session.session.ChatSession;

public interface ServerMessageController {
    
    void handlePing(final ChatSession chatSession);
    void handleConnected(final ChatSession chatSession);
    void handleDisconnected(final ChatSession chatSession);
    void handleNotAuthenticated(final ChatSession chatSession);
    void handleInvalidRequest(final ChatSession chatSession);
    
}

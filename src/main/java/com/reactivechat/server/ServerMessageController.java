package com.reactivechat.server;

import com.reactivechat.session.session.ChatSession;

public interface ServerMessageController {
    void handleConnected(ChatSession chatSession);
    void handleDisconnected(ChatSession chatSession);
    void handlePing(ChatSession chatSession);
    void handleInvalidRequest(ChatSession chatSession);
}

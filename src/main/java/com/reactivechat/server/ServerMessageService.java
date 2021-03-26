package com.reactivechat.server;

import com.reactivechat.session.session.ChatSession;

public interface ServerMessageService {
    void handleConnected(ChatSession chatSession);
    void handleDisconnected(ChatSession chatSession);
    void handlePing(ChatSession chatSession);
    void handleInvalidRequest(ChatSession chatSession);
}

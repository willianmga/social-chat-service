package com.reactivechat.controller;

import com.reactivechat.model.contacs.Contact;
import com.reactivechat.model.message.ChatHistoryRequest;
import com.reactivechat.model.message.ChatMessage;
import com.reactivechat.model.session.ChatSession;

public interface ChatMessageController {

    void handleChatMessage(final ChatSession chatSession, final ChatMessage chatMessage);
    void handleContactsMessage(ChatSession chatSession);
    void handleNewContact(final Contact contact, final ChatSession chatSession);
    
    void handleChatHistory(ChatSession chatSession,
                           ChatHistoryRequest chatHistoryRequest);
}

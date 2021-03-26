package com.reactivechat.message;

import com.reactivechat.contact.Contact;
import com.reactivechat.message.message.ChatHistoryRequest;
import com.reactivechat.message.message.ChatMessage;
import com.reactivechat.session.session.ChatSession;

public interface ChatMessageService {

    void handleChatMessage(ChatSession chatSession, ChatMessage chatMessage);
    void handleContactsMessage(ChatSession chatSession);
    void handleNewContact(Contact contact, ChatSession chatSession);
    void handleChatHistory(ChatSession chatSession, ChatHistoryRequest chatHistoryRequest);
    
}

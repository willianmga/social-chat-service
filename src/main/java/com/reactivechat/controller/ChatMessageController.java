package com.reactivechat.controller;

import com.reactivechat.model.Contact;
import com.reactivechat.model.message.ChatMessage;
import javax.websocket.Session;

public interface ChatMessageController {

    void handleChatMessage(final Session session, final ChatMessage chatMessage);
    void handleContactsMessage(Session session);
    void handleNewContact(final Contact contact, final Session session);
    
}

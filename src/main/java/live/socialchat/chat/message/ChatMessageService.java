package live.socialchat.chat.message;

import live.socialchat.chat.contact.Contact;
import live.socialchat.chat.message.message.ChatHistoryRequest;
import live.socialchat.chat.message.message.ChatMessage;
import live.socialchat.chat.session.session.ChatSession;

public interface ChatMessageService {

    void handleChatMessage(ChatSession chatSession, ChatMessage chatMessage);
    void handleContactsMessage(ChatSession chatSession);
    void handleNewContact(Contact contact, ChatSession chatSession);
    void handleChatHistory(ChatSession chatSession, ChatHistoryRequest chatHistoryRequest);
    
}

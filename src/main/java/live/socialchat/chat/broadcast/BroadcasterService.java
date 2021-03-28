package live.socialchat.chat.broadcast;

import live.socialchat.chat.message.message.ChatMessage;
import live.socialchat.chat.message.message.Message;
import live.socialchat.chat.message.message.ResponseMessage;
import live.socialchat.chat.session.session.ChatSession;

public interface BroadcasterService {
    
    void broadcastChatMessage(ChatSession chatSession, ResponseMessage<ChatMessage> message);
    void broadcastToAllExceptSession(ChatSession chatSession, Message message);
    void broadcastToUser(String userId, Message message);
    void broadcastToSession(ChatSession chatSession, Message message);
    
}
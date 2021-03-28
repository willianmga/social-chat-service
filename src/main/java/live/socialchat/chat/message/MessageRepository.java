package live.socialchat.chat.message;

import live.socialchat.chat.message.message.ChatHistoryRequest;
import live.socialchat.chat.message.message.ChatMessage;
import live.socialchat.chat.message.message.ChatMessage.DestinationType;
import reactor.core.publisher.Flux;

public interface MessageRepository {
    
    void insert(ChatMessage chatMessage);
    Flux<ChatMessage> findMessages(String senderId, DestinationType destinationType, ChatHistoryRequest chatHistoryRequest);
    
}

package com.reactivechat.message;

import com.reactivechat.message.message.ChatHistoryRequest;
import com.reactivechat.message.message.ChatMessage;
import com.reactivechat.message.message.ChatMessage.DestinationType;
import reactor.core.publisher.Flux;

public interface MessageRepository {
    
    void insert(ChatMessage chatMessage);
    Flux<ChatMessage> findMessages(String senderId, DestinationType destinationType, ChatHistoryRequest chatHistoryRequest);
    
}

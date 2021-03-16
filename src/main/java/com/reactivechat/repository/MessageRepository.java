package com.reactivechat.repository;

import com.reactivechat.model.message.ChatHistoryRequest;
import com.reactivechat.model.message.ChatMessage;
import com.reactivechat.model.message.ChatMessage.DestinationType;
import reactor.core.publisher.Flux;

public interface MessageRepository {
    
    void insert(final ChatMessage chatMessage);
    Flux<ChatMessage> findMessages(final String senderId, final DestinationType destinationType, final ChatHistoryRequest chatHistoryRequest);
    
}

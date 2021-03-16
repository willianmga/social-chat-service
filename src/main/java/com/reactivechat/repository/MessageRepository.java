package com.reactivechat.repository;

import com.reactivechat.model.message.ChatMessage;

public interface MessageRepository {
    
    void insert(final ChatMessage chatMessage);
    
}

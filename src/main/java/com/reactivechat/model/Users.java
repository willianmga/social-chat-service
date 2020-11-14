package com.reactivechat.model;

public interface Users {
    
    User CHAT_SERVER = User.builder()
        .id("chat-server")
        .name("chat-server")
        .username("chat-server")
        .build();
    
    
}

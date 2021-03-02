package com.reactivechat.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Users {
    
    CHAT_SERVER(User.builder()
        .id("f42fe26e-ead5-4ab6-9a0f-2f2534fc074c")
        .name("chat-server")
        .username("chat-server")
        .status("Online")
        .build()
    );
    
    private final User user;
    
    public String getId() {
        return user.getId();
    }
    
}

package com.reactivechat.config;

import com.reactivechat.controller.MessageBroadcasterController;
import com.reactivechat.repository.SessionsRepository;
import com.reactivechat.repository.UsersRepository;
import com.reactivechat.websocket.ChatEndpoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {
    
    @Bean
    public ChatEndpoint chatEndpoint(final UsersRepository usersRepository,
                                     final SessionsRepository sessionsRepository,
                                     final MessageBroadcasterController broadcasterController) {
        
        return new ChatEndpoint(usersRepository, sessionsRepository, broadcasterController);
    }
    
}
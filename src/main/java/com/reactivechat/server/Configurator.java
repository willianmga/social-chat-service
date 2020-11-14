package com.reactivechat.server;

import com.reactivechat.controller.MessageBroadcasterController;
import com.reactivechat.repository.InMemorySessionsRepository;
import com.reactivechat.repository.SessionsRepository;
import com.reactivechat.repository.UsersRepository;
import com.reactivechat.websocket.ChatEndpoint;
import javax.websocket.server.ServerEndpointConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Configurator extends ServerEndpointConfig.Configurator {
    
    private UsersRepository usersRepository;
    private SessionsRepository sessionsRepository;
    private MessageBroadcasterController broadcasterController;
    
    @Autowired
    public Configurator(final UsersRepository usersRepository,
                        final InMemorySessionsRepository sessionsRepository,
                        final MessageBroadcasterController broadcasterController) {
        
        this.usersRepository = usersRepository;
        this.sessionsRepository = sessionsRepository;
        this.broadcasterController = broadcasterController;
    }
    
    @Override
    public <T> T getEndpointInstance(Class<T> clazz) throws InstantiationException {
    
        if (clazz == ChatEndpoint.class) {
            return (T) new ChatEndpoint(usersRepository, sessionsRepository, broadcasterController);
        }
        
        return super.getEndpointInstance(clazz);
    }
}

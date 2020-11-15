package com.reactivechat.server;

import com.reactivechat.websocket.ChatEndpoint;
import javax.websocket.server.ServerEndpointConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Configurator extends ServerEndpointConfig.Configurator {
    
    private final ChatEndpoint chatEndpoint;

    @Autowired
    public Configurator(final ChatEndpoint chatEndpoint) {
        this.chatEndpoint = chatEndpoint;
    }
    
    @Override
    public <T> T getEndpointInstance(Class<T> clazz) throws InstantiationException {
    
        if (clazz == ChatEndpoint.class) {
            return (T) chatEndpoint;
        }
        
        return super.getEndpointInstance(clazz);
    }
    
}
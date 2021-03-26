package com.reactivechat.websocket.jetty;

import java.util.Map;
import javax.websocket.server.ServerEndpointConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ServerEndpointConfigurator extends ServerEndpointConfig.Configurator {
    
    private final Map<Class<?>, Object> webSocketEndpointsMap;

    @Autowired
    public ServerEndpointConfigurator(final Map<Class<?>, Object> webSocketEndpointsMap) {
        this.webSocketEndpointsMap = webSocketEndpointsMap;
    }
    
    @Override
    public <T> T getEndpointInstance(Class<T> clazz) {
        return (T) webSocketEndpointsMap.get(clazz);
    }
    
}
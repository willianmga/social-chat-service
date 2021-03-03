package com.reactivechat.config;

import com.reactivechat.controller.AuthenticationController;
import com.reactivechat.controller.ChatMessageController;
import com.reactivechat.controller.ClientServerMessageControllerImpl;
import com.reactivechat.server.JettyEmbeddedWebSocketServer;
import com.reactivechat.websocket.ChatEndpoint;
import java.util.HashMap;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {
    
    @Bean
    public ChatEndpoint chatEndpoint(final AuthenticationController authenticationController,
                                     final ChatMessageController chatMessageController,
                                     final ClientServerMessageControllerImpl clientServerMessageController) {
        
        return new ChatEndpoint(authenticationController, chatMessageController, clientServerMessageController);
    }
    
    @Bean("webSocketEndpointsMap")
    public Map<Class<?>, Object> webSocketEndpointsMap(final ChatEndpoint chatEndpoint) {
        return new HashMap<Class<?>, Object>() {{
            put(chatEndpoint.getClass(), chatEndpoint);
        }};
    }
    
    @Bean
    public JettyEmbeddedWebSocketServer jettyWebSocketServer() {
        JettyEmbeddedWebSocketServer webSocketServer = new JettyEmbeddedWebSocketServer();
        webSocketServer.start();
        return webSocketServer;
    }
    
}
package com.reactivechat.config;

import com.reactivechat.session.AuthenticationController;
import com.reactivechat.message.ChatMessageController;
import com.reactivechat.server.ServerMessageControllerImpl;
import com.reactivechat.session.session.ServerDetails;
import com.reactivechat.jetty.JettyEmbeddedWebSocketServer;
import com.reactivechat.jetty.ServerEndpointConfigurator;
import com.reactivechat.websocket.ChatEndpoint;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JettyServerConfig {

    @Bean
    public ServerDetails serverDetails() {
        return ServerDetails.builder()
            .serverInstanceId(UUID.randomUUID().toString())
            .build();
    }
    
    @Bean
    public ChatEndpoint chatEndpoint(final AuthenticationController authenticationController,
                                     final ChatMessageController chatMessageController,
                                     final ServerMessageControllerImpl clientServerMessageController) {
        
        return new ChatEndpoint(authenticationController, chatMessageController, clientServerMessageController);
    }
    
    @Bean("webSocketEndpointsMap")
    public Map<Class<?>, Object> webSocketEndpointsMap(final ChatEndpoint chatEndpoint) {
        return new HashMap<Class<?>, Object>() {{
            put(chatEndpoint.getClass(), chatEndpoint);
        }};
    }
    
    @Bean
    public JettyEmbeddedWebSocketServer jettyWebSocketServer(final ServerEndpointConfigurator serverEndpointConfigurator) {
        JettyEmbeddedWebSocketServer webSocketServer = new JettyEmbeddedWebSocketServer(serverEndpointConfigurator);
        webSocketServer.start();
        return webSocketServer;
    }
    
}
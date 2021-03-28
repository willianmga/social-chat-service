package com.reactivechat.config;

import com.reactivechat.session.session.ServerDetails;
import com.reactivechat.websocket.ChatEndpointController;
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

    @Bean("webSocketEndpointsMap")
    public Map<Class<?>, Object> webSocketEndpointsMap(final ChatEndpointController chatEndpointController) {
        return new HashMap<Class<?>, Object>() {{
            put(chatEndpointController.getClass(), chatEndpointController);
        }};
    }

}
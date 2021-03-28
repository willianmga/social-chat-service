package live.socialchat.chat.config;

import live.socialchat.chat.session.session.ServerDetails;
import live.socialchat.chat.websocket.ChatEndpointController;
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
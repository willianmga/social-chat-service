package com.reactivechat.server;

import com.reactivechat.websocket.ChatEndpoint;
import javax.websocket.server.ServerEndpointConfig;
import javax.websocket.server.ServerEndpointConfig.Builder;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JettyEmbeddedWebSocketServer {
    
    @Autowired
    private ServerEndpointConfigurator serverEndpointConfigurator;
    
    public void start() {
    
        Server server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(8080);
        server.addConnector(connector);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);
    
        try {
            
            WebSocketServerContainerInitializer.configure(context,
                (servletContext, wsContainer) -> {

                    wsContainer.setDefaultMaxTextMessageBufferSize(65535);
                    
                    ServerEndpointConfig serverEndpointConfig = Builder
                        .create(ChatEndpoint.class, "/chat/{userId}")
                        .configurator(serverEndpointConfigurator)
                        .build();
                    
                    wsContainer.addEndpoint(serverEndpointConfig);
                });
    
            server.start();
            server.join();
            
        } catch (Throwable t) {
            t.printStackTrace(System.err);
        }
        
    }
    
}
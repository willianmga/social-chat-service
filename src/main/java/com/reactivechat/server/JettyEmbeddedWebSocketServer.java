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
    private Configurator configurator;
    
    public void start() {
    
        Server server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(8080);
        server.addConnector(connector);
    
        // Setup the basic application "context" for this application at "/"
        // This is also known as the handler tree (in jetty speak)
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);
    
        try {
            // Initialize javax.websocket layer
            WebSocketServerContainerInitializer.configure(context,
                (servletContext, wsContainer) ->
                {
                    // This lambda will be called at the appropriate place in the
                    // ServletContext initialization phase where you can initialize
                    // and configure  your websocket container.
                
                    // Configure defaults for container
                    wsContainer.setDefaultMaxTextMessageBufferSize(65535);
                
                    // Add WebSocket endpoint to javax.websocket layer
                    
                    ServerEndpointConfig serverEndpointConfig = Builder
                        .create(ChatEndpoint.class, "/chat/{userId}")
                        .configurator(configurator)
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
package com.reactivechat.websocket.jetty;

import com.reactivechat.websocket.filter.AccessTokenFilter;
import com.reactivechat.websocket.ChatEndpoint;
import java.util.EnumSet;
import javax.servlet.DispatcherType;
import javax.websocket.server.ServerEndpointConfig;
import javax.websocket.server.ServerEndpointConfig.Builder;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;
import org.springframework.stereotype.Component;

@Component
public class JettyEmbeddedWebSocketServer {
    
    private static final String SERVER_SSL_ENABLED = "jetty.server.ssl.enabled";
    private static final String SERVER_CERTIFICATE_PATH = "jetty.server.ssl.certificate";
    private static final String SERVER_CERTIFICATE_PASSWORD = "jetty.server.ssl.password";
    private static final String SERVER_SSL_PROTOCOL = "http/1.1";
    private static final String PORT_ENV = "PORT";
    private static final int DEFAULT_SERVER_PORT = 8080;
    
    private final ServerEndpointConfigurator serverEndpointConfigurator;
    
    public JettyEmbeddedWebSocketServer(final ServerEndpointConfigurator serverEndpointConfigurator) {
        this.serverEndpointConfigurator = serverEndpointConfigurator;
        start();
    }
    
    public void start() {
    
        Server server = new Server();
    
        ServerConnector connector = (sslEnabled())
            ? sslServerConnector(server)
            : httpServerConnector(server);
        
        server.setConnectors(new Connector[]{connector});
        
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);
    
        FilterHolder cors = context.addFilter(CrossOriginFilter.class,"/*", EnumSet.of(DispatcherType.REQUEST));
        cors.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
        cors.setInitParameter(CrossOriginFilter.ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, "*");
        cors.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "GET,POST,HEAD");
        cors.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM, "X-Requested-With,Content-Type,Accept,Origin");
    
        context.addFilter(AccessTokenFilter.class,"/*", EnumSet.of(DispatcherType.REQUEST));
        
        try {
            
            WebSocketServerContainerInitializer.configure(context,
                (servletContext, wsContainer) -> {

                    wsContainer.setDefaultMaxTextMessageBufferSize(65535);
                    
                    ServerEndpointConfig serverEndpointConfig = Builder
                        .create(ChatEndpoint.class, "/chat")
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
    
    private ServerConnector httpServerConnector(final Server server) {
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(getServerPort());
        return connector;
    }
    
    private ServerConnector sslServerConnector(final Server server) {
    
        HttpConfiguration https = new HttpConfiguration();
        https.addCustomizer(new SecureRequestCustomizer());
    
        SslContextFactory sslContextFactory = new SslContextFactory();
        
        sslContextFactory.setKeyStorePath(JettyEmbeddedWebSocketServer.class
            .getClassLoader().getResource(SERVER_CERTIFICATE_PATH).toExternalForm());
    
        final String password = System.getProperty(SERVER_CERTIFICATE_PASSWORD);
    
        sslContextFactory.setKeyStorePassword(password);
        sslContextFactory.setKeyManagerPassword(password);
    
        ServerConnector sslConnector = new ServerConnector(
            server,
            new SslConnectionFactory(sslContextFactory, SERVER_SSL_PROTOCOL),
            new HttpConnectionFactory(https)
        );
    
        sslConnector.setPort(getServerPort());
        
        return sslConnector;
    }
    
    private boolean sslEnabled() {
        return Boolean.parseBoolean(System.getProperty(SERVER_SSL_ENABLED, "false"));
    }
    
    private int getServerPort() {
        String portEnv = System.getenv(PORT_ENV);
        return (portEnv != null && !portEnv.isEmpty())
            ? Integer.parseInt(portEnv)
            : DEFAULT_SERVER_PORT;
    }
    
}
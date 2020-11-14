package com.reactivechat;

import com.reactivechat.server.JettyEmbeddedWebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class App {
    
    public static void main(String[] args) {
    
        ConfigurableApplicationContext applicationContext = new SpringApplicationBuilder(App.class)
            .web(false)
            .run(args);
    
        JettyEmbeddedWebSocketServer webSocketServer = applicationContext.getBean(JettyEmbeddedWebSocketServer.class);
        webSocketServer.start();
    }
    
}
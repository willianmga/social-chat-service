package com.reactivechat;

import com.reactivechat.server.JettyEmbeddedWebSocketServer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class App {
    
    public static void main(String[] args) {
        new SpringApplicationBuilder(App.class)
            .web(false)
            .run(args);
    
        new JettyEmbeddedWebSocketServer().start();
    }

}
package com.reactivechat.config;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoDatabase;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

@Configuration
public class MongoConfig {
    
    @Bean
    public MongoDatabase mongoDatabase(MongoClient mongoClient, @Value("${mongo.database}") String mongoDatabase) {
        return mongoClient.getDatabase(mongoDatabase);
    }
    
    @Bean
    public MongoClient mongoClient(List<ServerAddress> serverAddresses,
                                   CodecRegistry codecRegistry,
                                   @Value("${mongo.username}") String mongoUsername,
                                   @Value("${mongo.password}") String mongoPassword,
                                   @Value("${mongo.database}") String mongoDatabase) {
        
        MongoCredential credential = MongoCredential
            .createCredential(mongoUsername, "admin", mongoPassword.toCharArray());
        
        MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
            .credential(credential)
            .codecRegistry(codecRegistry)
            .applyToClusterSettings(builder -> builder.hosts(serverAddresses))
            .build();
        
        return MongoClients.create(mongoClientSettings);
    }
    
    @Bean
    public CodecRegistry codecRegistry() {
        return
            fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));
    }
    
    @Bean
    public List<ServerAddress> serverAddresses(@Value("${mongo.servers}") String mongoServers) {
        return Collections.unmodifiableList(Stream.of(mongoServers.split(","))
            .map(serverAddress -> {
                String[] data = serverAddress.split(":");
                String host = data[0];
                int port = Integer.parseInt(data[1]);
                return new ServerAddress(host, port);
            })
            .collect(Collectors.toList()));
    }
    
}

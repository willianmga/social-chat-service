package com.reactivechat.repository.impl;

import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;
import com.reactivechat.model.message.ChatMessage;
import com.reactivechat.repository.MessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class MongoMessageRepository implements MessageRepository {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(MongoMessageRepository.class);
    private static final String CHAT_MESSAGE_COLLECTION = "chat_message";
    
    private final MongoCollection<ChatMessage> mongoCollection;
    
    @Autowired
    public MongoMessageRepository(MongoDatabase mongoDatabase) {
        this.mongoCollection = mongoDatabase.getCollection(CHAT_MESSAGE_COLLECTION, ChatMessage.class);
    }
    
    @Override
    public void insert(final ChatMessage chatMessage) {
        Mono.from(mongoCollection.insertOne(chatMessage))
            .doOnSuccess(message -> LOGGER.info("Inserted message {}", message.getInsertedId()))
            .doOnError(error -> LOGGER.info("Error Inserting message. Reason {}", error.getMessage()))
            .subscribe();
    }
    
}

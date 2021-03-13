package com.reactivechat.repository.impl;

import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;
import com.reactivechat.model.User;
import com.reactivechat.model.session.ChatSession;
import com.reactivechat.repository.SessionRepository;
import javax.websocket.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class MongoSessionRepository implements SessionRepository {
    
    private static final String SESSIONS_COLLECTION = "user_session";
    
    private final MongoCollection mongoCollection;
    
    @Autowired
    public MongoSessionRepository(final MongoDatabase mongoDatabase) {
        this.mongoCollection = mongoDatabase.getCollection(SESSIONS_COLLECTION);
    }
    
    
    @Override
    public void create(ChatSession session) {
    
    }
    
    @Override
    public Mono<ChatSession> findByConnectionId(final String sessionId) {
        return null;
    }
    
    @Override
    public void authenticate(ChatSession chatSession,
                             User user,
                             String token) {
        
    }
    
    @Override
    public Mono<User> reauthenticate(ChatSession chatSession,
                                     String token) {
        return null;
    }
    
    @Override
    public void delete(ChatSession session) {
    
    }
    
    @Override
    public Flux<Session> findByUser(String userId) {
        return null;
    }
    
    @Override
    public Mono<User> findBySessionId(String sessionId) {
        return null;
    }
    
    @Override
    public Flux<Session> findAll() {
        return null;
    }
}

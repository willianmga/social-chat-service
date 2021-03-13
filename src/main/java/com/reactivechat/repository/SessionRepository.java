package com.reactivechat.repository;

import com.reactivechat.model.User;
import com.reactivechat.model.session.ChatSession;
import javax.websocket.Session;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SessionRepository {
    
    void create(final ChatSession chatSession);
    Mono<ChatSession> findByConnectionId(final String connectionId);
    void authenticate(final ChatSession chatSession, final User user, final String token);
    Mono<User> reauthenticate(final ChatSession chatSession, final String token);
    void delete(final ChatSession session);
    Flux<Session> findByUser(final String userId);
    Mono<User> findBySessionId(final String sessionId);
    Flux<Session> findAll();
    
}

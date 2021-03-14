package com.reactivechat.repository;

import com.reactivechat.model.User;
import com.reactivechat.model.session.ChatSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SessionRepository {
    
    void authenticate(final ChatSession chatSession, final User user, final String token);
    Mono<String> reauthenticate(final ChatSession chatSession, final String token);
    Mono<ChatSession> findByConnectionId(final String connectionId);
    void deleteConnection(final String connectionId);
    void logoff(ChatSession chatSession);
    Flux<ChatSession> findByUser(final String userId);
    Mono<String> findUserBySessionId(final String sessionId);
    Flux<ChatSession> findAll();
    
}

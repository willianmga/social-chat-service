package com.reactivechat.session;

import com.reactivechat.session.session.ChatSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SessionRepository {
    Mono<Boolean> deleteConnection(String connectionId);
    Flux<ChatSession> findByUser(String userId);
    Flux<ChatSession> findAllConnections();
}

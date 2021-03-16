package com.reactivechat.repository;

import com.reactivechat.model.contacs.User;
import com.reactivechat.model.message.ChatMessage.DestinationType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserRepository {
    
    Mono<User> create(final User user);
    Mono<User> findById(final String id);
    Mono<User> findFullDetailsByUsername(final String username);
    Flux<User> findContacts(final String userId);
    Mono<DestinationType> findDestinationType(final String destinationId);
    
}

package com.reactivechat.user;

import com.reactivechat.user.model.User;
import com.reactivechat.message.message.ChatMessage.DestinationType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserRepository {
    
    Mono<User> create(final User user);
    Mono<User> findById(final String id);
    Mono<User> findFullDetailsByUsername(final String username);
    Flux<User> findContacts(final String userId);
    Mono<DestinationType> findDestinationType(final String destinationId);
    
}

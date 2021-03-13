package com.reactivechat.repository;

import com.reactivechat.model.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserRepository {
    
    Mono<User> create(final User user);
    Mono<User> findById(final String id);
    Mono<User> findFullDetailsByUsername(final String username);
    
    boolean exists(String username);
    
    Flux<User> findContacts(final User user);
    User mapToNonSensitiveDataUser(User user);
    
}

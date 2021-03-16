package com.reactivechat.repository;

import com.reactivechat.model.Group;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GroupRepository {
    
    Mono<Group> create(final Group group);
    Flux<Group> findGroups(final String userId);
    
}

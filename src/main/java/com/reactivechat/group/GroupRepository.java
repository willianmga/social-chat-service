package com.reactivechat.group;

import com.reactivechat.group.model.Group;
import com.reactivechat.message.message.ChatMessage.DestinationType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GroupRepository {
    
    Mono<Group> create(final Group group);
    Flux<Group> findGroups(final String userId);
    Mono<DestinationType> findDestinationType(final String groupId);
    
}

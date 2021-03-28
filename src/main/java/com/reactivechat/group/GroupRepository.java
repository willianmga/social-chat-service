package com.reactivechat.group;

import com.reactivechat.group.model.Group;
import com.reactivechat.message.message.ChatMessage.DestinationType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GroupRepository {
    Mono<Group> create(Group group);
    Flux<Group> findGroups(String userId);
    Mono<DestinationType> findDestinationType(String groupId);
}

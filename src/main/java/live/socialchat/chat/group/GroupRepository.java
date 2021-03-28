package live.socialchat.chat.group;

import live.socialchat.chat.group.model.Group;
import live.socialchat.chat.message.message.ChatMessage.DestinationType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GroupRepository {
    Mono<Group> create(Group group);
    Flux<Group> findGroups(String userId);
    Mono<DestinationType> findDestinationType(String groupId);
}

package live.socialchat.chat.user;

import live.socialchat.chat.user.model.User;
import live.socialchat.chat.message.message.ChatMessage.DestinationType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserRepository {
    Flux<User> findContacts(final String userId);
    Mono<DestinationType> findDestinationType(final String destinationId);
}

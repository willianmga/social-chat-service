package live.socialchat.chat.session;

import live.socialchat.chat.session.session.ChatSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SessionRepository {
    Mono<Boolean> createSession(ChatSession chatSession);
    Mono<Void> deleteSession(ChatSession chatSession);
    Flux<ChatSession> findAllActiveSessions();
    Flux<ChatSession> findAllActiveSessionsByUser(String userId);
}

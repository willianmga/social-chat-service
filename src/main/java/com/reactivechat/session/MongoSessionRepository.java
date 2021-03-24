package com.reactivechat.session;

import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;
import com.reactivechat.session.session.ChatSession;
import java.util.HashMap;
import java.util.Map;
import javax.websocket.Session;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.include;
import static com.reactivechat.session.session.ChatSession.Status.AUTHENTICATED;

@Repository
public class MongoSessionRepository implements SessionRepository {

    private static final String SESSIONS_COLLECTION = "user_session";
    private static final String CONNECTION_ID = "connectionId";
    private static final String SERVER_DETAILS = "serverDetails";
    private static final String USER_AUTHENTICATION_DETAILS = "userAuthenticationDetails";
    private static final String USER_ID = USER_AUTHENTICATION_DETAILS + ".userId";
    private static final String SESSION_STATUS = "status";
    private static final String SESSION_TYPE = "type";
    
    private static final Bson SERVER_REQUIRED_FIELDS =
        fields(include("id", CONNECTION_ID, SERVER_DETAILS, USER_AUTHENTICATION_DETAILS, SESSION_STATUS, SESSION_TYPE));
    
    private final Map<String, Session> connectionsMap;
    private final MongoCollection<ChatSession> mongoCollection;
    
    @Autowired
    public MongoSessionRepository(final MongoDatabase mongoDatabase) {
        this.mongoCollection = mongoDatabase.getCollection(SESSIONS_COLLECTION, ChatSession.class);
        this.connectionsMap = new HashMap<>();
    }

    @Override
    public Flux<ChatSession> findByUser(final String userId) {
        return Flux.from(
                mongoCollection
                    .find(and(eq(USER_ID, userId), eq(SESSION_STATUS, AUTHENTICATED.name())))
                    .projection(SERVER_REQUIRED_FIELDS)
            )
            .filter(session -> connectionsMap.containsKey(session.getConnectionId()))
            .map(session -> buildChatSession(session, connectionsMap.get(session.getConnectionId())));
    }

    @Override
    public Flux<ChatSession> findAllConnections() {
        return Flux.fromIterable(connectionsMap.values())
            .map(ChatSession::fromSession);
    }
    
    @Override
    public Mono<Boolean> deleteConnection(final String connectionId) {
        return Mono.just(connectionsMap.remove(connectionId) != null);
    }
    
    private ChatSession buildChatSession(final ChatSession session, final Session webSocketSession) {
        
        return session.from()
            .webSocketSession(webSocketSession)
            .build();
    }
    
}

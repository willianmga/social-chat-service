package com.reactivechat.repository.impl;

import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;
import com.reactivechat.exception.ChatException;
import com.reactivechat.model.User;
import com.reactivechat.model.session.ChatSession;
import com.reactivechat.model.session.ChatSession.Status;
import com.reactivechat.model.session.ServerDetails;
import com.reactivechat.model.session.UserAuthenticationDetails;
import com.reactivechat.repository.SessionRepository;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.websocket.Session;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.include;
import static com.reactivechat.exception.ResponseStatus.INVALID_CREDENTIALS;
import static com.reactivechat.model.session.ChatSession.Status.AUTHENTICATED;
import static com.reactivechat.model.session.ChatSession.Status.LOGGED_OFF;

@Repository
public class MongoSessionRepository implements SessionRepository {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(MongoSessionRepository.class);
    
    private static final String SESSIONS_COLLECTION = "user_session";
    private static final String CONNECTION_ID = "connectionId";
    private static final String SESSION_ID = "id";
    private static final String TOKEN = "userConnectionDetails.token";
    private static final String USER_ID = "userConnectionDetails.userId";
    private static final String SESSION_STATUS = "status";
    
    private static final Bson SERVER_REQUIRED_FIELDS =
        fields(include("id", "connectionId", "serverDetails", "userConnectionDetails", "contactType"));
    
    private static final Bson AUTHENTICATION_FIELDS =
        fields(include("userConnectionDetails"));
    
    private final Map<String, Session> connectionsMap;
    private final MongoCollection<ChatSession> mongoCollection;
    private final ServerDetails serverDetails;
    
    @Autowired
    public MongoSessionRepository(final MongoDatabase mongoDatabase,
                                  final ServerDetails serverDetails) {
        this.mongoCollection = mongoDatabase.getCollection(SESSIONS_COLLECTION, ChatSession.class);
        this.connectionsMap = new HashMap<>();
        this.serverDetails = serverDetails;
    }

    @Override
    public void authenticate(final ChatSession chatSession, final User user, final String token) {
    
        final Optional<ChatSession> chatSessionOpt = findByActiveToken(token)
            .blockOptional();
    
        if (chatSessionOpt.isPresent()) {
            throw new ChatException("Failed to authenticate session: Token is already in use by another session");
        }
    
        final UserAuthenticationDetails userAuthenticationDetails = UserAuthenticationDetails.builder()
            .userId(user.getId())
            .token(token)
            .build();
    
        final ChatSession newAuthenticatedSession = chatSession.from()
            .userAuthenticationDetails(userAuthenticationDetails)
            .status(AUTHENTICATED)
            .build();
    
        final Session webSocketSession = chatSession.getWebSocketSession();
        connectionsMap.put(webSocketSession.getId(), webSocketSession);
    
        Mono.from(mongoCollection.insertOne(newAuthenticatedSession))
            .doOnSuccess(result -> LOGGER.info("Inserted new session {}", result.getInsertedId()))
            .subscribe();
        
    }
    
    @Override
    public Mono<String> reauthenticate(final ChatSession chatSession,
                                       final String token) {
    
        final Optional<ChatSession> chatSessionOpt = findByActiveToken(token)
            .blockOptional();
    
        if (chatSessionOpt.isPresent()) {
    
            final ChatSession existingSession = chatSessionOpt.get();
            final String userId = existingSession.getUserAuthenticationDetails().getUserId();
    
            final ChatSession newSession = existingSession.from()
                .id(chatSession.getId())
                .connectionId(chatSession.getConnectionId())
                .userDeviceDetails(chatSession.getUserDeviceDetails())
                .serverDetails(serverDetails)
                .startDate(OffsetDateTime.now().toString())
                .status(AUTHENTICATED)
                .build();
    
            final Session webSocketSession = chatSession.getWebSocketSession();
            connectionsMap.put(webSocketSession.getId(), webSocketSession);
            mongoCollection.insertOne(newSession);
    
            Mono.from(mongoCollection.insertOne(newSession))
                .doOnSuccess(result -> LOGGER.info("Inserted reauthenticate session {}", result.getInsertedId()))
                .subscribe();

            return Mono.just(userId);
        }
    
        throw new ChatException("Token isn't assigned to any session", INVALID_CREDENTIALS);
    }
    
    @Override
    public Mono<ChatSession> findByConnectionId(final String connectionId) {
        
        final Session webSocketSession = connectionsMap.get(connectionId);
        
        if (webSocketSession != null) {
            return Mono.from(
                mongoCollection
                    .find(and(
                        eq(CONNECTION_ID, webSocketSession.getId()),
                        eq(SESSION_STATUS, AUTHENTICATED.name())
                        )
                    )
                    .projection(SERVER_REQUIRED_FIELDS)
                    .first()
                )
                .map(session -> buildChatSession(session, webSocketSession));
        }
        
        return Mono.empty();
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
    public Mono<String> findUserBySessionId(final String sessionId) {
    
        return Mono.from(
                mongoCollection
                    .find(and(
                            eq(SESSION_ID, sessionId),
                            eq(SESSION_STATUS, AUTHENTICATED.name())
                        )
                    )
                    .projection(AUTHENTICATION_FIELDS)
                    .first()
            )
            .map(session -> session.getUserAuthenticationDetails().getUserId());
    }
    
    @Override
    public Flux<ChatSession> findAll() {
    
        return Flux.from(
                mongoCollection
                    .find(eq(SESSION_STATUS, AUTHENTICATED.name()))
                    .projection(SERVER_REQUIRED_FIELDS)
            )
            .filter(session -> connectionsMap.containsKey(session.getConnectionId()))
            .map(session -> buildChatSession(session, connectionsMap.get(session.getConnectionId())));
        
    }
    
    @Override
    public void deleteConnection(final String connectionId) {
        connectionsMap.remove(connectionId);
    }
    
    @Override
    public void logoff(final ChatSession chatSession) {
        
        if (chatSession.isAuthenticated()) {
            
            mongoCollection.updateMany(
                eq(TOKEN, chatSession.getUserAuthenticationDetails().getToken()),
                Collections.singletonList(
                    eq(SESSION_STATUS, LOGGED_OFF.name())
                )
            );
            
            deleteConnection(chatSession.getId());
            
            LOGGER.info("Session {} successfully logged off", chatSession.getId());
        }
        
    }
    
    private Mono<ChatSession> findByActiveToken(final String token) {
        return Mono.from(
            mongoCollection
                .find(and(eq(TOKEN, token), eq(SESSION_STATUS, AUTHENTICATED.name())))
                .first()
        );
    }
    
    private ChatSession buildChatSession(final ChatSession session,
                                         final Session webSocketSession) {
        
        return session.from()
            .webSocketSession(webSocketSession)
            .build();
    }
    
}

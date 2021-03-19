package com.reactivechat.user;

import com.mongodb.client.model.Filters;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;
import com.reactivechat.exception.ChatException;
import com.reactivechat.exception.ResponseStatus;
import com.reactivechat.user.model.User;
import com.reactivechat.message.message.ChatMessage.DestinationType;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.ne;
import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.include;

@Repository
public class MongoUserRepository implements UserRepository {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(MongoUserRepository.class);
    private static final String USER_COLLECTION_NAME = "user";
    private static final String USER_ID = "_id";
    private static final String USERNAME = "username";
    private static final String CONTACT_TYPE = "contactType";
    
    private static final Bson NON_SENSITIVE_FIELDS =
        fields(include("id", "name", "avatar", "description", CONTACT_TYPE));

    private final MongoCollection<User> mongoCollection;
    
    @Autowired
    public MongoUserRepository(final MongoDatabase mongoDatabase) {
        this.mongoCollection = mongoDatabase.getCollection(USER_COLLECTION_NAME, User.class);
    }
    
    @Override
    public Mono<User> create(final User user) {
    
        usernameExists(user.getUsername())
            .blockOptional()
            .ifPresent((result) -> {
                throw new ChatException("username already taken", ResponseStatus.USERNAME_IN_USE);
            });

        return Mono.from(mongoCollection.insertOne(user))
            .doOnSuccess(result -> LOGGER.info("Inserted user {}", result.getInsertedId()))
            .doOnError(error -> LOGGER.info("Failed to insert user. Reason: {}", error.getMessage()))
            .flatMap(result -> Mono.just(user));
    }
    
    @Override
    public Mono<User> findById(final String id) {
        return Mono.from(
                mongoCollection
                    .find(eq(USER_ID, id))
                    .projection(NON_SENSITIVE_FIELDS)
                    .first()
            );
    }
    
    @Override
    public Mono<User> findFullDetailsByUsername(final String username) {
        return Mono.from(
                mongoCollection
                    .find(eq(USERNAME, username))
                    .first()
            );
    }
    
    @Override
    public Flux<User> findContacts(final String userId) {
        return Flux
            .from(
                mongoCollection
                    .find(ne(USER_ID, userId))
                    .projection(NON_SENSITIVE_FIELDS)
            );
    }
    
    @Override
    public Mono<DestinationType> findDestinationType(final String userId) {
        return Mono.from(
                mongoCollection.find(Filters.eq(USER_ID, userId))
                    .projection(fields(include(CONTACT_TYPE)))
            )
            .map(group -> DestinationType.USER);
    }
    
    private Mono<User> usernameExists(final String username) {
        
        return Mono.from(
            mongoCollection.find(eq(USERNAME, username))
                .projection(fields(include(USER_ID)))
                .first()
            );
    }

}

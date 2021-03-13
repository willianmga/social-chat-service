package com.reactivechat.repository.impl;

import com.mongodb.client.result.InsertOneResult;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;
import com.reactivechat.exception.ChatException;
import com.reactivechat.exception.ResponseStatus;
import com.reactivechat.model.Contact.ContactType;
import com.reactivechat.model.User;
import com.reactivechat.repository.UserRepository;
import java.util.UUID;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.include;

@Repository
public class MongoUserRepository implements UserRepository {
    
    private static final Bson NON_SENSITIVE_FIELDS =
        fields(include("id", "name", "avatar", "description", "contactType"));
    
    private static final Bson SERVER_REQUIRED_FIELDS =
        fields(include("id", "username", "name", "contactType"));
    
    private final MongoCollection<User> mongoCollection;
    
    @Autowired
    public MongoUserRepository(final MongoDatabase mongoDatabase) {
        this.mongoCollection = mongoDatabase.getCollection("user", User.class);
    }
    
    @Override
    public Mono<User> create(User user) {
    
        if (exists(user.getUsername())) {
            throw new ChatException("username already taken", ResponseStatus.USERNAME_IN_USE);
        }
    
        final User newUser = User.builder()
            .id(UUID.randomUUID().toString())
            .username(user.getUsername())
            .password(user.getPassword())
            .name(user.getName())
            .avatar(user.getAvatar())
            .description(user.getDescription())
            .contactType(ContactType.USER)
            .build();
    
        InsertOneResult result = Mono.from(mongoCollection.insertOne(newUser))
            .block();
    
        return Mono.just(newUser);
    }
    
    @Override
    public Mono<User> findById(final String id) {
        return Mono
            .from(
                mongoCollection
                    .find(eq("id", id))
                    .projection(SERVER_REQUIRED_FIELDS)
                    .first()
            );
    }
    
    @Override
    public Mono<User> findFullDetailsByUsername(final String username) {
        return Mono
            .from(
                mongoCollection.find(eq("username", username)).first()
            );
    }
    
    @Override
    public boolean exists(final String username) {
    
        return Mono.from(
                mongoCollection.find(eq("username", username))
                    .projection(fields(include("id")))
                    .first()
            )
            .blockOptional()
            .isPresent();
    }
    
    @Override
    public Flux<User> findContacts(final User userToFind) {
        return Flux
            .from(
                mongoCollection
                    .find()
                    .projection(NON_SENSITIVE_FIELDS)
            )
            .filter(user -> !user.getId().equals(userToFind.getId()));
    }
    
    @Override
    public User mapToNonSensitiveDataUser(final User user) {
        return User.builder()
            .id(user.getId())
            .name(user.getName())
            .description(user.getDescription())
            .avatar(user.getAvatar())
            .contactType(user.getContactType())
            .build();
    }
    
}

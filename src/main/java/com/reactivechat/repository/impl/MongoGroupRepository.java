package com.reactivechat.repository.impl;

import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;
import com.reactivechat.model.Group;
import com.reactivechat.repository.GroupRepository;
import java.util.UUID;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.include;

@Repository
public class MongoGroupRepository implements GroupRepository {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(MongoGroupRepository.class);
    
    private static final String GROUPS_COLLECTION = "chat_group";
    
    private static final Bson NON_SENSITIVE_FIELDS =
        fields(include("id", "name", "avatar", "description", "contactType"));
    
    private final MongoCollection<Group> mongoCollection;
    
    @Autowired
    public MongoGroupRepository(MongoDatabase mongoDatabase) {
        this.mongoCollection = mongoDatabase.getCollection(GROUPS_COLLECTION, Group.class);
    }
    
    @Override
    public Mono<Group> create(final Group group) {
    
        final Group newGroup = Group.builder()
            .id(UUID.randomUUID().toString())
            .name(group.getName())
            .avatar(group.getAvatar())
            .build();
    
        Mono.from(mongoCollection.insertOne(newGroup))
            .doOnError(error -> LOGGER.error("Failed to insert group. Reason: {}", error.getMessage()))
            .subscribe(result -> LOGGER.info("Created group {}", result.getInsertedId()));
        
        return Mono.just(newGroup);
    }
    
    @Override
    public Flux<Group> findGroups(final String userId) {
        return Flux.from(
            mongoCollection.find()
                .projection(NON_SENSITIVE_FIELDS)
        );
    }
    
}

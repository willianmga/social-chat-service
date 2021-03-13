package com.reactivechat.repository.impl;

import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;
import com.reactivechat.model.Group;
import com.reactivechat.model.User;
import com.reactivechat.repository.GroupRepository;
import java.util.UUID;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.include;

@Repository
public class MongoGroupRepository implements GroupRepository {
    
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
    
        mongoCollection.insertOne(newGroup);
        
        return Mono.just(newGroup);
    }
    
    @Override
    public Flux<Group> findGroups(final User user) {
        return Flux.from(
            mongoCollection.find()
                .projection(NON_SENSITIVE_FIELDS)
        );
    }
    
}

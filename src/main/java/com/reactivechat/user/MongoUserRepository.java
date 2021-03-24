package com.reactivechat.user;

import com.mongodb.client.model.Filters;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;
import com.reactivechat.message.message.ChatMessage.DestinationType;
import com.reactivechat.user.model.User;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.mongodb.client.model.Filters.ne;
import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.include;

@Repository
public class MongoUserRepository implements UserRepository {
    
    private static final String USER_COLLECTION_NAME = "user";
    private static final String USER_ID = "_id";
    private static final String CONTACT_TYPE = "contactType";
    
    private static final Bson NON_SENSITIVE_FIELDS =
        fields(include("id", "name", "avatar", "description", CONTACT_TYPE));

    private final MongoCollection<User> mongoCollection;
    
    @Autowired
    public MongoUserRepository(final MongoDatabase mongoDatabase) {
        this.mongoCollection = mongoDatabase.getCollection(USER_COLLECTION_NAME, User.class);
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

}

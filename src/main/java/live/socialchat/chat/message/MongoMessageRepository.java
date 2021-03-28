package live.socialchat.chat.message;

import com.mongodb.client.model.Filters;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;
import live.socialchat.chat.message.message.ChatHistoryRequest;
import live.socialchat.chat.message.message.ChatMessage;
import live.socialchat.chat.message.message.ChatMessage.DestinationType;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.lt;
import static com.mongodb.client.model.Filters.or;

@Repository
public class MongoMessageRepository implements MessageRepository {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(MongoMessageRepository.class);
    private static final String CHAT_MESSAGE_COLLECTION = "chat_message_object_id";
    private static final String MESSAGE_ID = "_id";
    private static final String SENDER_ID = "from";
    private static final String DESTINATION_ID = "destinationId";
    
    private static final BiFunction<String, String, Bson> CONTACT_FILTER_FUNCTION = (senderId, destinationId) ->
        or(
            and(eq(SENDER_ID, senderId), eq(DESTINATION_ID, destinationId)),
            and(eq(SENDER_ID, destinationId), eq(DESTINATION_ID, senderId))
        );
    
    private static final Function<String, Bson> GROUP_FILTER_FUNCTION = (destinationId) ->
        eq(DESTINATION_ID, destinationId);
    
    private final MongoCollection<ChatMessage> mongoCollection;
    private final Integer messagesLimit;
    
    @Autowired
    public MongoMessageRepository(MongoDatabase mongoDatabase,
                                  @Value("${chat.history.messages.limit}") Integer messagesLimit) {
        this.mongoCollection = mongoDatabase.getCollection(CHAT_MESSAGE_COLLECTION, ChatMessage.class);
        this.messagesLimit = messagesLimit;
    }
    
    @Override
    public void insert(final ChatMessage chatMessage) {
        Mono.from(mongoCollection.insertOne(chatMessage))
            .doOnSuccess(message -> LOGGER.info("Inserted message {}", message.getInsertedId()))
            .doOnError(error -> LOGGER.info("Error Inserting message. Reason {}", error.getMessage()))
            .subscribe();
    }
    
    @Override
    public Flux<ChatMessage> findMessages(final String senderId,
                                          final DestinationType destinationType,
                                          final ChatHistoryRequest chatHistoryRequest) {
    
        final Bson senderAndDestinationFilter = (DestinationType.USER == destinationType)
            ? CONTACT_FILTER_FUNCTION.apply(senderId, chatHistoryRequest.getDestinationId())
            : GROUP_FILTER_FUNCTION.apply(chatHistoryRequest.getDestinationId());
    
        final Bson messagesFilter = (chatHistoryRequest.getLastMessageId() != null && !chatHistoryRequest.getLastMessageId().trim().isEmpty())
            ? and(lt(MESSAGE_ID, chatHistoryRequest.getLastMessageId()), senderAndDestinationFilter)
            : senderAndDestinationFilter;
        
        return Flux.from(
                mongoCollection
                    .find(messagesFilter)
                    .limit(messagesLimit)
                    .sort(Filters.eq(MESSAGE_ID, -1))
            );
    }
    
}

package com.reactivechat.message.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

@Getter
@Builder
@ToString
public class ChatMessage {
    
    @BsonId @JsonIgnore
    private final ObjectId objectId;
    private final String from;
    private final DestinationType destinationType;
    private final String destinationId;
    private final String content;
    private final MimeType mimeType;
    private final String date;
    
    @BsonCreator
    public ChatMessage(@BsonProperty("objectId") final ObjectId objectId,
                       @BsonProperty("from") final String from,
                       @BsonProperty("destinationType") final DestinationType destinationType,
                       @BsonProperty("destinationId") final String destinationId,
                       @BsonProperty("content") final String content,
                       @BsonProperty("mimeType") final MimeType mimeType,
                       @BsonProperty("date") final String date) {
        this.objectId = objectId;
        this.from = from;
        this.destinationType = destinationType;
        this.destinationId = destinationId;
        this.content = content;
        this.mimeType = mimeType;
        this.date = date;
    }
    
    @JsonCreator
    public ChatMessage(@JsonProperty("id") final String id,
                       @JsonProperty("from") final String from,
                       @JsonProperty("destinationType") final DestinationType destinationType,
                       @JsonProperty("destinationId") final String destinationId,
                       @JsonProperty("content") final String content,
                       @JsonProperty("mimeType") final MimeType mimeType,
                       @JsonProperty("date") final String date) {
        this.objectId = (id != null && !id.trim().isEmpty())
            ? new ObjectId(id)
            : null;
        this.from = from;
        this.destinationType = destinationType;
        this.destinationId = destinationId;
        this.content = content;
        this.mimeType = mimeType;
        this.date = date;
    }
    
    @JsonInclude
    public String getId() {
        return objectId.toString();
    }
    
    @BsonIgnore
    public ChatMessageBuilder from() {
        return ChatMessage.builder()
            .objectId(objectId)
            .from(from)
            .destinationId(destinationId)
            .destinationType(destinationType)
            .content(content)
            .date(date);
    }
    
    public enum MimeType {
        TEXT
    }
    
    public enum DestinationType {
        USER,
        GROUP,
        ALL_USERS_GROUP
    }
    
}

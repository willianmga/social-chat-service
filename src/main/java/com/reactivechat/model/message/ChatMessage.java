package com.reactivechat.model.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.codecs.pojo.annotations.BsonProperty;

@Getter
@Builder
@ToString
public class ChatMessage {
    
    @BsonId
    private final String id;
    private final String from;
    private final DestinationType destinationType;
    private final String destinationId;
    private final String content;
    private final MimeType mimeType;
    private final String date;
    
    @JsonCreator
    @BsonCreator
    public ChatMessage(@BsonProperty("id") @JsonProperty("id") final String id,
                       @BsonProperty("from") @JsonProperty("from") final String from,
                       @BsonProperty("destinationType") @JsonProperty("destinationType") final DestinationType destinationType,
                       @BsonProperty("destinationId") @JsonProperty("destinationId") final String destinationId,
                       @BsonProperty("content") @JsonProperty("content") final String content,
                       @BsonProperty("mimeType") @JsonProperty("mimeType") final MimeType mimeType,
                       @BsonProperty("date") @JsonProperty("date") final String date) {
        this.id = id;
        this.from = from;
        this.destinationType = destinationType;
        this.destinationId = destinationId;
        this.content = content;
        this.mimeType = mimeType;
        this.date = date;
    }
    
    @BsonIgnore
    public ChatMessageBuilder from() {
        return ChatMessage.builder()
            .id(id)
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

package com.reactivechat.model.message;

import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
@AllArgsConstructor
public class ChatMessage {
    
    private final String id;
    private final String from;
    private final DestinationType destinationType;
    private final String destinationId;
    private final String content;
    private final MimeType mimeType;
    private final OffsetDateTime date;
    
    public enum DestinationType {
        USER,
        GROUP,
        ALL_USERS_GROUP
    }
    
    public ChatMessageBuilder from() {
        return ChatMessage.builder()
            .id(id)
            .from(from)
            .destinationId(destinationId)
            .destinationType(destinationType)
            .content(content)
            .date(date);
    }
    
    @Getter
    public enum MimeType {
        TEXT("text/plain");
    
        private final String type;
    
        MimeType(final String type) {
            this.type = type;
        }
    
        @Override
        public String toString() {
            return type;
        }
    }
    
}

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
    private final OffsetDateTime date;
    
    public enum DestinationType {
        USER,
        GROUP,
        ALL_USERS_GROUP
    }
    
}

package com.reactivechat.model;

import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
@AllArgsConstructor
public class Message {
    
    private final String id;
    private final String from;
    private final Destination destination;
    private final String message;
    private final OffsetDateTime date;

    public static MessageBuilder newBuilder() {
        
        return Message.builder()
            .id(UUID.randomUUID().toString());
        
    }
    
}

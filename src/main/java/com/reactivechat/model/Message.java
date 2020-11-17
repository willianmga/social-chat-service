package com.reactivechat.model;

import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
@AllArgsConstructor
public class Message<T> {
    
    private final String id;
    private final String from;
    private final Destination destination;
    private final MessageContent<T> payload;
    private final OffsetDateTime date;

}

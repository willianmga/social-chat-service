package com.reactivechat.message.message;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class ChatHistoryRequest {
    
    private final String destinationId;
    private final String lastMessageId;
    
}

package com.reactivechat.message.message;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder
@EqualsAndHashCode
@AllArgsConstructor
public class ChatHistoryResponse {
    
    private final String destinationId;
    private final List<ChatMessage> chatHistory;
    
}

package com.reactivechat.model.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
@AllArgsConstructor
public class ResponseMessage<T> implements Message {

    private final MessageType type;
    private final T payload;
    
}
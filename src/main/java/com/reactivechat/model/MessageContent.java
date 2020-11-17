package com.reactivechat.model;

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
public class MessageContent<T> {
    
    private final MessageType type;
    private final T content;
    
    public enum MessageType {
        CONTACTS_LIST,
        USER_MESSAGE,
        SERVER
    }
    
}
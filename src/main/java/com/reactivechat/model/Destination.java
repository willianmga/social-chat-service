package com.reactivechat.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class Destination {
    
    private final DestinationType destinationType;
    private final String destinationId;
    
    public enum DestinationType {
        USER,
        GROUP
    }
    
}
package com.reactivechat.message.message;

import com.reactivechat.exception.ResponseStatus;
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
public class ErrorMessage {
    
    private final String message;
    private final ResponseStatus status;
    
}

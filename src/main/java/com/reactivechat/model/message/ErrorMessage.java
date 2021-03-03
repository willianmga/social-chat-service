package com.reactivechat.model.message;

import com.reactivechat.exception.ErrorType;
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
    private final ErrorType errorType;
    
}

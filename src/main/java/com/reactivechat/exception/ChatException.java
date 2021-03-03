package com.reactivechat.exception;

import com.reactivechat.model.message.ErrorMessage;
import lombok.Getter;

@Getter
public class ChatException extends RuntimeException {
    
    private final ErrorType errorType;
    
    public ChatException(String message) {
        this(message, ErrorType.SERVER_ERROR);
    }
    
    public ChatException(String message, ErrorType errorType) {
        super(message);
        this.errorType = errorType;
    }
    
    public ErrorMessage toErrorMessage() {
        return ErrorMessage
            .builder()
            .errorType(this.errorType)
            .message(this.getMessage())
            .build();
    }
    
}

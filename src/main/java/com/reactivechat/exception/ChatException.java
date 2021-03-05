package com.reactivechat.exception;

import com.reactivechat.model.message.ErrorMessage;
import lombok.Getter;

@Getter
public class ChatException extends RuntimeException {
    
    private final ResponseStatus responseStatus;
    
    public ChatException(String message) {
        this(message, ResponseStatus.SERVER_ERROR);
    }
    
    public ChatException(String message, ResponseStatus responseStatus) {
        super(message);
        this.responseStatus = responseStatus;
    }
    
    public ErrorMessage toErrorMessage() {
        return ErrorMessage
            .builder()
            .status(this.responseStatus)
            .message(this.getMessage())
            .build();
    }
    
}

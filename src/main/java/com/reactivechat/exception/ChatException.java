package com.reactivechat.exception;

import com.reactivechat.message.message.ErrorMessage;
import lombok.Getter;

import static com.reactivechat.exception.ResponseStatus.SERVER_ERROR;

@Getter
public class ChatException extends RuntimeException {
    
    private static final String SERVER_ERROR_MESSAGE = "A server error happened";
    
    private final ResponseStatus responseStatus;
    
    public ChatException(final String message) {
        this(message, SERVER_ERROR);
    }
    
    public ChatException(final String message, final ResponseStatus responseStatus) {
        super(message);
        this.responseStatus = responseStatus;
    }
    
    public ErrorMessage toErrorMessage() {
        
        final String errorMessage = (SERVER_ERROR.equals(responseStatus))
            ? SERVER_ERROR_MESSAGE
            : getMessage();
        
        return ErrorMessage
            .builder()
            .status(responseStatus)
            .message(errorMessage)
            .build();
    }
    
}

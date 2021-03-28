package live.socialchat.chat.exception;

import lombok.Getter;

import static live.socialchat.chat.exception.ResponseStatus.SERVER_ERROR;

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

}

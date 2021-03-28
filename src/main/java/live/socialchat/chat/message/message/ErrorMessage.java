package live.socialchat.chat.message.message;

import live.socialchat.chat.exception.ResponseStatus;
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

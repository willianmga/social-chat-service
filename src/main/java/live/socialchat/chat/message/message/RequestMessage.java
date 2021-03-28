package live.socialchat.chat.message.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
@AllArgsConstructor
public class RequestMessage<T> implements Message {
    
    private final Integer seqId;
    private final MessageType type;
    private final T payload;
    
}

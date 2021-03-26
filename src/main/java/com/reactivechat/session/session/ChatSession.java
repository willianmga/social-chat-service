package com.reactivechat.session.session;

import java.util.Objects;
import javax.websocket.Session;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
@AllArgsConstructor
public class ChatSession {
    
    private final String id;
    private final String connectionId;
    private final ServerDetails serverDetails;
    private final UserAuthenticationDetails userAuthenticationDetails;
    private final Session webSocketSession;

    public boolean isOpen() {
        return webSocketSession != null &&
            webSocketSession.isOpen();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChatSession that = (ChatSession) o;
        return id.equals(that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}

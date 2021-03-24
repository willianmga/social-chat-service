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
    private final Status status;
    private final Session webSocketSession;
    
    public static ChatSession fromSession(final Session session) {
        return ChatSession.builder()
            .connectionId(session.getId())
            .webSocketSession(session)
            .status(Status.NOT_AUTHENTICATED)
            .build();
    }
    
    public boolean isAuthenticated() {
        return Status.AUTHENTICATED.equals(status) &&
            userAuthenticationDetails != null &&
            userAuthenticationDetails.getUserId() != null &&
            !userAuthenticationDetails.getUserId().isEmpty();
    }
    
    public boolean isOpen() {
        return webSocketSession != null &&
            webSocketSession.isOpen();
    }
    
    public ChatSessionBuilder from() {
        return ChatSession.builder()
            .id(id)
            .connectionId(connectionId)
            .userAuthenticationDetails(userAuthenticationDetails)
            .serverDetails(serverDetails)
            .webSocketSession(webSocketSession)
            .status(status);
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
    
    public enum Status {
        NOT_AUTHENTICATED, AUTHENTICATED, LOGGED_OFF
    }

}

package com.reactivechat.model.session;

import java.util.Objects;
import javax.websocket.Session;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.codecs.pojo.annotations.BsonProperty;

@Getter
@Builder
@ToString
@AllArgsConstructor
public class ChatSession {
    
    @BsonId
    private final String id;
    private final String connectionId;
    private final ServerDetails serverDetails;
    private final UserDeviceDetails userDeviceDetails;
    private final UserAuthenticationDetails userAuthenticationDetails;
    private final String startDate;
    private final Status status;
    private final Type type;
    
    @BsonIgnore
    private final Session webSocketSession;
    
    @BsonCreator
    public ChatSession(@BsonProperty("id") String id,
                       @BsonProperty("connectionId") String connectionId,
                       @BsonProperty("serverDetails") ServerDetails serverDetails,
                       @BsonProperty("userDeviceDetails") UserDeviceDetails userDeviceDetails,
                       @BsonProperty("userAuthenticationDetails") UserAuthenticationDetails userAuthenticationDetails,
                       @BsonProperty("startDate") String startDate,
                       @BsonProperty("status") Status status,
                       @BsonProperty("type") Type type) {
        this.id = id;
        this.connectionId = connectionId;
        this.serverDetails = serverDetails;
        this.userDeviceDetails = userDeviceDetails;
        this.userAuthenticationDetails = userAuthenticationDetails;
        this.startDate = startDate;
        this.status = status;
        this.type = type;
        this.webSocketSession = null;
    }
    
    public static ChatSession fromSession(final Session session) {
        return ChatSession.builder()
            .connectionId(session.getId())
            .webSocketSession(session)
            .status(Status.NOT_AUTHENTICATED)
            .build();
    }
    
    @BsonIgnore
    public boolean isAuthenticated() {
        return Status.AUTHENTICATED.equals(status) &&
            userAuthenticationDetails != null &&
            userAuthenticationDetails.getUserId() != null &&
            !userAuthenticationDetails.getUserId().isEmpty();
    }
    
    @BsonIgnore
    public boolean isOpen() {
        return webSocketSession != null &&
            webSocketSession.isOpen();
    }
    
    @BsonIgnore
    public ChatSessionBuilder from() {
        return ChatSession.builder()
            .id(id)
            .connectionId(connectionId)
            .userDeviceDetails(userDeviceDetails)
            .userAuthenticationDetails(userAuthenticationDetails)
            .serverDetails(serverDetails)
            .webSocketSession(webSocketSession)
            .startDate(startDate)
            .status(status)
            .type(type);
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
    
    public enum Type {
        AUTHENTICATE, REAUTHENTICATE
    }
    
}

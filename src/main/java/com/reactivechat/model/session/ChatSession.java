package com.reactivechat.model.session;

import java.time.OffsetDateTime;
import java.util.Objects;
import javax.websocket.Session;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonIgnore;

@Getter
@Builder
@ToString
@AllArgsConstructor
public class ChatSession {
    
    @BsonId
    private final String id;
    private final String connectionId;
    private final ServerDetails serverDetails;
    private final UserDeviceDetails userConnectionDetails;
    private final UserAuthenticationDetails userAuthenticationDetails;
    private final OffsetDateTime startDate;
    
    @BsonIgnore
    private final Session webSocketSession;
    
    public boolean isAuthenticated() {
        return userAuthenticationDetails != null &&
            userAuthenticationDetails.getUserId() != null &&
            !userAuthenticationDetails.getUserId().isEmpty();
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

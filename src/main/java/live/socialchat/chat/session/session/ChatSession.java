package live.socialchat.chat.session.session;

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
    
    @BsonIgnore
    private final Session webSocketSession;
    
    private final String connectionId;
    private final ServerDetails serverDetails;
    private final UserAuthenticationDetails userAuthenticationDetails;

    // TODO: add ConnectionType LOCAL and REMOTE in order to destinguish between sessions connected on this
    // server instance or in another
    
    @BsonCreator
    public ChatSession(@BsonProperty("id") String id,
                       @BsonProperty("connectionId") String connectionId,
                       @BsonProperty("serverDetails") ServerDetails serverDetails,
                       @BsonProperty("userAuthenticationDetails") UserAuthenticationDetails userAuthenticationDetails) {
        this.id = id;
        this.connectionId = connectionId;
        this.serverDetails = serverDetails;
        this.userAuthenticationDetails = userAuthenticationDetails;
        this.webSocketSession = null;
    }
    
    @BsonIgnore
    public String getSessionId() {
        
        if (webSocketSession != null) {
            return userAuthenticationDetails.getUserId() + "-" +
                id + "-" +
                webSocketSession.getId();
        }
        
        throw new IllegalStateException("Session ID could not be built");
    }
    
    @BsonIgnore
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

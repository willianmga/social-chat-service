package com.reactivechat.model.session;

import java.util.Objects;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

@Builder
@Getter
@ToString
public class ServerDetails {
    
    private final String serverInstanceId;
    
    @BsonCreator
    public ServerDetails(@BsonProperty("serverInstanceId") String serverInstanceId) {
        this.serverInstanceId = serverInstanceId;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ServerDetails that = (ServerDetails) o;
        return serverInstanceId.equals(that.serverInstanceId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(serverInstanceId);
    }
    
}

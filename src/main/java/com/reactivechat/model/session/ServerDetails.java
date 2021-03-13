package com.reactivechat.model.session;

import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
@AllArgsConstructor
public class ServerDetails {
    
    private final String serverInstanceId;
    
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

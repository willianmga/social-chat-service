package com.reactivechat.session.session;

import java.util.Objects;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

@Getter
@Builder
@ToString
public class UserAuthenticationDetails {
    
    private final String userId;
    private final String token;
    
    @BsonCreator
    public UserAuthenticationDetails(@BsonProperty("userId") final String userId,
                                     @BsonProperty("token") final String token) {
        this.userId = userId;
        this.token = token;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserAuthenticationDetails that = (UserAuthenticationDetails) o;
        return userId.equals(that.userId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }
    
}

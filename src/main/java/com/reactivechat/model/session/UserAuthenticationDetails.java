package com.reactivechat.model.session;

import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
@AllArgsConstructor
public class UserAuthenticationDetails {
    
    private final String userId;
    private final String token;
    
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

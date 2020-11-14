package com.reactivechat.model;

import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
@AllArgsConstructor
public class User {

    private final String id;
    private final String username;
    private final String name;
    private final String avatar;
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        return id.equals(user.id) &&
            username.equals(user.username);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, username);
    }
    
}
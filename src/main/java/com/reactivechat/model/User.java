package com.reactivechat.model;

import java.util.Objects;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;

@Getter
@Builder
@ToString
public class User implements Contact {

    @BsonId
    private final String id;
    private final String username;
    private final String password;
    private final String name;
    private final String avatar;
    private final String description;
    private final ContactType contactType;
    
    @BsonCreator
    public User(@BsonProperty("id") String id,
                @BsonProperty("username") String username,
                @BsonProperty("password") String password,
                @BsonProperty("name") String name,
                @BsonProperty("avatar") String avatar,
                @BsonProperty("description") String description,
                @BsonProperty("contactType") ContactType contactType) {
        
        this.id = id;
        this.username = username;
        this.password = password;
        this.name = name;
        this.avatar = avatar;
        this.description = description;
        this.contactType = contactType;
    }
    
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
package com.reactivechat.model.contacs;

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
public class Group implements Contact {

    @BsonId
    private final String id;
    private final String name;
    private final String avatar;
    private final String description;
    private final ContactType contactType;
    
    @BsonCreator
    public Group(@BsonProperty("id") String id,
                 @BsonProperty("name") String name,
                 @BsonProperty("avatar") String avatar,
                 @BsonProperty("description") String description,
                 @BsonProperty("contactType") ContactType contactType) {
        
        this.id = id;
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
        Group user = (Group) o;
        return id.equals(user.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
}
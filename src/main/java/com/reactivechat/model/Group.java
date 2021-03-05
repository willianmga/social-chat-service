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
public class Group implements Contact {

    private final String id;
    private final String name;
    private final String avatar;
    private final String description;
    private final ContactType contactType;
    
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
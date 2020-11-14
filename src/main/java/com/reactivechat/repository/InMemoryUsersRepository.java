package com.reactivechat.repository;

import com.reactivechat.model.User;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class InMemoryUsersRepository implements UsersRepository {

    private final Map<String, User> idToUsersMap;
    
    public InMemoryUsersRepository() {
        this.idToUsersMap = new HashMap<>();
    }
    
    public User create(final User user) {
    
        if (idToUsersMap.containsKey(user.getUsername())) {
            throw new IllegalArgumentException("username " + user.getUsername() + " already taken");
        }
        
        final User newUser = User.builder()
            .id(UUID.randomUUID().toString())
            .username(user.getUsername())
            .name(user.getName())
            .avatar(user.getAvatar())
            .build();
    
        idToUsersMap.put(newUser.getUsername(), newUser);

        return newUser;
    }
    
    public User findById(final String id) {
    
        final User user = idToUsersMap.get(id);
        
        if (user == null) {
            throw new IllegalArgumentException("User " + id + " is not registered");
        }
    
        return user;
    }
    
}
package com.reactivechat.repository;

import com.reactivechat.model.User;
import com.reactivechat.model.Users;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;
import org.springframework.stereotype.Component;

@Component
public class InMemoryUsersRepository implements UsersRepository {

    private final Map<String, User> idToUsersMap;
    
    public InMemoryUsersRepository() {
        this.idToUsersMap = new HashMap<>();
    
        // TODO: temporary till user creation is finished
        Stream.of(Users.values())
            .forEach(user -> {
                idToUsersMap.put(user.getId(), user.getUser());
            });

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
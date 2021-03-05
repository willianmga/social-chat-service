package com.reactivechat.repository;

import com.reactivechat.exception.ChatException;
import com.reactivechat.exception.ResponseStatus;
import com.reactivechat.model.Contact.ContactType;
import com.reactivechat.model.User;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class InMemoryUsersRepository implements UsersRepository {

    private final Map<String, User> idToUsersMap;
    
    public InMemoryUsersRepository() {
        this.idToUsersMap = new HashMap<>();
    }
    
    public User create(final User user) {
    
        if (idToUsersMap.containsKey(user.getUsername())) {
            throw new ChatException("username already taken", ResponseStatus.USERNAME_IN_USE);
        }
        
        final User newUser = User.builder()
            .id(UUID.randomUUID().toString())
            .username(user.getUsername())
            .name(user.getName())
            .avatar(user.getAvatar())
            .description(user.getDescription())
            .contactType(ContactType.USER)
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
    
    @Override
    public Optional<User> findByUsername(String username) {
        return idToUsersMap
            .values()
            .stream()
            .filter(user -> user.getUsername().equals(username))
            .findFirst();
    }
    
    @Override
    public List<User> findContacts(User user) {
        
        return idToUsersMap
            .values()
            .stream()
            .filter(usr -> !usr.equals(user))
            .collect(Collectors.toList());
        
    }
    
}
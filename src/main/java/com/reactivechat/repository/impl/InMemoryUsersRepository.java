package com.reactivechat.repository.impl;

import com.reactivechat.exception.ChatException;
import com.reactivechat.exception.ResponseStatus;
import com.reactivechat.model.Contact.ContactType;
import com.reactivechat.model.User;
import com.reactivechat.repository.LegacyUsersRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class InMemoryUsersRepository implements LegacyUsersRepository {

    private final Map<String, User> idToUsersMap;
    
    public InMemoryUsersRepository() {
        this.idToUsersMap = new HashMap<>();
    }
    
    @Override
    public User create(final User user) {
    
        if (findFullDetailsByUsername(user.getUsername()).isPresent()) {
            throw new ChatException("username already taken", ResponseStatus.USERNAME_IN_USE);
        }
        
        final User newUser = User.builder()
            .id(UUID.randomUUID().toString())
            .username(user.getUsername())
            .password(user.getPassword())
            .name(user.getName())
            .avatar(user.getAvatar())
            .description(user.getDescription())
            .contactType(ContactType.USER)
            .build();
    
        idToUsersMap.put(newUser.getId(), newUser);

        return newUser;
    }
    
    @Override
    public User findById(final String id) {
    
        final User user = idToUsersMap.get(id);
        
        if (user == null) {
            throw new IllegalArgumentException("User " + id + " is not registered");
        }
    
        return mapToNonSensitiveDataUser(user);
    }
    
    @Override
    public Optional<User> findFullDetailsByUsername(final String username) {
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
            .map(this::mapToNonSensitiveDataUser)
            .collect(Collectors.toList());
    }
    
    @Override
    public User mapToNonSensitiveDataUser(final User user) {
        return User.builder()
            .id(user.getId())
            .name(user.getName())
            .description(user.getDescription())
            .avatar(user.getAvatar())
            .contactType(user.getContactType())
            .build();
    }
    
}
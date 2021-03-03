package com.reactivechat.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reactivechat.model.User;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class InMemoryUsersRepository implements UsersRepository {

    private final Map<String, User> idToUsersMap;
    
    public InMemoryUsersRepository() {
    
        // TODO: temporary till user creation is finished
        this.idToUsersMap = readDummyUsers()
            .stream()
            .collect(Collectors.toMap(
                User::getId,
                user -> user,
                (a, b) -> a,
                ConcurrentHashMap::new
            ));
        
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
    
    private static List<User> readDummyUsers() {
        
        try {
            
            URL resource = InMemoryUsersRepository.class.getClassLoader().getResource("dummy-users.json");
            TypeReference<List<User>> typeReference = new TypeReference<List<User>>() {};
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(resource, typeReference);
            
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read dummy users for server");
        }
        
    }
    
}
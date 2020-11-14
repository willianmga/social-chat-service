package com.reactivechat.repository;

import com.reactivechat.exception.ChatException;
import com.reactivechat.model.User;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.websocket.Session;
import org.springframework.stereotype.Component;

@Component
public class InMemorySessionsRepository implements SessionsRepository {
    
    private final Map<String, User> sessionIdToUserMap;
    private final Map<User, List<Session>> userToSessionsMap;
    
    public InMemorySessionsRepository() {
        this.userToSessionsMap = new HashMap<>();
        this.sessionIdToUserMap = new HashMap<>();
    }
    
    public void create(final User user, final Session session) {
    
        final List<Session> userSessions = userToSessionsMap.getOrDefault(user, new ArrayList<>());
        userSessions.add(session);
        
        userToSessionsMap.put(user, userSessions);
        sessionIdToUserMap.put(session.getId(), user);

    }
    
    @Override
    public void delete(final User user, Session session) {
        
        sessionIdToUserMap.remove(session.getId());
    
        final List<Session> remainingSessions = userToSessionsMap.getOrDefault(user, new ArrayList<>())
            .stream()
            .filter(loggedSession -> !loggedSession.getId().equals(session.getId()))
            .collect(Collectors.toList());
    
        if (!remainingSessions.isEmpty()) {
            userToSessionsMap.put(user, remainingSessions);
        } else {
            userToSessionsMap.remove(user);
        }
        
    }
    
    public List<Session> findByUser(final User user) {
    
        final List<Session> sessions = userToSessionsMap.get(user);
        
        if (sessions == null || sessions.isEmpty()) {
            throw new ChatException("User is not connected to server");
        }
        
        return sessions;
    
    }
    
    public User findBySession(final Session session) {
    
        final User user = sessionIdToUserMap.get(session.getId());
    
        if (user == null) {
            throw new ChatException("no user is connected on this session");
        }
        
        return user;
    }
 
}
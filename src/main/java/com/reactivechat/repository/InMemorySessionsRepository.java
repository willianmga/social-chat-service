package com.reactivechat.repository;

import com.reactivechat.exception.ChatException;
import com.reactivechat.model.User;
import java.util.ArrayList;
import java.util.Collection;
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
    private final Map<String, String> authenticatedSessionsMap;
    
    public InMemorySessionsRepository() {
        this.userToSessionsMap = new HashMap<>();
        this.sessionIdToUserMap = new HashMap<>();
        this.authenticatedSessionsMap = new HashMap<>();
    }
    
    public void create(final User user, final Session session) {
    
        final List<Session> userSessions = userToSessionsMap.getOrDefault(user, new ArrayList<>());
        userSessions.add(session);
        
        userToSessionsMap.put(user, userSessions);
        sessionIdToUserMap.put(session.getId(), user);

    }
    
    @Override
    public void authenticate(Session session, String token) {
    
        if (authenticatedSessionsMap.get(token) == null) {
            authenticatedSessionsMap.put(token, session.getId());
        }
        
        throw new ChatException("Failed to authenticate session: Token is already in use by another session");
    }
    
    @Override
    public boolean sessionIsAuthenticated(final Session session,
                                          final String token) {
    
        final String sessionId = authenticatedSessionsMap.get(token);
        
        if (sessionId != null && !sessionId.isEmpty()) {
            return  sessionId.equals(session.getId());
        }
        
        return false;
    }
    
    
    @Override
    public void delete(final User user, Session session) {
        
        sessionIdToUserMap.remove(session.getId());
        
        authenticatedSessionsMap
            .entrySet()
            .stream()
            .filter(entry -> entry.getValue().equals(session.getId()))
            .findFirst()
            .ifPresent(entry -> {
                authenticatedSessionsMap.remove(entry.getKey());
            });
        
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
    
    @Override
    public List<Session> findByUser(final User user) {
    
        final List<Session> sessions = userToSessionsMap.get(user);
        
        if (sessions == null || sessions.isEmpty()) {
            throw new ChatException("User is not connected to server");
        }
        
        return sessions;
    
    }
    
    @Override
    public List<Session> findAll() {
    
        return userToSessionsMap
            .values()
            .stream()
            .flatMap(Collection::stream)
            .collect(Collectors.toList());

    }
    
    public User findBySession(final Session session) {
    
        final User user = sessionIdToUserMap.get(session.getId());
    
        if (user == null) {
            throw new ChatException("no user is connected on this session");
        }
        
        return user;
    }
 
}
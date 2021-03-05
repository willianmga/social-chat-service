package com.reactivechat.repository;

import com.reactivechat.exception.ChatException;
import com.reactivechat.model.User;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import javax.websocket.Session;
import org.springframework.stereotype.Component;

@Component
public class InMemorySessionsRepository implements SessionsRepository {
    
    private final Map<String, User> sessionIdToUserMap;
    private final Map<User, List<Session>> userToSessionsMap;
    private final Map<String, List<String>> authenticatedSessionsMap;
    
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
    public void authenticate(final Session session, final String token) {
    
        if (authenticatedSessionsMap.get(token) != null) {
            throw new ChatException("Failed to authenticate session: Token is already in use by another session");
        }
    
        authenticatedSessionsMap.put(token, Collections.singletonList(session.getId()));
    }
    
    @Override
    public User reauthenticate(final Session session, final String token) {
    
        final List<String> sessionIds = authenticatedSessionsMap.get(token);
        
        if (sessionIds != null && !sessionIds.isEmpty()) {
    
            final String firstSession = sessionIds.get(0);
            final User user = findBySessionId(firstSession);
            
            if (!sessionIds.contains(session.getId())) {
                create(user, session);
                final List<String> newSessions = new ArrayList<>(sessionIds);
                newSessions.add(session.getId());
                authenticatedSessionsMap.put(token, newSessions);
            }
            
            return user;
        }
    
        throw new ChatException("Failed to reauthenticate with token: Token isn't assigned to any session");
    }
    
    @Override
    public boolean sessionIsAuthenticated(final Session session, final String token) {
    
        final List<String> sessionIds = authenticatedSessionsMap.get(token);
        
        if (sessionIds != null && !sessionIds.isEmpty()) {
            return sessionIds.contains(session.getId());
        }
        
        return false;
    }
    
    @Override
    public void delete(final Session session) {
        
        sessionIdToUserMap.remove(session.getId());
        
        authenticatedSessionsMap
            .entrySet()
            .stream()
            .filter(entry -> entry.getValue().contains(session.getId()))
            .findFirst()
            .ifPresent(entry -> {
                authenticatedSessionsMap.remove(entry.getKey());
            });
    
        final List<Entry<User, List<Session>>> userToSessionEntries = userToSessionsMap
            .entrySet()
            .stream()
            .filter(entry -> entry.getValue().contains(session))
            .collect(Collectors.toList());
    
        userToSessionEntries
            .forEach(entry -> {
    
                List<Session> sessions = userToSessionsMap.get(entry.getKey());
                sessions.remove(session);
                
                if (sessions.isEmpty()) {
                    userToSessionsMap.remove(entry.getKey());
                } else {
                    userToSessionsMap.put(entry.getKey(), sessions);
                }
                
            });
        
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
    
    public User findBySessionId(final String sessionId) {
    
        final User user = sessionIdToUserMap.get(sessionId);
    
        if (user == null) {
            throw new ChatException("no user is connected on this session");
        }
        
        return user;
    }
 
}
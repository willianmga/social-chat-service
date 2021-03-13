package com.reactivechat.repository;

import com.reactivechat.model.User;
import java.util.List;
import javax.websocket.Session;

@Deprecated
public interface LegacySessionsRepository {
    
    void create(final User user, final Session session);
    void authenticate(final Session session, final String token);
    User reauthenticate(final Session session, final String token);
    boolean sessionIsAuthenticated(final Session session, final String token);
    void delete(final Session session);
    List<Session> findByUser(final String userId);
    List<Session> findAll();
    User findBySessionId(final String sessionId);
    
}

package com.reactivechat.repository;

import com.reactivechat.model.User;
import java.util.List;
import javax.websocket.Session;

public interface SessionsRepository {
    
    void create(final User user, final Session session);
    void authenticate(Session session, String token);
    boolean sessionIsAuthenticated(final Session session, final String token);
    void delete(final User user, final Session session);
    List<Session> findByUser(final User user);
    List<Session> findAll();
    User findBySession(final Session session);
    
}

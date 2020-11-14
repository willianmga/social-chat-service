package com.reactivechat.repository;

import com.reactivechat.model.User;
import java.util.List;
import javax.websocket.Session;

public interface SessionsRepository {
    
    void create(final User user, final Session session);
    void delete(final User user, final Session session);
    List<Session> findByUser(final User user);
    User findBySession(final Session session);
    
}

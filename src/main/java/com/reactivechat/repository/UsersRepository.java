package com.reactivechat.repository;

import com.reactivechat.model.User;
import java.util.List;
import java.util.Optional;

public interface UsersRepository {
    
    User create(final User user);
    User findById(final String id);
    Optional<User> findByUsername(final String username);
    List<User> findContacts(final User user);
    
}

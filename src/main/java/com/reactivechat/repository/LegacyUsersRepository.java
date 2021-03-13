package com.reactivechat.repository;

import com.reactivechat.model.User;
import java.util.List;
import java.util.Optional;

@Deprecated

/**
 * @Deprecated: Use {@link UserRepository} instead
 */
public interface LegacyUsersRepository {
    
    User create(final User user);
    User findById(final String id);
    Optional<User> findFullDetailsByUsername(final String username);
    List<User> findContacts(final User user);
    User mapToNonSensitiveDataUser(User user);
    
}

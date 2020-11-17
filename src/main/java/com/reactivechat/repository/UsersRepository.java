package com.reactivechat.repository;

import com.reactivechat.model.User;
import java.util.List;

public interface UsersRepository {
    
    User create(final User user);
    
    User findById(final String id);
    
    List<User> findContacts(final User user);
    
}

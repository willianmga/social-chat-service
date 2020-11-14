package com.reactivechat.repository;

import com.reactivechat.model.User;

public interface UsersRepository {
    
    User create(final User user);
    User findById(final String id);
    
}

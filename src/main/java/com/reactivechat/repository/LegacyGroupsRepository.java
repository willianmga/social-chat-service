package com.reactivechat.repository;

import com.reactivechat.model.Group;
import com.reactivechat.model.User;
import java.util.List;

@Deprecated

/**
 * @Deprecated: Use {@link GroupRepository} instead
 */
public interface LegacyGroupsRepository {
    
    Group create(final Group group);
    Group findById(final String id);
    List<Group> findGroups(final User user);
    
}

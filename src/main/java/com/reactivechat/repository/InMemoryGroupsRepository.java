package com.reactivechat.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reactivechat.model.Group;
import com.reactivechat.model.User;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class InMemoryGroupsRepository implements GroupsRepository {

    private final Map<String, Group> idToGroupsMap;
    
    public InMemoryGroupsRepository() {
    
        // TODO: temporary till user creation is finished
        this.idToGroupsMap = readDummyGroups()
            .stream()
            .collect(Collectors.toMap(
                Group::getId,
                user -> user,
                (a, b) -> a,
                ConcurrentHashMap::new
            ));
        
    }
    
    public Group create(final Group group) {
    
        if (idToGroupsMap.containsKey(group.getId())) {
            throw new IllegalArgumentException("group " + group.getId() + " already taken");
        }
        
        final Group newGroup = Group.builder()
            .id(UUID.randomUUID().toString())
            .name(group.getName())
            .avatar(group.getAvatar())
            .build();
    
        idToGroupsMap.put(newGroup.getId(), newGroup);

        return newGroup;
    }
    
    public Group findById(final String id) {
    
        final Group group = idToGroupsMap.get(id);
        
        if (group == null) {
            throw new IllegalArgumentException("Group " + id + " is not registered");
        }
    
        return group;
    }
    
    @Override
    public List<Group> findGroups(User user) {
        return new ArrayList<>(idToGroupsMap.values());
    }
    
    private static List<Group> readDummyGroups() {
        
        try {
            
            URL resource = InMemoryGroupsRepository.class.getClassLoader().getResource("dummy-groups.json");
            TypeReference<List<Group>> typeReference = new TypeReference<List<Group>>() {};
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(resource, typeReference);
            
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read dummy groups for server");
        }
        
    }
    
}
package com.reactivechat.controller.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reactivechat.controller.AvatarController;
import com.reactivechat.model.contacs.Avatar;
import java.io.IOException;
import java.net.URL;
import java.security.SecureRandom;
import java.util.List;
import org.springframework.stereotype.Controller;

@Controller
public class AvatarControllerImpl implements AvatarController {
    
    private static final List<Avatar> AVAILABLE_AVATARS = loadAvailableAvatars();
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    
    @Override
    public String pickRandomAvatar() {
        final int avatarIndex = SECURE_RANDOM.nextInt(AVAILABLE_AVATARS.size());
        return AVAILABLE_AVATARS.get(avatarIndex).getAvatar();
    }
    
    private static List<Avatar> loadAvailableAvatars() {
        try {
            URL resource = AvatarControllerImpl.class.getClassLoader().getResource("user-avatars.json");
            TypeReference<List<Avatar>> typeReference = new TypeReference<List<Avatar>>() {};
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(resource, typeReference);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read available avatars");
        }
    }
    
}

package com.reactivechat.websocket.encoder;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PayloadEncoder {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(PayloadEncoder.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    
    public static <T> T decodePayload(Object payload, Class<T> type) {
    
        try {
            
            final String payloadAsJson = OBJECT_MAPPER.writeValueAsString(payload);
            return OBJECT_MAPPER.readValue(payloadAsJson, type);
            
        } catch (Exception e) {
            LOGGER.error("Failed to decode payload to type {}", type.getName());
            throw new RuntimeException(e);
        }
    
    }
    
}

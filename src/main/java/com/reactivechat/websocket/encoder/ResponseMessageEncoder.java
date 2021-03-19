package com.reactivechat.websocket.encoder;

import com.google.gson.Gson;
import com.reactivechat.message.message.ResponseMessage;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

public class ResponseMessageEncoder implements Encoder.Text<ResponseMessage> {

    private static final Gson GSON = new Gson();

    @Override
    public String encode(ResponseMessage message) {
        return GSON.toJson(message);
    }

    @Override
    public void init(EndpointConfig endpointConfig) {
        // Custom initialization logic
    }

    @Override
    public void destroy() {
        // Close resources
    }
    
}

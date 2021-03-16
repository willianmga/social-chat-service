package com.reactivechat.websocket.decoder;

import com.google.gson.Gson;
import com.reactivechat.model.message.RequestMessage;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

public class RequestMessageDecoder implements Decoder.Text<RequestMessage> {

    private static final Gson GSON = new Gson();

    @Override
    public RequestMessage decode(String json) {
        return GSON.fromJson(json, RequestMessage.class);
    }

    @Override
    public boolean willDecode(String json) {
        return (json != null);
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

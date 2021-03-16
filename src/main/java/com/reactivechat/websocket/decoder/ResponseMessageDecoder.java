package com.reactivechat.websocket.decoder;

import com.google.gson.Gson;
import com.reactivechat.model.message.ResponseMessage;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

public class ResponseMessageDecoder implements Decoder.Text<ResponseMessage> {

    private static final Gson GSON = new Gson();

    @Override
    public ResponseMessage decode(String json) {
        return GSON.fromJson(json, ResponseMessage.class);
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

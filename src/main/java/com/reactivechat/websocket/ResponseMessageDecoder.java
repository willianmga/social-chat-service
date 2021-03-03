package com.reactivechat.websocket;

import com.google.gson.Gson;
import com.reactivechat.model.message.ResponseMessage;
import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

public class ResponseMessageDecoder implements Decoder.Text<ResponseMessage> {

    private static final Gson GSON = new Gson();

    @Override
    public ResponseMessage decode(String s) throws DecodeException {
        return GSON.fromJson(s, ResponseMessage.class);
    }

    @Override
    public boolean willDecode(String s) {
        return (s != null);
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

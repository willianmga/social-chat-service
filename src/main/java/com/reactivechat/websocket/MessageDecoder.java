package com.reactivechat.websocket;

import com.google.gson.Gson;
import com.reactivechat.model.message.RequestMessage;
import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

public class MessageDecoder implements Decoder.Text<RequestMessage> {

    private static Gson gson = new Gson();

    @Override
    public RequestMessage decode(String s) throws DecodeException {
        RequestMessage message = gson.fromJson(s, RequestMessage.class);
        return message;
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

package com.reactivechat.websocket;

import com.reactivechat.controller.AuthenticationController;
import com.reactivechat.controller.ChatMessageController;
import com.reactivechat.controller.ClientServerMessageController;
import com.reactivechat.controller.ClientServerMessageControllerImpl;
import com.reactivechat.model.message.AuthenticateRequest;
import com.reactivechat.model.message.ChatMessage;
import com.reactivechat.model.message.MessageType;
import com.reactivechat.model.message.RequestMessage;
import com.reactivechat.model.message.SignupRequest;
import java.util.Arrays;
import java.util.List;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.reactivechat.model.message.MessageType.AUTHENTICATE;
import static com.reactivechat.model.message.MessageType.PING;
import static com.reactivechat.model.message.MessageType.SIGNUP;
import static com.reactivechat.websocket.PayloadEncoder.decodePayload;

@Component
@ServerEndpoint(
    value = "/chat",
    decoders = {RequestMessageDecoder.class, ResponseMessageDecoder.class},
    encoders = {RequestMessageEncoder.class, ResponseMessageEncoder.class}
)
public class ChatEndpoint {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ChatEndpoint.class);
    private static final List<MessageType> WHITELISTED_MESSAGE_TYPES = Arrays.asList(PING, AUTHENTICATE, SIGNUP);
    
    private final AuthenticationController authenticationController;
    private final ChatMessageController chatMessageController;
    private final ClientServerMessageController clientServerMessageController;
    
    @Autowired
    public ChatEndpoint(final AuthenticationController authenticationController,
                        final ChatMessageController chatMessageController,
                        final ClientServerMessageControllerImpl clientServerMessageController) {
        
        this.authenticationController = authenticationController;
        this.chatMessageController = chatMessageController;
        this.clientServerMessageController = clientServerMessageController;
    }
    
    @OnOpen
    public void onOpen(final Session session) {
        clientServerMessageController.handleConnected(session);
        LOGGER.info("Session {} connected", session.getId());
    }

    @OnMessage
    public void onMessage(final Session session, final RequestMessage<?> requestMessage) {
    
        final MessageType messageType = requestMessage.getType();
    
        if (!WHITELISTED_MESSAGE_TYPES.contains(messageType)) {
            if (authenticationController.isAuthenticatedSession(session, requestMessage.getToken())) {
                handleBlackListedMessages(session, requestMessage, messageType);
            } else {
                clientServerMessageController.handleNotAuthenticated(session);
            }
        } else {
            handleWhiteListed(session, requestMessage, messageType);
        }
        
    }

    private void handleBlackListedMessages(final Session session,
                                           final RequestMessage<?> requestMessage,
                                           final MessageType messageType) {
        
        switch (messageType) {
            case USER_MESSAGE:
                chatMessageController
                    .handleChatMessage(session, decodePayload(requestMessage.getPayload(), ChatMessage.class));
                break;
            case CONTACTS_LIST:
                chatMessageController
                    .handleContactsMessage(session);
                break;
            default: LOGGER.error("Unable to handle message of type {}", messageType.name());
        }
        
    }
    
    private void handleWhiteListed(final Session session,
                                   final RequestMessage<?> requestMessage,
                                   final MessageType messageType) {
    
        switch (messageType) {
            case AUTHENTICATE:
                authenticationController
                    .handleAuthenticate(decodePayload(requestMessage.getPayload(), AuthenticateRequest.class), session);
                break;
            case SIGNUP:
                authenticationController
                    .handleSignup(decodePayload(requestMessage.getPayload(), SignupRequest.class), session);
                break;
            case PING:
                clientServerMessageController.handlePing(session);
                break;
            default: LOGGER.error("Unable to handle message of type {}", messageType.name());
        }

    }
    
    @OnClose
    public void onClose(final Session session) {
        authenticationController.logoff(session);
        LOGGER.info("Session {} finished gracefully", session.getId());
    }
    
    @OnError
    public void onError(Session session, Throwable throwable) {
        LOGGER.error("Error occurred during session {}. Reason {}", session.getId(), throwable.getMessage());
        throwable.printStackTrace();
    }

}
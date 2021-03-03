package com.reactivechat.websocket;

import com.reactivechat.controller.AuthenticationController;
import com.reactivechat.controller.ChatMessageController;
import com.reactivechat.controller.ClientServerMessageController;
import com.reactivechat.controller.ClientServerMessageControllerImpl;
import com.reactivechat.model.message.AuthenticateRequest;
import com.reactivechat.model.message.MessageType;
import com.reactivechat.model.message.RequestMessage;
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

@Component
@ServerEndpoint(
    value = "/chat",
    decoders = MessageDecoder.class,
    encoders = MessageEncoder.class
)
public class ChatEndpoint {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ChatEndpoint.class);
    private static final List<MessageType> WHITELISTED_MESSAGE_TYPES = Arrays.asList(PING, AUTHENTICATE);
    
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
    }

    @OnMessage
    public void onMessage(final Session session, final RequestMessage requestMessage) {
    
        final MessageType messageType = requestMessage.getType();
    
        if (!WHITELISTED_MESSAGE_TYPES.contains(messageType)) {
            if (authenticationController.isAuthenticatedSession(session, requestMessage.getToken())) {
                handleBlackListedMessages(session, requestMessage, messageType);
            } else {
                clientServerMessageController.handleNotAuthenticated(session);
            }
        } else {
            handleWhiteListed(session, messageType);
        }
        
    }

    private void handleBlackListedMessages(final Session session,
                                           final RequestMessage requestMessage,
                                           final MessageType messageType) {
        
        switch (messageType) {
            case AUTHENTICATE:
                authenticationController.handleAuthenticate((AuthenticateRequest) requestMessage.getPayload(), session);
                break;
            case USER_MESSAGE:
                chatMessageController.handleChatMessage(session, requestMessage);
                break;
            case CONTACTS_LIST:
                chatMessageController.handleContactsMessage(session);
                break;
            default: LOGGER.error("Unable to handle message of type {}" + messageType.name());
        }
        
    }
    
    private void handleWhiteListed(final Session session, final MessageType messageType) {
        if (messageType == PING) {
            clientServerMessageController.handlePing(session);
        } else {
            LOGGER.error("Unable to handle message of type {}" + messageType.name());
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
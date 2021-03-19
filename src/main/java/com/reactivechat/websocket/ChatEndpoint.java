package com.reactivechat.websocket;

import com.reactivechat.session.AuthenticationController;
import com.reactivechat.message.ChatMessageController;
import com.reactivechat.server.ServerMessageController;
import com.reactivechat.server.ServerMessageControllerImpl;
import com.reactivechat.message.message.AuthenticateRequest;
import com.reactivechat.message.message.ChatHistoryRequest;
import com.reactivechat.message.message.ChatMessage;
import com.reactivechat.message.message.MessageType;
import com.reactivechat.message.message.ReauthenticateRequest;
import com.reactivechat.message.message.RequestMessage;
import com.reactivechat.message.message.SignupRequest;
import com.reactivechat.session.session.ChatSession;
import com.reactivechat.websocket.decoder.RequestMessageDecoder;
import com.reactivechat.websocket.decoder.ResponseMessageDecoder;
import com.reactivechat.websocket.encoder.RequestMessageEncoder;
import com.reactivechat.websocket.encoder.ResponseMessageEncoder;
import java.util.Optional;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import static com.reactivechat.websocket.encoder.PayloadEncoder.decodePayload;

@ServerEndpoint(
    value = "/chat",
    decoders = {RequestMessageDecoder.class, ResponseMessageDecoder.class},
    encoders = {RequestMessageEncoder.class, ResponseMessageEncoder.class}
)
public class ChatEndpoint {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ChatEndpoint.class);
    
    private final AuthenticationController authenticationController;
    private final ChatMessageController chatMessageController;
    private final ServerMessageController serverMessageController;
    
    @Autowired
    public ChatEndpoint(final AuthenticationController authenticationController,
                        final ChatMessageController chatMessageController,
                        final ServerMessageControllerImpl clientServerMessageController) {
        
        this.authenticationController = authenticationController;
        this.chatMessageController = chatMessageController;
        this.serverMessageController = clientServerMessageController;
    }
    
    @OnOpen
    public void onOpen(final Session session) {
        serverMessageController.handleConnected(ChatSession.fromSession(session));
    }

    @OnMessage
    public void onMessage(final Session session, final RequestMessage<?> requestMessage) {
    
        if (!validRequestMessage(requestMessage)) {
            serverMessageController.handleInvalidRequest(ChatSession.fromSession(session));
            return;
        }
    
        final MessageType messageType = requestMessage.getType();
    
        if (!messageType.isWhitelisted() && authenticatedRequestMessage(requestMessage)) {
        
            final Optional<ChatSession> chatSessionOpt = authenticationController
                .restoreSessionByToken(ChatSession.fromSession(session), requestMessage.getToken());
    
            if (chatSessionOpt.isPresent() && chatSessionOpt.get().isAuthenticated()) {
                handleBlackListedMessages(chatSessionOpt.get(), requestMessage, messageType);
            } else {
                serverMessageController.handleNotAuthenticated(ChatSession.fromSession(session));
            }
    
        } else if (messageType.isWhitelisted()) {
            handleWhiteListedMessages(ChatSession.fromSession(session), requestMessage, messageType);
        } else {
            serverMessageController.handleInvalidRequest(ChatSession.fromSession(session));
        }
    
    }
    
    @OnClose
    public void onClose(final Session session) {
        serverMessageController.handleDisconnected(ChatSession.fromSession(session));
    }
    
    @OnError
    public void onError(final Session session, final Throwable throwable) {
        LOGGER.error("Error occurred during connection {}. Reason {}", session.getId(), throwable.getMessage());
    }
    
    private void handleBlackListedMessages(final ChatSession chatSession,
                                           final RequestMessage<?> requestMessage,
                                           final MessageType messageType) {

        switch (messageType) {
            case USER_MESSAGE:
                chatMessageController
                    .handleChatMessage(chatSession, decodePayload(requestMessage.getPayload(), ChatMessage.class));
                break;
            case CHAT_HISTORY:
                chatMessageController
                    .handleChatHistory(chatSession, decodePayload(requestMessage.getPayload(), ChatHistoryRequest.class));
                break;
            case CONTACTS_LIST:
                chatMessageController
                    .handleContactsMessage(chatSession);
                break;
            default:
                LOGGER.error("Unable to handle message of type {}", messageType.name());
        }
        
    }
    
    private void handleWhiteListedMessages(final ChatSession chatSession,
                                           final RequestMessage<?> requestMessage,
                                           final MessageType messageType) {
    
        switch (messageType) {
            case AUTHENTICATE:
                authenticationController
                    .handleAuthenticate(decodePayload(requestMessage.getPayload(), AuthenticateRequest.class), chatSession);
                break;
            case REAUTHENTICATE:
                authenticationController
                    .handleReauthenticate(decodePayload(requestMessage.getPayload(), ReauthenticateRequest.class), chatSession);
                break;
            case SIGNUP:
                authenticationController
                    .handleSignup(decodePayload(requestMessage.getPayload(), SignupRequest.class), chatSession);
                break;
            case PING:
                serverMessageController.handlePing(chatSession);
                break;
            case LOGOFF:
                authenticationController.logoff(chatSession);
                break;
            default: LOGGER.error("Unable to handle message of type {}", messageType.name());
        }

    }

    private boolean validRequestMessage(RequestMessage<?> requestMessage) {
        return requestMessage != null  && requestMessage.getType() != null;
    }
    
    private boolean authenticatedRequestMessage(RequestMessage<?> requestMessage) {
        return requestMessage.getToken() != null && !requestMessage.getToken().trim().isEmpty();
    }

}
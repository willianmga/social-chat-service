package com.reactivechat.websocket;

import com.reactivechat.controller.AuthenticationController;
import com.reactivechat.controller.ChatMessageController;
import com.reactivechat.controller.ClientServerMessageController;
import com.reactivechat.controller.impl.ClientServerMessageControllerImpl;
import com.reactivechat.model.message.AuthenticateRequest;
import com.reactivechat.model.message.ChatMessage;
import com.reactivechat.model.message.MessageType;
import com.reactivechat.model.message.ReauthenticateRequest;
import com.reactivechat.model.message.RequestMessage;
import com.reactivechat.model.message.SignupRequest;
import com.reactivechat.model.session.ChatSession;
import com.reactivechat.repository.SessionRepository;
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

import static com.reactivechat.websocket.PayloadEncoder.decodePayload;

@ServerEndpoint(
    value = "/chat",
    decoders = {RequestMessageDecoder.class, ResponseMessageDecoder.class},
    encoders = {RequestMessageEncoder.class, ResponseMessageEncoder.class}
)
public class ChatEndpoint {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ChatEndpoint.class);
    
    private final AuthenticationController authenticationController;
    private final ChatMessageController chatMessageController;
    private final ClientServerMessageController clientServerMessageController;
    private final SessionRepository sessionRepository;
    
    @Autowired
    public ChatEndpoint(final AuthenticationController authenticationController,
                        final ChatMessageController chatMessageController,
                        final ClientServerMessageControllerImpl clientServerMessageController,
                        final SessionRepository sessionRepository) {
        
        this.authenticationController = authenticationController;
        this.chatMessageController = chatMessageController;
        this.clientServerMessageController = clientServerMessageController;
        this.sessionRepository = sessionRepository;
    }
    
    @OnOpen
    public void onOpen(final Session session) {
        clientServerMessageController.handleConnected(ChatSession.fromSession(session));
        LOGGER.info("Connection opened: {}", session.getId());
    }

    @OnMessage
    public void onMessage(final Session session, final RequestMessage<?> requestMessage) {
    
        if (requestMessage == null || requestMessage.getType() == null) {
            clientServerMessageController.handleInvalidRequest(ChatSession.fromSession(session));
            return;
        }
        
        final MessageType messageType = requestMessage.getType();
    
        if (!messageType.isWhitelisted()) {
        
            final Optional<ChatSession> chatSessionOpt = sessionRepository.findByConnectionId(session.getId())
                .blockOptional();
    
            if (chatSessionOpt.isPresent() && chatSessionOpt.get().isAuthenticated()) {
                handleBlackListedMessages(chatSessionOpt.get(), requestMessage, messageType);
            } else {
                clientServerMessageController.handleNotAuthenticated(ChatSession.fromSession(session));
            }
    
        } else {
            handleWhiteListedMessages(ChatSession.fromSession(session), requestMessage, messageType);
        }
    
    }

    private void handleBlackListedMessages(final ChatSession chatSession,
                                           final RequestMessage<?> requestMessage,
                                           final MessageType messageType) {

        switch (messageType) {
            case USER_MESSAGE:
                chatMessageController
                    .handleChatMessage(chatSession, decodePayload(requestMessage.getPayload(), ChatMessage.class));
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
                clientServerMessageController.handlePing(chatSession);
                break;
            case LOGOFF:
                authenticationController.logoff(chatSession);
                break;
            default: LOGGER.error("Unable to handle message of type {}", messageType.name());
        }

    }
    
    @OnClose
    public void onClose(final Session session) {
        clientServerMessageController.handleDisconnected(ChatSession.fromSession(session));
    }
    
    @OnError
    public void onError(final Session session, final Throwable throwable) {
        LOGGER.error("Error occurred during connection {}. Reason {}", session.getId(), throwable.getMessage());
    }

}
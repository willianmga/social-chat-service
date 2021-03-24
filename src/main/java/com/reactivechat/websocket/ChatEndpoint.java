package com.reactivechat.websocket;

import com.reactivechat.message.ChatMessageController;
import com.reactivechat.message.message.ChatHistoryRequest;
import com.reactivechat.message.message.ChatMessage;
import com.reactivechat.message.message.MessageType;
import com.reactivechat.message.message.RequestMessage;
import com.reactivechat.server.ServerMessageController;
import com.reactivechat.server.ServerMessageControllerImpl;
import com.reactivechat.session.session.ChatSession;
import com.reactivechat.websocket.AccessTokenFilter.LoggedInUser;
import com.reactivechat.websocket.decoder.RequestMessageDecoder;
import com.reactivechat.websocket.decoder.ResponseMessageDecoder;
import com.reactivechat.websocket.encoder.RequestMessageEncoder;
import com.reactivechat.websocket.encoder.ResponseMessageEncoder;
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

import static com.reactivechat.session.session.ChatSession.Status.AUTHENTICATED;
import static com.reactivechat.websocket.encoder.PayloadEncoder.decodePayload;

@ServerEndpoint(
    value = "/chat",
    decoders = {RequestMessageDecoder.class, ResponseMessageDecoder.class},
    encoders = {RequestMessageEncoder.class, ResponseMessageEncoder.class}
)
@Component
public class ChatEndpoint {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ChatEndpoint.class);
    
    private final ChatMessageController chatMessageController;
    private final ServerMessageController serverMessageController;
    
    @Autowired
    public ChatEndpoint(final ChatMessageController chatMessageController,
                        final ServerMessageControllerImpl clientServerMessageController) {
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
    
        handleMessages(restoreSession(session), requestMessage, requestMessage.getType());
    
    }

    @OnClose
    public void onClose(final Session session) {
        serverMessageController.handleDisconnected(ChatSession.fromSession(session));
    }
    
    @OnError
    public void onError(final Session session, final Throwable throwable) {
        LOGGER.error("Error occurred during connection {}. Reason {}", session.getId(), throwable.getMessage());
        throwable.printStackTrace();
    }
    
    private void handleMessages(final ChatSession chatSession,
                                final RequestMessage<?> requestMessage,
                                final MessageType messageType) {

        switch (messageType) {
            case PING:
                serverMessageController.handlePing(chatSession);
                break;
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
    
    public ChatSession restoreSession(final Session session) {
        
        final LoggedInUser userPrincipal = (LoggedInUser) session.getUserPrincipal();
        
        return ChatSession.builder()
            .id(userPrincipal.getSessionId())
            .userAuthenticationDetails(userPrincipal.getUserAuthenticationDetails())
            .webSocketSession(session)
            .connectionId(session.getId())
            .status(AUTHENTICATED)
            .build();
    }
    
    private boolean validRequestMessage(final RequestMessage<?> requestMessage) {
        return requestMessage != null  && requestMessage.getType() != null;
    }

}
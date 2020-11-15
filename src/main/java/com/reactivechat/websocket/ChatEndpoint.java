package com.reactivechat.websocket;

import com.reactivechat.controller.MessageBroadcasterController;
import com.reactivechat.model.Destination;
import com.reactivechat.model.Destination.DestinationType;
import com.reactivechat.model.Message;
import com.reactivechat.model.User;
import com.reactivechat.repository.SessionsRepository;
import com.reactivechat.repository.UsersRepository;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.reactivechat.model.Users.CHAT_SERVER;

@Component
@ServerEndpoint(
    value = "/chat/{userId}",
    decoders = MessageDecoder.class,
    encoders = MessageEncoder.class
)
public class ChatEndpoint {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ChatEndpoint.class);
    
    private final UsersRepository usersRepository;
    private final SessionsRepository sessionsRepository;
    private final MessageBroadcasterController broadcasterController;
    
    @Autowired
    public ChatEndpoint(final UsersRepository usersRepository,
                        final SessionsRepository sessionsRepository,
                        final MessageBroadcasterController broadcasterController) {
    
        this.usersRepository = usersRepository;
        this.sessionsRepository = sessionsRepository;
        this.broadcasterController = broadcasterController;
    }
    
    @OnOpen
    public void onOpen(final Session session, @PathParam("userId") final String userId) {
    
        final User user = usersRepository.findById(userId);
        sessionsRepository.create(user, session);

        final Message message = chatServerMessage(user, "Connected!");
        
        broadcasterController.broadcastToSession(session, message);
    
        LOGGER.info("New session created: {}", session.getId());
    }

    @OnMessage
    public void onMessage(final Session session, final Message message) {
    
        final User user = sessionsRepository.findBySession(session);
    
        final Message newMessage = Message.newBuilder()
            .from(user.getId())
            .destination(Destination.builder()
                .destinationType(message.getDestination().getDestinationType())
                .destinationId(message.getDestination().getDestinationId())
                .build()
            )
            .message(message.getMessage())
            .build();

        broadcasterController.broadcast(newMessage);
    
        LOGGER.info("Messaged received from session {}", session.getId());
    }

    @OnClose
    public void onClose(final Session session) {
        
        final User user = sessionsRepository.findBySession(session);
        final Message message = chatServerMessage(user, "Disconnected!");
    
        broadcasterController.broadcastToSession(session, message);
        sessionsRepository.delete(user, session);
    
        LOGGER.info("Session {} finished gracefully", session.getId());
    }
    
    @OnError
    public void onError(Session session, Throwable throwable) {
        LOGGER.error("Error occurred during session {}. Reason {}", session.getId(), throwable.getMessage());
    }
    
    private Message chatServerMessage(final User destinationUser, final String message) {
        
        return Message.newBuilder()
            .from(CHAT_SERVER.getId())
            .destination(Destination.builder()
                .destinationType(DestinationType.USER)
                .destinationId(destinationUser.getId())
                .build()
            )
            .message(message)
            .build();

    }

}
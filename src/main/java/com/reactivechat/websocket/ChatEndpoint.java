package com.reactivechat.websocket;

import com.reactivechat.controller.MessageBroadcasterController;
import com.reactivechat.model.Contact;
import com.reactivechat.model.Destination;
import com.reactivechat.model.Destination.DestinationType;
import com.reactivechat.model.Group;
import com.reactivechat.model.Message;
import com.reactivechat.model.MessageContent;
import com.reactivechat.model.MessageContent.MessageType;
import com.reactivechat.model.User;
import com.reactivechat.repository.GroupsRepository;
import com.reactivechat.repository.SessionsRepository;
import com.reactivechat.repository.UsersRepository;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
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
    private final GroupsRepository groupsRepository;
    private final SessionsRepository sessionsRepository;
    private final MessageBroadcasterController broadcasterController;
    
    @Autowired
    public ChatEndpoint(final UsersRepository usersRepository,
                        final GroupsRepository groupsRepository,
                        final SessionsRepository sessionsRepository,
                        final MessageBroadcasterController broadcasterController) {
    
        this.usersRepository = usersRepository;
        this.groupsRepository = groupsRepository;
        this.sessionsRepository = sessionsRepository;
        this.broadcasterController = broadcasterController;
    }
    
    @OnOpen
    public void onOpen(final Session session, @PathParam("userId") final String userId) {
    
        final User user = usersRepository.findById(userId);
        sessionsRepository.create(user, session);

        final Message<String> message = chatServerMessage(user, "Connected!");
        broadcasterController.broadcastToSession(session, message);
    
        final List<User> userContacts = usersRepository.findContacts(user);
        final List<Group> groupContacts = groupsRepository.findGroups(user);
        final List<Contact> allContacts = new ArrayList<>(groupContacts);
        allContacts.addAll(userContacts);
        
        final Message<List<Contact>> contactsListMessage = newMessage(CHAT_SERVER.getUser(), user, MessageType.CONTACTS_LIST, allContacts);
        broadcasterController.broadcastToSession(session, contactsListMessage);
    
        LOGGER.info("New session created: {}", session.getId());
    }

    @OnMessage
    public void onMessage(final Session session, final Message<String> message) {
    
        final MessageType messageType = message.getPayload().getType();
    
        switch (messageType) {
            case PING: handlePingMessage(session);
            case USER_MESSAGE: handleUserMessage(session, message);
            default: LOGGER.error("Unable to handle message of type {}" + messageType.name());
        }

    }
    
    private void handleUserMessage(final Session session, final Message<String> message) {
    
        final User user = sessionsRepository.findBySession(session);
    
        if (message.getDestination().getDestinationType() == DestinationType.USER) {
            final User destinationUser = usersRepository.findById(message.getDestination().getDestinationId());
            LOGGER.info("Messaged received from user {} to user {}", user.getUsername(), destinationUser.getUsername());
        } else if (message.getDestination().getDestinationType() == DestinationType.ALL_USERS_GROUP) {
            LOGGER.info("Messaged received from user {} to all users", user.getName());
        }
    
        Message<String> newMessage = new Message<>(
            UUID.randomUUID().toString(),
            user.getId(),
            Destination.builder()
                .destinationType(message.getDestination().getDestinationType())
                .destinationId(message.getDestination().getDestinationId())
                .build(),
            new MessageContent<>(MessageType.USER_MESSAGE, message.getPayload().getContent()),
            OffsetDateTime.now()
        );
    
        broadcasterController.broadcast(session, newMessage);
        
    }
    
    private void handlePingMessage(final Session session) {
    
        Message<String> pongMessage = new Message<>(
            UUID.randomUUID().toString(),
            CHAT_SERVER.getId(),
            Destination.builder()
                .destinationType(DestinationType.CLIENT)
                .build(),
            new MessageContent<>(MessageType.PONG, MessageType.PONG.name()),
            OffsetDateTime.now()
        );
        
        broadcasterController.broadcastToSession(session, pongMessage);
        
    }
    
    @OnClose
    public void onClose(final Session session) {
        
        final User user = sessionsRepository.findBySession(session);
        
        //final Message<String> message = chatServerMessage(user, "Disconnected!");
        //broadcasterController.broadcastToSession(session, message);
        
        sessionsRepository.delete(user, session);
    
        LOGGER.info("Session {} finished gracefully", session.getId());
    }
    
    @OnError
    public void onError(Session session, Throwable throwable) {
        LOGGER.error("Error occurred during session {}. Reason {}", session.getId(), throwable.getMessage());
        throwable.printStackTrace();
    }
    
    private Message<String> chatServerMessage(final User destinationUser, final String message) {
    
        return new Message<>(
            UUID.randomUUID().toString(),
            CHAT_SERVER.getId(),
            Destination.builder()
                .destinationType(DestinationType.USER)
                .destinationId(destinationUser.getId())
                .build(),
            new MessageContent<>(MessageType.SERVER, message),
            OffsetDateTime.now()
        );

    }
    
    private <T> Message<T> newMessage(final User originUser, final User destinationUser, final MessageType messageType, final T message) {
        
        return new Message<T>(
            UUID.randomUUID().toString(),
            originUser.getId(),
            Destination.builder()
                .destinationType(DestinationType.USER)
                .destinationId(destinationUser.getId())
                .build(),
            new MessageContent<T>(messageType, message),
            OffsetDateTime.now()
        );
        
    }

}
package com.reactivechat.controller.impl;

import com.reactivechat.controller.ChatMessageController;
import com.reactivechat.controller.MessageBroadcasterController;
import com.reactivechat.model.Contact;
import com.reactivechat.model.Group;
import com.reactivechat.model.User;
import com.reactivechat.model.message.ChatMessage;
import com.reactivechat.model.message.ChatMessage.DestinationType;
import com.reactivechat.model.message.MessageType;
import com.reactivechat.model.message.ResponseMessage;
import com.reactivechat.repository.GroupRepository;
import com.reactivechat.repository.SessionsRepository;
import com.reactivechat.repository.UserRepository;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.websocket.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import static com.reactivechat.model.message.MessageType.CONTACTS_LIST;
import static com.reactivechat.model.message.MessageType.NEW_CONTACT_REGISTERED;

@Service
public class ChatMessageControllerImpl implements ChatMessageController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ChatMessageControllerImpl.class);
    
    private final UserRepository usersRepository;
    private final GroupRepository groupsRepository;
    private final SessionsRepository sessionsRepository;
    private final MessageBroadcasterController broadcasterController;
    
    public ChatMessageControllerImpl(final UserRepository userRepository,
                                     final GroupRepository groupRepository,
                                     final SessionsRepository sessionsRepository,
                                     final MessageBroadcasterController broadcasterController) {
        
        this.usersRepository = userRepository;
        this.groupsRepository = groupRepository;
        this.sessionsRepository = sessionsRepository;
        this.broadcasterController = broadcasterController;
    }
    
    @Override
    public void handleChatMessage(final Session session,
                                  final ChatMessage receivedMessage) {
        
        final User user = sessionsRepository.findBySessionId(session.getId());

        final ChatMessage chatMessage = ChatMessage.builder()
            .id(UUID.randomUUID().toString())
            .from(user.getId())
            .destinationId(receivedMessage.getDestinationId())
            .destinationType(receivedMessage.getDestinationType())
            .message(receivedMessage.getMessage())
            .date(OffsetDateTime.now())
            .build();
        
        if (chatMessage.getDestinationType() == DestinationType.USER) {
            final User destinationUser = usersRepository.findById(chatMessage.getDestinationId()).block();
            LOGGER.info("Messaged received from user {} to user {}", user.getUsername(), destinationUser.getUsername());
        } else if (chatMessage.getDestinationType() == DestinationType.ALL_USERS_GROUP) {
            LOGGER.info("Messaged received from user {} to all users", user.getName());
        }
    
        ResponseMessage<ChatMessage> responseMessage = new ResponseMessage<>(MessageType.USER_MESSAGE, chatMessage);

        broadcasterController.broadcastChatMessage(session, responseMessage);
        
    }
    
    @Override
    public void handleContactsMessage(final Session session) {
    
        final User user = sessionsRepository.findBySessionId(session.getId());
        
        final Flux<User> userContacts = usersRepository.findContacts(user);
        final Flux<Group> groupContacts = groupsRepository.findGroups(user);
        final List<Contact> allContacts = Flux.concat(userContacts, groupContacts)
            .toStream()
            .collect(Collectors.toList());

        ResponseMessage<Object> responseMessage = ResponseMessage
            .builder()
            .type(CONTACTS_LIST)
            .payload(allContacts)
            .build();
        
        broadcasterController.broadcastToSession(session, responseMessage);
    }
    
    @Override
    public void handleNewContact(final Contact contact, final Session session) {
    
        ResponseMessage<Object> responseMessage = ResponseMessage
            .builder()
            .type(NEW_CONTACT_REGISTERED)
            .payload(Collections.singletonList(contact))
            .build();
    
        broadcasterController.broadcastToAllExceptSession(session, responseMessage);
        
    }
    
}

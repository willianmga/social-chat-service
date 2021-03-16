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
import com.reactivechat.model.session.ChatSession;
import com.reactivechat.repository.GroupRepository;
import com.reactivechat.repository.UserRepository;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import static com.reactivechat.model.message.MessageType.CONTACTS_LIST;
import static com.reactivechat.model.message.MessageType.NEW_CONTACT_REGISTERED;

@Service
public class ChatMessageControllerImpl implements ChatMessageController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ChatMessageControllerImpl.class);
    
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final MessageBroadcasterController broadcasterController;
    
    public ChatMessageControllerImpl(final UserRepository userRepository,
                                     final GroupRepository groupRepository,
                                     final MessageBroadcasterController broadcasterController) {
        
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
        this.broadcasterController = broadcasterController;
    }
    
    @Override
    public void handleChatMessage(final ChatSession chatSession,
                                  final ChatMessage receivedMessage) {
        
        final String userId = chatSession.getUserAuthenticationDetails().getUserId();

        final ChatMessage chatMessage = ChatMessage.builder()
            .id(UUID.randomUUID().toString())
            .from(userId)
            .destinationId(receivedMessage.getDestinationId())
            .destinationType(receivedMessage.getDestinationType())
            .content(receivedMessage.getContent())
            .date(OffsetDateTime.now())
            .build();
        
        if (chatMessage.getDestinationType() == DestinationType.USER) {
            final String destinationUser = chatMessage.getDestinationId();
            LOGGER.info("Messaged sent from user {} to user {}", userId, destinationUser);
        } else if (chatMessage.getDestinationType() == DestinationType.ALL_USERS_GROUP) {
            LOGGER.info("Messaged sent from user {} to all users", userId);
        }
    
        ResponseMessage<ChatMessage> responseMessage = new ResponseMessage<>(MessageType.USER_MESSAGE, chatMessage);

        broadcasterController.broadcastChatMessage(chatSession, responseMessage);
        
    }
    
    @Override
    public void handleContactsMessage(final ChatSession chatSession) {
    
        final String userId = chatSession.getUserAuthenticationDetails().getUserId();
        final Flux<User> userContacts = userRepository.findContacts(userId);
        final Flux<Group> groupContacts = groupRepository.findGroups(userId);
        
        Flux.concat(userContacts, groupContacts)
            .collectList()
            .subscribe(contacts -> {

                ResponseMessage<Object> responseMessage = ResponseMessage
                    .builder()
                    .type(CONTACTS_LIST)
                    .payload(contacts)
                    .build();
    
                broadcasterController.broadcastToSession(chatSession, responseMessage);
                
            });

    }
    
    @Override
    public void handleNewContact(final Contact contact, final ChatSession chatSession) {
    
        ResponseMessage<Object> responseMessage = ResponseMessage
            .builder()
            .type(NEW_CONTACT_REGISTERED)
            .payload(Collections.singletonList(contact))
            .build();
    
        broadcasterController.broadcastToAllExceptSession(chatSession, responseMessage);
        
    }
    
}

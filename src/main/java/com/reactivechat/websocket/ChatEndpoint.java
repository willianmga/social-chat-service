package com.reactivechat.websocket;

import com.reactivechat.controller.MessageBroadcasterController;
import com.reactivechat.model.Destination;
import com.reactivechat.model.Destination.DestinationType;
import com.reactivechat.model.Message;
import com.reactivechat.model.User;
import com.reactivechat.repository.SessionsRepository;
import com.reactivechat.repository.UsersRepository;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler.Whole;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.reactivechat.model.Users.CHAT_SERVER;

@Component
@ServerEndpoint(
    value = "/chat/{userId}",
    decoders = MessageDecoder.class,
    encoders = MessageEncoder.class
)
public class ChatEndpoint { // extends Endpoint
    
    private UsersRepository usersRepository;
    private SessionsRepository sessionsRepository;
    private MessageBroadcasterController broadcasterController;
    
    @Autowired
    public ChatEndpoint(final UsersRepository usersRepository,
                        final SessionsRepository sessionsRepository,
                        final MessageBroadcasterController broadcasterController) {
        
        this.usersRepository = usersRepository;
        this.sessionsRepository = sessionsRepository;
        this.broadcasterController = broadcasterController;
    }
    
/*    public ChatEndpoint() {
        // Needed for jetty initialization
    }*/
    
    @OnOpen
    public void onOpen(final Session session, @PathParam("userId") final String userId) {
    
        final User user = usersRepository.findById(userId);
        sessionsRepository.create(user, session);

        final Message message = chatServerMessage(user, "Connected!");
        
        broadcasterController.broadcastToSession(session, message);
    }
    
/*    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    
        RemoteEndpoint remote = session.getBasicRemote();
        session.addMessageHandler(new Whole<Message>() {
            
            @Override
            public void onMessage(Message message) {
                ChatEndpoint.this.onMessage(session, message);
            }
            
        });
    
    
    }*/

    @OnMessage
    public void onMessage(final Session session, final Message message) {
    
        final User user = sessionsRepository.findBySession(session);
    
        final Message newMessage = Message.newBuilder()
            .from(user.getId())
            .destination(Destination.builder()
                .destinationType(DestinationType.USER)
                .destinationId(user.getId())
                .build()
            )
            .message(message.getMessage())
            .build();

        broadcasterController.broadcast(newMessage);
    }

    @OnClose
    public void onClose(final Session session) {
        
        final User user = sessionsRepository.findBySession(session);
        final Message message = chatServerMessage(user, "Disconnected!");
    
        broadcasterController.broadcastToSession(session, message);
        sessionsRepository.delete(user, session);
    }
    
    @OnError
    public void onError(Session session, Throwable throwable) {
        // Do error handling here
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
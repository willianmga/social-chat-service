package com.reactivechat.session;

import com.reactivechat.message.message.AuthenticateRequest;
import com.reactivechat.message.message.ReauthenticateRequest;
import com.reactivechat.message.message.SignupRequest;
import com.reactivechat.session.session.ChatSession;
import java.util.Optional;

public interface AuthenticationController {
    
    void handleAuthenticate(final AuthenticateRequest authenticateRequest, final ChatSession chatSession);
    void handleReauthenticate(final ReauthenticateRequest reauthenticateRequest, final ChatSession chatSession);
    void handleSignup(final SignupRequest signupRequest, final ChatSession chatSession);
    
    Optional<ChatSession> restoreSessionByToken(ChatSession incompleteSession,
                                                String token);
    
    void logoff(final ChatSession chatSession);
    
}

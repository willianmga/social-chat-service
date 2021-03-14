package com.reactivechat.controller;

import com.reactivechat.model.message.AuthenticateRequest;
import com.reactivechat.model.message.ReauthenticateRequest;
import com.reactivechat.model.message.SignupRequest;
import com.reactivechat.model.session.ChatSession;

public interface AuthenticationController {
    
    void handleAuthenticate(final AuthenticateRequest authenticateRequest, final ChatSession chatSession);
    void handleReauthenticate(final ReauthenticateRequest reauthenticateRequest, final ChatSession chatSession);
    void handleSignup(final SignupRequest signupRequest, final ChatSession chatSession);
    void logoff(final ChatSession chatSession);
    
}

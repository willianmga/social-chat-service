package com.reactivechat.controller;

import com.reactivechat.model.message.SignupRequest;
import com.reactivechat.model.message.AuthenticateRequest;
import javax.websocket.Session;

public interface AuthenticationController {
    
    void handleAuthenticate(final AuthenticateRequest authenticateRequest, final Session session);
    void handleSignup(final SignupRequest signupRequest, final Session session);
    void logoff(final Session session);
    boolean isAuthenticatedSession(final Session session, final String token);
    
}

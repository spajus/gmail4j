package com.googlecode.gmail4j.auth;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

public class GmailHttpAuthenticator extends Authenticator {
    
    private final Credentials credentials;
    
    public GmailHttpAuthenticator(final Credentials credentials) {
        this.credentials = credentials;
    }

    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
        if ("mail.google.com".equals(getRequestingHost())) {
            return new PasswordAuthentication(credentials.getUsername(), 
                    credentials.getPasword());
        }
        return null;
    }
    
}

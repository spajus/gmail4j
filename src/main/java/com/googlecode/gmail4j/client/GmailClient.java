package com.googlecode.gmail4j.client;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.googlecode.gmail4j.auth.Credentials;
import com.googlecode.gmail4j.message.GmailMessage;

public abstract class GmailClient {

    protected final Log log = LogFactory.getLog(getClass());
    
    protected Credentials loginCredentials;

    public GmailClient() {
        super();
    }

    public void setLoginCredentials(final Credentials loginCredentials) {
        this.loginCredentials = loginCredentials;
    }

    public void setLoginCredentials(final String username, final char[] password) {
        setLoginCredentials(new Credentials(username, password));
    }
    
    public abstract void init();
    
    public abstract List<GmailMessage> getUnreadMessages();

}
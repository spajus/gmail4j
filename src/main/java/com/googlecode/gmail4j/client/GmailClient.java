package com.googlecode.gmail4j.client;

import com.googlecode.gmail4j.auth.Credentials;

public class GmailClient {
    
    private Credentials loginCredentials;
    
    private String gmailFeedUrl = "https://mail.google.com/mail/feed/atom";

    public void setLoginCredentials(final Credentials loginCredentials) {
        this.loginCredentials = loginCredentials;
    }

    public void setLoginCredentials(final String username, final char[] password) {
        this.loginCredentials = new Credentials(username, password);
    }

    public String getGmailFeedUrl() {
        return gmailFeedUrl;
    }

    public void setGmailFeedUrl(final String gmailFeedUrl) {
        this.gmailFeedUrl = gmailFeedUrl;
    }
    
    
    

}
